package elearningspringboot.service;

import elearningspringboot.dto.request.CreatePaymentRequest;
import elearningspringboot.dto.response.CreatePaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface PaymentService {

    CreatePaymentResponse createVnPayPayment(CreatePaymentRequest request, HttpServletRequest httpServletRequest)
            throws UnsupportedEncodingException;

    String handleVnPayIPN(Map<String, String> vnpayParams);
}