package com.rrkim.ipcamserver.module.cam.service;

import com.rrkim.ipcamserver.core.configuration.constant.DeviceConfiguration;
import com.rrkim.ipcamserver.core.configuration.service.DeviceConfigService;
import com.rrkim.ipcamserver.core.device.service.DeviceManagementService;
import com.rrkim.ipcamserver.core.utility.AesUtility;
import com.rrkim.ipcamserver.core.utility.RsaUtility;
import com.rrkim.ipcamserver.core.utility.ShaUtility;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class IPCamService {

    private OpenCVFrameGrabber grabber;
    private int cameraDevice = 0;
    private final DeviceConfigService deviceConfigService;
    private final DeviceManagementService deviceManagementService;
    private String deviceInitialized = null;

    @PostConstruct
    private void init() throws FrameGrabber.Exception {
        deviceInitialized = deviceConfigService.getConfigValue(DeviceConfiguration.INITIALIZED);
        if(deviceInitialized == null || deviceInitialized.isEmpty()) { return; }

        grabber = new OpenCVFrameGrabber(cameraDevice);
        grabber.start();
    }

    @PreDestroy
    private void destroy() throws FrameGrabber.Exception {
        if(deviceInitialized == null || deviceInitialized.isEmpty()) { return; }
        grabber.stop();
    }

    public void streamVideo(HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
        String privateKeyText = deviceManagementService.getDeviceId();
        String privateKey = ShaUtility.hash(privateKeyText, "").substring(0, 32);
        System.out.println("privateKey = " + privateKey);

        try {
            while (true) {
                Frame frame = grabber.grab();
                BufferedImage bufferedImage = convertToBufferedImage(frame);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                if(byteArrayOutputStream.size() == 0) { continue; }
                byte[] bytes = byteArrayOutputStream.toByteArray();

                String encryptedBytes = AesUtility.encodeAesCbc(bytes, privateKey);

                bufferedOutputStream.write(encryptedBytes.getBytes());
                bufferedOutputStream.write("\0".getBytes());
                bufferedOutputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage convertToBufferedImage(Frame frame) {
        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
        BufferedImage bufferedImage = paintConverter.convert(frame);
        paintConverter.close();

        return bufferedImage;
    }
}
