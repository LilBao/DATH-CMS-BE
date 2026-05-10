package com.cms.auth.service;

import com.cms.auth.dto.*;
import com.cms.auth.strategy.AuthStrategyContext;
import com.cms.common.enums.AuthProviderType;
import com.cms.common.enums.UserType;
import com.cms.common.exception.AppException;
import com.cms.entity.customer.Customer;
import com.cms.entity.customer.Membership;
import com.cms.enums.ERank;
import com.cms.enums.ESex;
import com.cms.repository.customer.CustomerRepository;
import com.cms.repository.customer.MembershipRepository;
import com.cms.security.jwt.JwtTokenBlacklist;
import com.cms.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthService - Điều phối các luồng xác thực.
 * Dùng AuthStrategyContext để chọn đúng strategy (Local, Google, Facebook...).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthStrategyContext authStrategyContext;
    private final CustomerRepository customerRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenBlacklist jwtTokenBlacklist;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;


    /**
     * Đăng nhập - delegate đến đúng AuthStrategy qua Context
     *
     * @param request LoginRequest với provider type
     * @return JwtResponse
     */
    public JwtResponse login(LoginRequest request) {
        AuthProviderType providerType = request.getProviderType();
        log.info("Login attempt with provider: {}", providerType);
        return authStrategyContext.getStrategy(providerType).authenticate(request);
    }


    /**
     * Đăng ký tài khoản Customer mới (LOCAL only)
     */
    @Transactional
    public JwtResponse register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại chưa
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw AppException.conflict("Email already registered: " + request.getEmail());
        }

        // Tạo Customer
        long count = customerRepository.count() + 1;
        String userId = String.format("CUS%03d", count);

        Customer customer = Customer.builder()
                .cUserId(userId)
                .cName(request.getFullName())
                .email(request.getEmail())
                .ePassword(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .sex(request.getSex() != null ?
                        ESex.valueOf(request.getSex()) : null)
                .userType(UserType.MEMBER)
                .authProvider(AuthProviderType.LOCAL)
                .isActive(true)
                .build();

        Customer saved = customerRepository.save(customer);

        // Tạo Membership mặc định (Bronze - rank 1)
        Membership membership = Membership.builder()
                .point(0)
                .memberRank(ERank.BRONZE)
                .customer(saved)
                .build();
        membershipRepository.save(membership);

        log.info("New customer registered: {} ({})", saved.getEmail(), saved.getCUserId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", saved.getCUserId());
        claims.put("role", saved.getUserType().name());
        claims.put("provider", AuthProviderType.LOCAL.name());

        String accessToken = jwtTokenProvider.generateAccessToken(saved.getEmail(), claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(saved.getEmail());
        jwtTokenBlacklist.storeRefreshToken(saved.getCUserId(), refreshToken, refreshExpirationMs);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpirationMs / 1000)
                .userId(saved.getCUserId())
                .email(saved.getEmail())
                .fullName(saved.getCName())
                .role(saved.getUserType().name())
                .build();
    }


    /**
     * Làm mới access token bằng refresh token
     */
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw AppException.unauthorized("Invalid or expired refresh token");
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw AppException.badRequest("Token is not a refresh token");
        }

        if (jwtTokenBlacklist.isBlacklisted(refreshToken)) {
            throw AppException.unauthorized("Refresh token has been revoked");
        }

        String email = jwtTokenProvider.getSubject(refreshToken);
        String userId = (String) jwtTokenProvider.getClaim(refreshToken, "userId");

        // Tạo access token mới
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        String newAccessToken = jwtTokenProvider.generateAccessToken(email, claims);

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)  // Giữ nguyên refresh token
                .expiresIn(jwtExpirationMs / 1000)
                .email(email)
                .build();
    }


    /**
     * Đăng xuất - blacklist cả access token và refresh token
     */
    public void logout(String accessToken, String userId) {
        // Blacklist access token
        jwtTokenBlacklist.blacklistToken(accessToken);

        // Xoá refresh token khỏi Redis
        if (userId != null) {
            jwtTokenBlacklist.deleteRefreshToken(userId);
        }

        log.info("User logged out: {}", userId);
    }
}
