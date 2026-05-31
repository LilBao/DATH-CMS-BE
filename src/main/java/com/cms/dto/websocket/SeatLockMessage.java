package com.cms.dto.websocket;

import lombok.Data;

/**
 * Message client gửi lên server để lock/unlock một ghế.
 */
@Data
public class SeatLockMessage {
    /** ID suất chiếu */
    private Integer timeId;

    /** Hàng ghế (1-based) */
    private Integer sRow;

    /** Cột ghế (1-based) */
    private Integer sColumn;

    /** "LOCK" hoặc "UNLOCK" */
    private String action;
}
