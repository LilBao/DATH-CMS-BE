package com.cms.auth.dto;


@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String name;
    private String email;
    private String avatarUrl;
    private java.time.LocalDate birthday;
    private String role;
    private MembershipInfo membership;

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MembershipInfo {
        private String rank;
        private Integer points;
    }
}