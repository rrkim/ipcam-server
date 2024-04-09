package com.rrkim.ipcamserver.core.device;

import com.rrkim.ipcamserver.core.file.constant.FileName;
import com.rrkim.ipcamserver.core.file.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DeviceIdProvider {

    private final FileService fileService;
    private String deviceId;

    @PostConstruct
    private void init() throws IOException {
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
        String deviceId = fileService.readFileByDataStream(FileName.DEVICE_INFO);

        if(deviceId == null) {
            deviceId = createDeviceId();
        }

        return deviceId;
    }

    private String createDeviceId() throws IOException {
        String deviceId = UUID.randomUUID().toString();
        fileService.saveFileByDataStream(FileName.DEVICE_INFO, deviceId);
        return deviceId;
    }
}
