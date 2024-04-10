package com.rrkim.ipcamserver.module.cam.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Service
public class IPCamService {

    private OpenCVFrameGrabber grabber;
    private int cameraDevice = 0;

    @PostConstruct
    private void init() throws FrameGrabber.Exception {
        grabber = new OpenCVFrameGrabber(cameraDevice);
        grabber.start();
    }

    @PreDestroy
    private void destroy() throws FrameGrabber.Exception {
        grabber.stop();
    }

    public void streamVideo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "multipart/x-mixed-replace; boundary=--BoundaryString");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());

        try {
            while (true) {
                Frame frame = grabber.grab();
                BufferedImage bufferedImage = convertToBufferedImage(frame);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                if(byteArrayOutputStream.size() == 0) { continue; }
                byte[] bytes = byteArrayOutputStream.toByteArray();

                bufferedOutputStream.write("--BoundaryString\r\n".getBytes());
                bufferedOutputStream.write("Content-Type: image/jpeg\r\n".getBytes());
                bufferedOutputStream.write(("Content-Length: " + bytes.length + "\r\n").getBytes());
                bufferedOutputStream.write("\r\n".getBytes());
                bufferedOutputStream.write(bytes);
                bufferedOutputStream.write("\r\n".getBytes());
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
