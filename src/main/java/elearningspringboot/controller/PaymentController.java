package elearningspringboot.controller;

import elearningspringboot.dto.request.CreatePaymentRequest;
import elearningspringboot.dto.response.CreatePaymentResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.PaymentService;
import elearningspringboot.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final MessageSource messageSource;

    @PostMapping("/create-payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResponseData<CreatePaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request, HttpServletRequest httpServletRequest)
            throws UnsupportedEncodingException {

        CreatePaymentResponse response = paymentService.createVnPayPayment(request, httpServletRequest);
        String message = messageSource.getMessage("payment.create.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<String> handleVnPayIPN(@RequestParam Map<String, String> vnpayParams) {
        log.info("VNPAY IPN Received with params: {}", vnpayParams);
        String rspCode = paymentService.handleVnPayIPN(vnpayParams);

        if ("00".equals(rspCode)) {
            return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        } else {
            return ResponseEntity.ok("{\"RspCode\":\"" + rspCode + "\",\"Message\":\"Confirm Fail\"}");
        }
    }
}