package com.cms.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message server broadcast tới tất cả client đang subscribe cùng suất chiếu.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusMessage {
    /** ID suất chiếu */
    private Integer timeId;

    /** Hàng ghế */
    private Integer sRow;

    /** Cột ghế */
    private Integer sColumn;

    /**
     * Trạng thái WS hiện tại:
     *  - "LOCKED"   — đang được ai đó chọn
     *  - "UNLOCKED" — vừa được thả ra
     */
    private String status;
}
