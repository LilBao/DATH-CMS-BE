package com.cms.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token Provider - Tạo, parse và validate JWT tokens.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${app.jwt.issuer}")
    private String issuer;


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Tạo Access Token từ subject (email) và claims tuỳ chỉnh
     */
    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Tạo Access Token (overload đơn giản)
     */
    public String generateAccessToken(String subject) {
        return generateAccessToken(subject, new HashMap<>());
    }

    /**
     * Tạo Refresh Token - dài hơn access token
     */
    public String generateRefreshToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .issuer(issuer)
                .subject(subject)
                .claim("type", "refresh")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }


    /**
     * Lấy Claims từ token
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Lấy subject (email/userId) từ token
     */
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Lấy thời gian hết hạn
     */
    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    /**
     * Lấy claim tuỳ chỉnh
     */
    public Object getClaim(String token, String claimKey) {
        return parseClaims(token).get(claimKey);
    }

    // ── Token Validation ─────────────────────────────────────────

    /**
     * Validate token - trả về true nếu hợp lệ, false nếu không
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("JWT token unsupported: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("JWT token malformed: {}", ex.getMessage());
        } catch (SecurityException ex) {
            log.warn("JWT signature invalid: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT token empty claims: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Kiểm tra token có phải refresh token không
     */
    public boolean isRefreshToken(String token) {
        try {
            Object type = getClaim(token, "type");
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Lấy thời gian còn lại của token (ms)
     */
    public long getRemainingTimeMs(String token) {
        Date expiration = getExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }
}
