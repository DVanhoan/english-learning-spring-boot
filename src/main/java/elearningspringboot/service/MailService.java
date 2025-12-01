package elearningspringboot.service;

import elearningspringboot.entity.User;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface MailService {
    void sendConfirmLink(User recipient) throws MessagingException, UnsupportedEncodingException;

    void sendResetLink(User recipient) throws MessagingException, UnsupportedEncodingException;

}
