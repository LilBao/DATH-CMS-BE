package com.cms.entity.cinema;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {

    @Id
    private Integer id = 1;

    @Column(name = "email_notification")
    private boolean emailNotification;

    @Column(name = "order_expiration_minutes")
    private int orderExpirationMinutes;

    @Column(name = "auto_cancel_enabled")
    private boolean autoCancelEnabled;

    @Column(name = "seat_sync_interval")
    private int seatSyncInterval;

    @Column(name = "primary_color", length = 20)
    private String primaryColor;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "currency_format", length = 20)
    private String currencyFormat;

    @Column(name = "cinema_name")
    private String cinemaName;

    @Column(name = "cinema_address")
    private String cinemaAddress;

    @Column(name = "cinema_phone")
    private String cinemaPhone;
}
