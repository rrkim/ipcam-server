package com.rrkim.ipcamserver.module.auth.service;

import com.rrkim.ipcamserver.core.configuration.constant.DeviceConfiguration;
import com.rrkim.ipcamserver.core.configuration.service.DeviceConfigService;
import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.utility.RsaUtility;
import com.rrkim.ipcamserver.core.utility.StringUtility;
import com.rrkim.ipcamserver.module.auth.dto.CameraIdentity;
import com.rrkim.ipcamserver.module.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.core.file.service.FileService;
import com.rrkim.ipcamserver.core.utility.AESKeyGenerator;
import com.rrkim.ipcamserver.core.utility.JsonUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
@Service
public class IdentificationService {

    private final DeviceManagementService deviceManagementService;
    private final DeviceConfigService deviceConfigService;
    private final FileService fileService;

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
            String encodedTci = StringUtility.encodeBase64(jsonString);

            fileService.saveFileByDataStream("keys/public.key", publicKey);
            fileService.saveFileByDataStream("keys/private.key", privateKey);
            fileService.saveFileByDataStream("keys/keypair.tci", encodedTci);

            //fileService.saveFileByDataStream(String.format("%s.tci", deviceId), cameraIdentityContent);
            //deviceConfigService.setConfigValue(DeviceConfiguration.PUBLIC_KEY, publicKey);
            //deviceConfigService.setConfigValue(DeviceConfiguration.INITIALIZED, String.valueOf(LocalDateTime.now()));
        } catch (Exception e) {
            log.error("TCI 파일 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
    }


    public byte[] getEncryptedSharedKey() {
        // get AES key, encrypt by public key and return
        try {
            String publicKeyString = fileService.readFileByDataStream("keys/public.key");
            System.out.println(publicKeyString);
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            Key publicKey = keyFactory.generatePublic(keySpec);


            SecretKey key = this.createSharedKey();
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            if (key != null) {
                return cipher.doFinal(key.getEncoded());
            }
        }
        catch (Exception e) {
            log.error("대칭 키 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }
        return null;
    }


    public SecretKey createSharedKey() throws Exception {
        // create new AES-256 key
        SecretKey key = AESKeyGenerator.getAESKey();

        if (key != null) {
            String keyString = Base64.getEncoder().encodeToString(key.getEncoded());
            fileService.saveFileByDataStream("keys/shared.key", keyString);
            return key;
        }

        return null;
    }
}
