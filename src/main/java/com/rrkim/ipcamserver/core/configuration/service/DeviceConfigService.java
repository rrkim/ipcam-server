package com.rrkim.ipcamserver.core.configuration.service;

import com.rrkim.ipcamserver.core.configuration.entity.DeviceConfig;
import com.rrkim.ipcamserver.core.configuration.repository.DeviceConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DeviceConfigService {

    private final DeviceConfigRepository deviceConfigRepository;

    public String getConfigValue(String configId) {
        DeviceConfig deviceConfig = deviceConfigRepository.findByConfigId(configId);
        if(deviceConfig == null) { return null; }

        String configValue = deviceConfig.getConfigValue();
        if(configValue.isEmpty()) { return null; }

        return configValue;
    }

    public void setConfigValue(String configId, String configValue) {
        DeviceConfig checkDeviceConfig = deviceConfigRepository.findByConfigId(configId);
        if(checkDeviceConfig != null) { throw new RuntimeException("설정 값을 변경할 수 없습니다."); }

        DeviceConfig deviceConfig = DeviceConfig.builder().configId(configId).configValue(configValue).build();
        deviceConfigRepository.save(deviceConfig);
    }
}
