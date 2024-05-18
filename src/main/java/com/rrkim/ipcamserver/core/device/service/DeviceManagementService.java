package com.rrkim.ipcamserver.core.device.service;

import com.rrkim.ipcamserver.core.configuration.constant.DeviceConfiguration;
import com.rrkim.ipcamserver.core.configuration.service.DeviceConfigService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceManagementService {

    private final DeviceConfigService deviceConfigService;
    private String deviceId;

    @PostConstruct
    private void init() {
        String deviceId = getDeviceId();
        log.info("Camera Device Id : " + deviceId);
    }

    public String getDeviceId()  {
        if(this.deviceId == null) {
            try {
                this.deviceId = readDeviceId();
            } catch (Exception ignored) {}
        }

        return this.deviceId;
    }

    private String readDeviceId() throws IOException {
        String deviceId = deviceConfigService.getConfigValue(DeviceConfiguration.DEVICE_ID);

        if(deviceId == null) {
            deviceId = createDeviceId();
        }

        return deviceId;
    }

    private String createDeviceId() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        deviceConfigService.setConfigValue(DeviceConfiguration.DEVICE_ID, deviceId);
        return deviceId;
    }
}
