package com.cms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchRequest {

    @NotBlank(message = "Branch name is required")
    @Size(max = 100)
    private String bName;

    @NotBlank(message = "Branch address is required")
    @Size(max = 200)
    private String bAddress;

    /** Employee ID of the manager */
    private String managerId;

    private List<String> phoneNumbers;
}
