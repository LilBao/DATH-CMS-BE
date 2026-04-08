package com.cms.dto.request;

import com.cms.common.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "Employee ID is required")
    @Size(max = 20)
    private String eUserId;

    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String eName;

    private String sex;

    @Size(max = 15)
    private String phoneNumber;

    @Email
    @Size(max = 100)
    private String email;

    /** Plain-text password - will be encoded in service */
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String ePassword;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salary;

    @NotNull(message = "User type is required")
    private UserType userType;

    @NotNull(message = "Branch ID is required")
    private Integer branchId;

    private String managerId;
}
