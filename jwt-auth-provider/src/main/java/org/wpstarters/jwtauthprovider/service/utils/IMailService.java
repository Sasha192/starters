package org.wpstarters.jwtauthprovider.service.utils;

import org.springframework.core.io.FileSystemResource;

public interface IMailService {

    void send(String to, String subject, String plainText);

    void sendCode(String to, String subject, String code);

    void sendMimeMessage(String to,
                         String subject,
                         String html,
                         FileSystemResource... resources);

}
