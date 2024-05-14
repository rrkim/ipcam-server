package com.rrkim.ipcamserver.module.auth.service;

import com.rrkim.ipcamserver.core.configuration.constant.DeviceConfiguration;
import com.rrkim.ipcamserver.core.configuration.service.DeviceConfigService;
import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.utility.*;
import com.rrkim.ipcamserver.module.auth.dto.CameraIdentity;
import com.rrkim.ipcamserver.module.auth.dto.SecureKey;
import com.rrkim.ipcamserver.module.auth.dto.RSAKeyPair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentificationService {

    private final DeviceManagementService deviceManagementService;
    private final DeviceConfigService deviceConfigService;

    @Getter
    private String symmetricKey;

    public void createCameraIdentity() {
        // RSA
        try {
            String deviceId = deviceManagementService.getDeviceId();
            RSAKeyPair rsaKeyPair = RsaUtility.createKeyPair();
            String privateKey = rsaKeyPair.getPrivateKey();
            String publicKey = rsaKeyPair.getPublicKey();

            CameraIdentity cameraIdentity = CameraIdentity.builder().deviceId(deviceId).credential(privateKey).build();
            String jsonString = JsonUtility.convertJson(cameraIdentity);

            // encode JSON string by base64
            String cameraIdentityContent = StringUtility.encodeBase64(jsonString);

            FileUtility.saveFileByDataStream(String.format("keys/%s.tci", deviceId), cameraIdentityContent);
            deviceConfigService.setConfigValue(DeviceConfiguration.PUBLIC_KEY, publicKey);
            deviceConfigService.setConfigValue(DeviceConfiguration.INITIALIZED, String.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            log.error("TCI 파일 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }


    public SecureKey getSecureKey() {
        // get AES key, encrypt by public key and return
        try {
            String publicKeyString = deviceConfigService.getConfigValue(DeviceConfiguration.PUBLIC_KEY);
            System.out.println("= symmetricKey : " + symmetricKey);

            String secureKeyString = RsaUtility.encryptData(symmetricKey, publicKeyString);
            return new SecureKey(secureKeyString, LocalDateTime.now());
        }
        catch (Exception e) {
            log.error("비밀키 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return null;
    }


    public void createSymmetricKey() throws NoSuchAlgorithmException {
        this.symmetricKey = AesUtility.getRandomAesKey();
    }

}
