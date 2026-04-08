package com.cms.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
public enum ERType {
    STANDARD("Standard"),
    IMAX("IMAX"),
    FOUR_DX("4DX");

    private final String label;
}