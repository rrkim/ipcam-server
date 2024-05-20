package com.rrkim.ipcamserver.module.auth.controller;

import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.utility.FileUtility;
import com.rrkim.ipcamserver.module.auth.dto.SecureKey;
import com.rrkim.ipcamserver.module.auth.service.IdentificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequiredArgsConstructor
public class IdentificationController {
    private final IdentificationService identificationService;
    private final DeviceManagementService deviceManagementService;

    @GetMapping("setup")
    public void getPair(HttpServletResponse response) throws IOException {
        // make sure camera identity is present
        // create camera identity and generate key pair(.tci)

        // TODO: get key pair for multiple clients
        identificationService.createCameraIdentity();

        String deviceId = deviceManagementService.getDeviceId();


        DataInputStream bos = null;
        try {
            bos = FileUtility.getDataInputStream("keys/" + deviceId + ".tci");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        response.setHeader("Content-Type", "text/plain");
        if (bos == null) {
            return;
        }
        StreamUtils.copy(bos, response.getOutputStream());
    }

    @PostMapping("/auth/secure-key")
    public @ResponseBody SecureKey getSecureKey(@RequestBody String uuid) throws NoSuchAlgorithmException {
        String deviceId = deviceManagementService.getDeviceId();

        if(!deviceId.equals(uuid.strip())) {
            return null;
        }

        identificationService.createSymmetricKey();
        return identificationService.getSecureKey();
    }
}
