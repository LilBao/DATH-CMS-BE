package com.cms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EOrderStatus {
    PENDING,    // Đang chờ thanh toán
    PAID,       // Đã thanh toán
    CANCELLED,  // Đã huỷ
    REFUNDED    // Đã hoàn tiền
}