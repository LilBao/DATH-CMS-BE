package com.cms.service.user;

import com.cms.dto.request.ChangePasswordRequest;

public interface UserService {
    void changePassword(String userId, ChangePasswordRequest request);
}
