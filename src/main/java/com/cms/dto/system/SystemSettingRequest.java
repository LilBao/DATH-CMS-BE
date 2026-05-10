package com.cms.dto.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingRequest {
    private Boolean emailNotification;
    private Integer orderExpirationMinutes;
    private Boolean autoCancelEnabled;
    private Integer seatSyncInterval;
    private String primaryColor;
    private String currency;
    private String currencyFormat;
    private String cinemaName;
    private String cinemaAddress;
    private String cinemaPhone;
}
