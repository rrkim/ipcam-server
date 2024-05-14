package com.rrkim.ipcamserver.core.auth.service;

import com.rrkim.ipcamserver.core.auth.dto.CameraIdentity;
import com.rrkim.ipcamserver.core.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.core.configuration.constant.DeviceConfiguration;
import com.rrkim.ipcamserver.core.configuration.service.DeviceConfigService;
import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.file.service.FileService;
import com.rrkim.ipcamserver.core.utility.JsonUtility;
import com.rrkim.ipcamserver.core.utility.RsaUtility;
import com.rrkim.ipcamserver.core.utility.StringUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentificationService {

    private final DeviceManagementService deviceManagementService;
    private final DeviceConfigService deviceConfigService;
    private final FileService fileService;

    public void createCameraIdentity() throws NoSuchAlgorithmException, IOException {
        String deviceId = deviceManagementService.getDeviceId();

        try {
            RSAKeyPair rsaKeyPair = RsaUtility.createKeyPair();
            String privateKey = rsaKeyPair.getPrivateKey();
            String publicKey = rsaKeyPair.getPublicKey();

            CameraIdentity cameraIdentity = CameraIdentity.builder().deviceId(deviceId).credential(privateKey).build();
            String cameraIdentityJson = JsonUtility.convertJson(cameraIdentity);
            String cameraIdentityContent = StringUtility.encodeBase64(cameraIdentityJson);

            fileService.saveFileByDataStream(String.format("%s.tci", deviceId), cameraIdentityContent);
            deviceConfigService.setConfigValue(DeviceConfiguration.PUBLIC_KEY, publicKey);
            deviceConfigService.setConfigValue(DeviceConfiguration.INITIALIZED, String.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            log.error("TCI 파일 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
            throw e;
        }
    }
}
