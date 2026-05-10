package com.cms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String customerName;
    private String customerAvatar;
    private Integer rating;
    private String comment;
    private LocalDate reviewDate;
}
