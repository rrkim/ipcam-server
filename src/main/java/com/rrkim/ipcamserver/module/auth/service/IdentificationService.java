package com.rrkim.ipcamserver.module.auth.service;

import com.rrkim.ipcamserver.core.auth.dto.CameraIdentity;
import com.rrkim.ipcamserver.core.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.core.device.DeviceIdProvider;
import com.rrkim.ipcamserver.core.file.service.FileService;
import com.rrkim.ipcamserver.core.utility.JsonUtility;
import com.rrkim.ipcamserver.core.utility.RSAKeyPairGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentificationService {

    private final DeviceIdProvider deviceIdProvider;
    private final FileService fileService;

    public void createCameraIdentity() {
        //AES
        try {
            String deviceId = deviceIdProvider.getDeviceId();
            RSAKeyPair rsaKeyPair = RSAKeyPairGenerator.createKeyPair();
            String privateKey = rsaKeyPair.getPrivateKey();
            String publicKey = rsaKeyPair.getPublicKey();

            CameraIdentity cameraIdentity = CameraIdentity.builder().deviceId(deviceId).credential(privateKey).build();
            String jsonString = JsonUtility.convertJson(cameraIdentity);

            // encode JSON string by base64
            Base64.Encoder encoder = Base64.getEncoder();
            String encodedTCI = encoder.encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

            fileService.saveFileByDataStream("keys/public.key", publicKey);
            fileService.saveFileByDataStream("keys/private.key", privateKey);
            fileService.saveFileByDataStream("keys/keypair.tci", encodedTCI);
        } catch (Exception e) {
            log.error("TCI 파일 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}
