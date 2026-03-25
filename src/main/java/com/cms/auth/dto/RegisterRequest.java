package com.cms.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO cho đăng ký tài khoản mới
 */
@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    private String password;

    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$",
             message = "Invalid Vietnamese phone number")
    private String phoneNumber;

    /**
     * M = Male, F = Female
     */
    @Pattern(regexp = "^[MF]$", message = "Sex must be M or F")
    private String sex;
}
