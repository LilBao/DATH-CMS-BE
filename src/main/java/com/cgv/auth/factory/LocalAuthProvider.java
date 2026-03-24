package com.cgv.auth.factory;

import com.cgv.auth.dto.JwtResponse;
import com.cgv.auth.dto.LoginRequest;
import com.cgv.common.enums.AuthProviderType;
import com.cgv.common.exception.AppException;
import com.cgv.entity.customer.Customer;
import com.cgv.entity.staff.Employee;
import com.cgv.repository.customer.CustomerRepository;
import com.cgv.repository.staff.EmployeeRepository;
import com.cgv.security.UserPrincipal;
import com.cgv.security.jwt.JwtTokenBlacklist;
import com.cgv.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ============================================================
 * LOCAL AUTH PROVIDER - Đăng nhập bằng Email/Password
 * ============================================================
 * Implements AuthProvider cho LOCAL authentication.
 * Tìm user trong Customer hoặc Employee table.
 * Verify password bằng BCrypt.
 * ============================================================
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalAuthProvider implements AuthProvider {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenBlacklist jwtTokenBlacklist;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    public AuthProviderType getType() {
        return AuthProviderType.LOCAL;
    }

    @Override
    public JwtResponse authenticate(LoginRequest request) {
        log.debug("LOCAL auth for email: {}", request.getEmail());

        // Validation
        if (request.getEmail() == null || request.getPassword() == null) {
            throw AppException.badRequest("Email and password are required");
        }

        // Tìm trong Customer
        Optional<Customer> customerOpt = customerRepository.findByEmail(request.getEmail());
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return authenticateCustomer(customer, request.getPassword());
        }

        // Tìm trong Employee
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(request.getEmail());
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            return authenticateEmployee(employee, request.getPassword());
        }

        throw AppException.unauthorized("Invalid email or password");
    }

    // ── Private helpers ─────────────────────────────────────────

    private JwtResponse authenticateCustomer(Customer customer, String rawPassword) {
        if (!customer.isActive()) {
            throw AppException.forbidden("Account is disabled");
        }
        if (!passwordEncoder.matches(rawPassword, customer.getEPassword())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        Map<String, Object> claims = buildClaims(customer.getCUserId(), customer.getUserType().name());
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

    private JwtResponse authenticateEmployee(Employee employee, String rawPassword) {
        if (!employee.isActive()) {
            throw AppException.forbidden("Account is disabled");
        }
        if (!passwordEncoder.matches(rawPassword, employee.getEPassword())) {
            throw AppException.unauthorized("Invalid email or password");
        }

        Map<String, Object> claims = buildClaims(employee.getEUserId(), employee.getUserType().name());
        String accessToken = jwtTokenProvider.generateAccessToken(employee.getEmail(), claims);
        String refreshToken = jwtTokenProvider.generateRefreshToken(employee.getEmail());

        jwtTokenBlacklist.storeRefreshToken(employee.getEUserId(), refreshToken, refreshExpirationMs);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtExpirationMs / 1000)
                .userId(employee.getEUserId())
                .email(employee.getEmail())
                .fullName(employee.getEName())
                .role(employee.getUserType().name())
                .build();
    }

    private Map<String, Object> buildClaims(String userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("provider", AuthProviderType.LOCAL.name());
        return claims;
    }
}
