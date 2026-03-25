package com.cms.common.enums;

/**
 * Phân loại người dùng trong hệ thống cms.
 * Khớp với cột UserType trong Customer.CUSTOMER và Staff.EMPLOYEE
 */
public enum UserType {
    // Customer roles
    GUEST,
    MEMBER,
    // Staff roles
    STAFF,
    MANAGER,
    ADMIN
}
