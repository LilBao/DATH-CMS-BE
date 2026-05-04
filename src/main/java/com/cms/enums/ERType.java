package com.cms.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Getter
public enum ERType {
    STANDARD("Standard"),
    TWO_D("2D"),
    THR_D("3D"),
    IMAX("IMAX"),
    FOUR_DX("4DX");

    private final String label;

    public static ERType fromLabel(String label) {
        if (label == null || label.isEmpty()) return STANDARD;
        for (ERType type : ERType.values()) {
            if (type.label.equalsIgnoreCase(label) || type.name().equalsIgnoreCase(label)) {
                return type;
            }
        }
        return STANDARD;
    }

    @Converter(autoApply = true)
    public static class ERTypeConverter implements AttributeConverter<ERType, String> {
        @Override
        public String convertToDatabaseColumn(ERType attribute) {
            return attribute == null ? null : attribute.getLabel();
        }

        @Override
        public ERType convertToEntityAttribute(String dbData) {
            return ERType.fromLabel(dbData);
        }
    }
}