package com.cms.service.system;

import com.cms.dto.system.SystemSettingRequest;
import com.cms.dto.system.SystemSettingResponse;

public interface SystemSettingService {
    SystemSettingResponse getSettings();
    SystemSettingResponse updateSettings(SystemSettingRequest request);
}
