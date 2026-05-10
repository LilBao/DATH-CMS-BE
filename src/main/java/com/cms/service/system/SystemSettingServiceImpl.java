package com.cms.service.system;

import com.cms.dto.system.SystemSettingRequest;
import com.cms.dto.system.SystemSettingResponse;
import com.cms.entity.cinema.SystemSetting;
import com.cms.repository.cinema.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository repository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public SystemSettingResponse getSettings() {
        SystemSetting settings = repository.findById(1)
                .orElseGet(this::createDefaultSettings);
        return modelMapper.map(settings, SystemSettingResponse.class);
    }

    @Override
    public SystemSettingResponse updateSettings(SystemSettingRequest request) {
        SystemSetting settings = repository.findById(1)
                .orElseGet(this::createDefaultSettings);

        if (request.getEmailNotification() != null) settings.setEmailNotification(request.getEmailNotification());
        if (request.getOrderExpirationMinutes() != null) settings.setOrderExpirationMinutes(request.getOrderExpirationMinutes());
        if (request.getAutoCancelEnabled() != null) settings.setAutoCancelEnabled(request.getAutoCancelEnabled());
        if (request.getSeatSyncInterval() != null) settings.setSeatSyncInterval(request.getSeatSyncInterval());
        if (request.getPrimaryColor() != null) settings.setPrimaryColor(request.getPrimaryColor());
        if (request.getCurrency() != null) settings.setCurrency(request.getCurrency());
        if (request.getCurrencyFormat() != null) settings.setCurrencyFormat(request.getCurrencyFormat());
        if (request.getCinemaName() != null) settings.setCinemaName(request.getCinemaName());
        if (request.getCinemaAddress() != null) settings.setCinemaAddress(request.getCinemaAddress());
        if (request.getCinemaPhone() != null) settings.setCinemaPhone(request.getCinemaPhone());

        return modelMapper.map(repository.save(settings), SystemSettingResponse.class);
    }

    private SystemSetting createDefaultSettings() {
        SystemSetting defaultSettings = SystemSetting.builder()
                .id(1)
                .emailNotification(true)
                .orderExpirationMinutes(15)
                .autoCancelEnabled(true)
                .seatSyncInterval(5)
                .primaryColor("#4a4bd7")
                .currency("VND")
                .currencyFormat("vi-VN")
                .cinemaName("Antigravity Cinema")
                .cinemaAddress("Ho Chi Minh City, Vietnam")
                .cinemaPhone("0123456789")
                .build();
        return repository.save(defaultSettings);
    }
}
