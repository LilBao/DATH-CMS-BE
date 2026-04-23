package com.cms.auth.strategy;

import com.cms.auth.dto.JwtResponse;
import com.cms.auth.dto.LoginRequest;
import com.cms.common.enums.AuthProviderType;
import com.cms.common.enums.UserType;
import com.cms.common.exception.AppException;
import com.cms.entity.customer.Customer;
import com.cms.entity.customer.Membership;
import com.cms.enums.ERank;
import com.cms.repository.customer.CustomerRepository;
import com.cms.repository.customer.MembershipRepository;
import com.cms.security.jwt.JwtTokenBlacklist;
import com.cms.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * GOOGLE AUTH STRATEGY - Đăng nhập qua Google OAuth2
 * ============================================================
 * Flow:
 *   1. Frontend lấy Google ID Token (từ Google Sign-In SDK)
 *   2. Gửi ID Token đến backend trong LoginRequest.idToken
 *   3. Backend verify token với Google API
 *   4. Extract email, name, picture từ Google payload
 *   5. Tạo/cập nhật Customer account → Tạo JWT
 *
 * Implements AuthStrategy → tích hợp Strategy Pattern
 * ============================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleAuthStrategy implements AuthStrategy {

    private static final String GOOGLE_TOKEN_INFO_URL =
            "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private final CustomerRepository customerRepository;
    private final MembershipRepository membershipRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenBlacklist jwtTokenBlacklist;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    public AuthProviderType getType() {
        return AuthProviderType.GOOGLE;
    }

    @Override
    @Transactional
    public JwtResponse authenticate(LoginRequest request) {
        log.debug("GOOGLE auth with idToken");

        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            throw AppException.badRequest("Google ID token is required");
        }

        // Verify Google ID Token
        GoogleUserInfo googleUser = verifyGoogleToken(request.getIdToken());

        // Tìm hoặc tạo Customer
        Customer customer = findOrCreateGoogleCustomer(googleUser);

        // Tạo JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", customer.getCUserId());
        claims.put("role", customer.getUserType().name());
        claims.put("provider", AuthProviderType.GOOGLE.name());

        String accessToken = jwtTokenProvider.generateAccessToken(customer.getEmail(), claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(customer.getEmail());

        jwtTokenBlacklist.storeRefreshToken(customer.getCUserId(), refreshToken, refreshExpirationMs);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpirationMs / 1000)
                .userId(customer.getCUserId())
                .email(customer.getEmail())
                .fullName(customer.getCName())
                .role(customer.getUserType().name())
                .avatarUrl(customer.getAvatarUrl())
                .build();
    }

    // ── Private helpers ─────────────────────────────────────────

    /**
     * Verify Google ID Token bằng Google tokeninfo endpoint
     * Production: nên dùng google-auth-library để verify locally
     */
    @SuppressWarnings("unchecked")
    private GoogleUserInfo verifyGoogleToken(String idToken) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> payload = restTemplate.getForObject(
                    GOOGLE_TOKEN_INFO_URL + idToken,
                    Map.class
            );

            if (payload == null || payload.containsKey("error_description")) {
                throw AppException.unauthorized("Invalid Google token");
            }

            return GoogleUserInfo.builder()
                    .sub((String) payload.get("sub"))
                    .email((String) payload.get("email"))
                    .name((String) payload.get("name"))
                    .picture((String) payload.get("picture"))
                    .emailVerified("true".equals(payload.get("email_verified")))
                    .build();

        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify Google token: {}", e.getMessage());
            throw AppException.unauthorized("Google token verification failed");
        }
    }

    /**
     * Tìm hoặc tạo Customer từ Google user info
     * Auto-register nếu chưa có tài khoản
     */
    private Customer findOrCreateGoogleCustomer(GoogleUserInfo googleUser) {
        Optional<Customer> existing = customerRepository.findByEmail(googleUser.getEmail());

        if (existing.isPresent()) {
            Customer customer = existing.get();
            if (googleUser.getPicture() != null &&
                    !googleUser.getPicture().equals(customer.getAvatarUrl())) {
                customer.setAvatarUrl(googleUser.getPicture());
                customerRepository.save(customer);
            }
            return customer;
        }

        Customer newCustomer = Customer.builder()
                .cName(googleUser.getName())
                .email(googleUser.getEmail())
                .userType(UserType.MEMBER)
                .authProvider(AuthProviderType.GOOGLE)
                .providerId(googleUser.getSub())
                .avatarUrl(googleUser.getPicture())
                .isActive(true)
                .build();

        long count = customerRepository.count() + 1;
        newCustomer.setCUserId(String.format("CUS%03d", count));

        Customer saved = customerRepository.save(newCustomer);

        Membership membership = Membership.builder()
                .point(0)
                .memberRank(ERank.BRONZE)
                .customer(saved)
                .build();
        membershipRepository.save(membership);

        log.info("New Google user registered: {}", saved.getEmail());
        return saved;
    }


    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    static class GoogleUserInfo {
        private String sub;          // Google user ID
        private String email;
        private String name;
        private String picture;
        private boolean emailVerified;
    }
}
