package com.rrkim.ipcamserver.core.auth.service;

import com.rrkim.ipcamserver.core.auth.dto.CameraIdentity;
import com.rrkim.ipcamserver.core.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.core.device.DeviceIdProvider;
import com.rrkim.ipcamserver.core.file.service.FileService;
import com.rrkim.ipcamserver.core.utility.JsonUtility;
import com.rrkim.ipcamserver.core.utility.RSAKeyPairGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

            // TODO: encrypt jsonString, save .tci file and download .tci file to user
            fileService.saveFileByDataStream("public.key", publicKey);
        } catch (Exception e) {
            log.error("TCI 파일 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }
}
