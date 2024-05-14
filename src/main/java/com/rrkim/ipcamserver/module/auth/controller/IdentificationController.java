package com.rrkim.ipcamserver.module.auth.controller;

import com.rrkim.ipcamserver.core.utility.FileUtility;
import com.rrkim.ipcamserver.module.auth.dto.SecureKey;
import com.rrkim.ipcamserver.module.auth.service.IdentificationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.DataInputStream;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class IdentificationController {
    private final IdentificationService identificationService;

    @GetMapping("setup")
    public void getPair(HttpServletResponse response) throws IOException {
        // make sure camera identity is present
        // create camera identity and generate key pair(.tci)

        // TODO: get key pair for multiple clients
        identificationService.createCameraIdentity();

        DataInputStream bos = null;
        try {
            bos = FileUtility.getDataInputStream("keys/keypair.tci");
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

    @GetMapping("/auth/secure-key")
    public @ResponseBody SecureKey getSecureKey() throws IOException {
        return identificationService.getSecureKey();
    }
}
