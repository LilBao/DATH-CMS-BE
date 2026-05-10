package com.cms.controller;

import com.cms.dto.system.SystemSettingRequest;
import com.cms.dto.system.SystemSettingResponse;
import com.cms.service.system.SystemSettingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${server.api-prefix}/system/settings")
@RequiredArgsConstructor
@Tag(name = "System Settings", description = "Các API quản lý cấu hình hệ thống")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    public ResponseEntity<SystemSettingResponse> getSettings() {
        return ResponseEntity.ok(systemSettingService.getSettings());
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SystemSettingResponse> updateSettings(@RequestBody SystemSettingRequest request) {
        return ResponseEntity.ok(systemSettingService.updateSettings(request));
    }
}
