package com.rrkim.ipcamserver.module.cam.controller;

import com.rrkim.ipcamserver.module.cam.service.IPCamService;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


@AllArgsConstructor
@Controller
public class IPCamController {

    private final IPCamService ipCamService;

    @GetMapping("/stream")
    public void stream(HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        ipCamService.streamVideo(response);
    }
}
