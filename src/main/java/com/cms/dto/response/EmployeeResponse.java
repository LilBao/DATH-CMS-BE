package com.cms.dto.response;

import com.cms.common.enums.UserType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private String eUserId;
    private String eName;
    private String sex;
    private String phoneNumber;
    private String email;
    private BigDecimal salary;
    private UserType userType;
    private boolean isActive;
    private Integer branchId;
    private String branchName;
    private String managerId;
    private String managerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
