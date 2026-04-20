package com.cms.config;

import com.cms.security.UserPrincipalService;
import com.cms.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserPrincipalService userPrincipalService;

    private static final String API_V1 = "/api/v1";

    // ── Public Endpoints (no auth needed) ───────────────────────
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",
            "/actuator/health",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            API_V1 + "/payments/callback",
            API_V1 + "/payments/subscribe"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF (dùng JWT thay thế)
                .csrf(AbstractHttpConfigurer::disable)

                // Cấu hình CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Stateless session (JWT based)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        // ===== Public GET APIs =====
                        // Cho phép xem danh sách/chi tiết phim, suất chiếu, chi nhánh, menu, danh mục mà không cần đăng nhập
                        .requestMatchers(HttpMethod.GET, 
                                API_V1 + "/movies/**", 
                                API_V1 + "/showtimes/**", 
                                API_V1 + "/branches/**", 
                                API_V1 + "/food-drinks/**", 
                                API_V1 + "/catalog/**").permitAll()

                        // ===== Files =====
                        .requestMatchers(API_V1 + "/files/**").hasAnyRole("STAFF", "MANAGER", "ADMIN")

                        // ===== Orders & Payments =====
                        // Bất kỳ ai đăng nhập đều có thể xem lịch sử, tạo đơn, tạo payment
                        .requestMatchers(HttpMethod.POST, API_V1 + "/orders/**", API_V1 + "/payments/**").authenticated()
                        .requestMatchers(HttpMethod.GET, API_V1 + "/orders/**").authenticated()

                        // ===== Management APIs (MANAGER, ADMIN) =====
                        // Quản lý các module cơ bản: Phim, suất chiếu, chi nhánh, kho bắp nước, danh mục,...
                        .requestMatchers(API_V1 + "/movies/**", 
                                API_V1 + "/showtimes/**", 
                                API_V1 + "/branches/**", 
                                API_V1 + "/food-drinks/**", 
                                API_V1 + "/catalog/**",
                                API_V1 + "/customers/**").hasAnyRole("MANAGER", "ADMIN")

                        // ===== Admin only =====
                        // Quản lý nhân sự
                        .requestMatchers(API_V1 + "/employees/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                // Đăng ký JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // Xác thực provider
                .authenticationProvider(authenticationProvider());

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:3000",        // Frontend customer
                "http://localhost:3001",        // Frontend dashboard
        ));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept",
                "X-Requested-With", "Cache-Control"
        ));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userPrincipalService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
