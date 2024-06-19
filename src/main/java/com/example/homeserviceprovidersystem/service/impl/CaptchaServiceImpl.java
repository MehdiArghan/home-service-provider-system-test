package com.example.homeserviceprovidersystem.service.impl;

import com.example.homeserviceprovidersystem.customeException.CustomIoException;
import com.example.homeserviceprovidersystem.service.CaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Override
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) throws CustomIoException {
        try {
            String captchaText = generateCaptchaText();
            request.getSession().setAttribute("captcha", captchaText);
            response.setContentType("image/png");
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(generateImageCaptcha(captchaText));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new CustomIoException("Error generating captcha image");
        }
    }

    private String generateCaptchaText() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder captchaText = new StringBuilder();
        int captchaLength = 6;
        for (int i = 0; i < captchaLength; i++) {
            captchaText.append(chars.charAt(random.nextInt(chars.length())));
        }
        return captchaText.toString();
    }

    private byte[] generateImageCaptcha(String captchaText) throws CustomIoException {
        int width = 170;
        int height = 60;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setFont(new Font("Arial", Font.BOLD, 40));
        graphics.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graphics.setPaint(new GradientPaint(0, 0, Color.BLUE, 0, height / 2, Color.LIGHT_GRAY, true));
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(new Color(255, 153, 0));
        Random random = new Random();
        int x = 10 + random.nextInt(10);
        int y = 40 + random.nextInt(20);
        graphics.drawString(captchaText, x, y);
        graphics.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new CustomIoException("Error generating image captcha: ", e.getCause());
        }
    }
}