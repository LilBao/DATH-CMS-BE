package com.cms.dto.response;

import com.cms.common.enums.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private String cUserId;
    private String cName;
    private String sex;
    private String phoneNumber;
    private String email;
    private UserType userType;
    private String authProvider;
    private String avatarUrl;
    @JsonProperty("isActive")
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Membership info
    private String membershipTier;
    private Integer totalPoints;
}
