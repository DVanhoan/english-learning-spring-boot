package elearningspringboot.service.impl;

import elearningspringboot.entity.User;
import elearningspringboot.enumeration.TokenType;
import elearningspringboot.service.JwtService;
import elearningspringboot.service.MailService;
import elearningspringboot.service.WhitelistTokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final JwtService jwtService;
    private final WhitelistTokenService whitelistTokenService;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Override
    @Async
    public void sendConfirmLink(User recipient) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            Map<String, Object> properties = new HashMap<>();

            String confirmToken = jwtService.generateToken(recipient, TokenType.CONFIRM_TOKEN, 24);
            properties.put("confirmationUrl",
                    String.format("http://localhost:3000/verify-email?token=%s", confirmToken));
            properties.put("fullName", recipient.getFullName());
            properties.put("expiryHours", 24);
            context.setVariables(properties);

            String html = templateEngine.process("confirm-email", context);

            helper.setFrom(emailFrom, "K-English Education");
            helper.setTo(recipient.getEmail());
            helper.setSubject("Confirm email");
            helper.setText(html, true);

            javaMailSender.send(message);

            log.info("Confirmation email sent successfully to {}", recipient.getEmail());
        } catch (Exception e) {
            log.error("Failed to send confirmation email to {}: {}", recipient.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Async
    public void sendResetLink(User recipient) throws MessagingException, UnsupportedEncodingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            Map<String, Object> properties = new HashMap<>();

            String resetToken = jwtService.generateToken(recipient, TokenType.RESET_TOKEN, 1);
            System.out.println(resetToken);
            whitelistTokenService.createToken(resetToken, TokenType.RESET_TOKEN, recipient.getEmail());
            properties.put("resetUrl", String.format("http://localhost:3000/reset-password?token=%s", resetToken));
            properties.put("email", recipient.getEmail());
            properties.put("expiryHours", 1);
            context.setVariables(properties);

            String html = templateEngine.process("reset-password", context);

            helper.setFrom(emailFrom, "K-English Education");
            helper.setTo(recipient.getEmail());
            helper.setSubject("Reset password email");
            helper.setText(html, true);

            javaMailSender.send(message);

            log.info("Reset url sent successfully to {}", recipient.getEmail());
        } catch (Exception e) {
            log.error("Failed to send reset link to email {}: {}", recipient.getEmail(), e.getMessage());
            throw e;
        }
    }
}
