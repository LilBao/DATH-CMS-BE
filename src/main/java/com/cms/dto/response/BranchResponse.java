package com.cms.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    private Integer branchId;
    private String bName;
    private String bAddress;
    private String managerName;
    private String managerId;
    private List<String> phoneNumbers;
    private Integer totalRooms;
    private Boolean isActive;
}
