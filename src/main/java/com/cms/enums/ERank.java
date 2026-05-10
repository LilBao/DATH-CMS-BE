package com.cms.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
public enum ERank {
    NONE(0),
    BRONZE(1),
    SILVER(2),
    GOLD(3),
    DIAMOND(4);

    private final Integer label;
}
