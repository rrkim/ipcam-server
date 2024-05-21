package com.rrkim.ipcamserver.module.auth.controller;

import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.utility.ApiUtility;
import com.rrkim.ipcamserver.core.utility.FileUtility;
import com.rrkim.ipcamserver.module.auth.dto.SecureKey;
import com.rrkim.ipcamserver.module.auth.dto.SecureKeyRequestDto;
import com.rrkim.ipcamserver.module.auth.service.IdentificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class IdentificationController {
    private final IdentificationService identificationService;
    private final DeviceManagementService deviceManagementService;

    @GetMapping("setup")
    public void getPair(HttpServletResponse response) throws IOException {
        identificationService.writeResponseCreateCameraIdentity(response);
    }

    @PostMapping("/auth/secure-key")
    public @ResponseBody Map<String, Object> getSecureKey(@RequestBody(required = false) SecureKeyRequestDto secureKeyRequestDto) throws NoSuchAlgorithmException {
        if (!identificationService.checkSecureKeyRequest(secureKeyRequestDto)) {
            return ApiUtility.getMessageResponse("올바른 장치 ID (deviceId)가 필요합니다.");
        }

        SecureKey secureKey = identificationService.createSecureKey();
        return ApiUtility.getDataResponse(secureKey);
    }
}
