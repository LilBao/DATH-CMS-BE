package com.cms.dto.request;

import com.cms.enums.ESex;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    @NotBlank(message = "Tên không được để trống")
    private String name;
    
    private ESex sex;
    
    private LocalDate birthday;
    
    private String phoneNumber;
    
    private String avatarUrl;
}
