package org.wpstarters.jwtauthprovider.service.utils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class MailService implements IMailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String to, String subject, String plainText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(plainText);
        mailSender.send(message);
    }

    @Override
    public void sendCode(String to, String subject, String code) {
        send(to, subject, code);
    }

    @Override
    public void sendMimeMessage(String to, String subject,
                                String text,
                                FileSystemResource... resources) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            int i = 0;
            for (FileSystemResource resource: resources) {
                String attachmentName = resource.getFilename();
                if (attachmentName == null) {
                    attachmentName = "Attachment %d".formatted(i++);
                }
                helper.addAttachment(attachmentName, resource);
            }
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Error while sending email to %s".formatted(to), e);
            throw new RuntimeException(e);
        }
    }
}
