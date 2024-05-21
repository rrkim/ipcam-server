package com.rrkim.ipcamserver.module.auth.service;

import com.rrkim.ipcamserver.core.configuration.constant.DeviceConfiguration;
import com.rrkim.ipcamserver.core.configuration.service.DeviceConfigService;
import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.utility.*;
import com.rrkim.ipcamserver.module.auth.dto.CameraIdentity;
import com.rrkim.ipcamserver.module.auth.dto.SecureKey;
import com.rrkim.ipcamserver.module.auth.dto.RSAKeyPair;
import com.rrkim.ipcamserver.module.auth.dto.SecureKeyRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public String createCameraIdentity() {
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
            deviceConfigService.setConfigValue(DeviceConfiguration.PUBLIC_KEY, publicKey);
            deviceConfigService.setConfigValue(DeviceConfiguration.INITIALIZED, String.valueOf(LocalDateTime.now()));
            return cameraIdentityContent;
        } catch (Exception e) {
            log.error("TCI 파일 생성 중 오류가 발생했습니다.");
            e.printStackTrace();
        }

        return null;
    }

    public void writeResponseCreateCameraIdentity(HttpServletResponse response) throws IOException {
        String deviceId = deviceManagementService.getDeviceId();
        String cameraIdentity = createCameraIdentity();

        PrintWriter printWriter = response.getWriter();

        response.setHeader("Content-Type", "text/plain");
        String docName = URLEncoder.encode(String.format("%s.tci", deviceId), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + docName + ";");
        response.setContentType("text/plain");

        printWriter.print(cameraIdentity);
        response.flushBuffer();
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

    public boolean checkSecureKeyRequest(SecureKeyRequestDto secureKeyRequestDto) {
        System.out.println("secureKeyRequestDto = " + secureKeyRequestDto);
        if(secureKeyRequestDto == null || secureKeyRequestDto.getDeviceId() == null || secureKeyRequestDto.getDeviceId().isEmpty()) {
            return false;
        }

        String currentDeviceId = deviceManagementService.getDeviceId();
        String requestDeviceId = secureKeyRequestDto.getDeviceId();
        System.out.println("currentDeviceId = " + currentDeviceId);
        System.out.println("requestDeviceId = " + requestDeviceId);

        return currentDeviceId.equals(requestDeviceId);
    }

    public SecureKey createSecureKey() throws NoSuchAlgorithmException {
        createSymmetricKey();
        return getSecureKey();
    }
}
