package elearningspringboot.controller;

import elearningspringboot.dto.request.AddToCartRequest;
import elearningspringboot.dto.response.CartResponse;
import elearningspringboot.dto.response.ResponseData;
import elearningspringboot.service.CartService;
import elearningspringboot.util.ResponseBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
@PreAuthorize("isAuthenticated()")
public class CartController {

    private final CartService cartService;
    private final MessageSource messageSource;

    @PostMapping("/add")
    public ResponseEntity<ResponseData<Void>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        cartService.addToCart(request);
        String message = messageSource.getMessage("cart.add.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.CREATED, message);
    }

    @GetMapping("/me")
    public ResponseEntity<ResponseData<CartResponse>> getMyCart() {
        CartResponse response = cartService.getMyCart();
        String message = messageSource.getMessage("cart.get.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, response);
    }

    @DeleteMapping("/remove/{courseId}")
    public ResponseEntity<ResponseData<Void>> removeFromCart(
            @PathVariable @Min(1) Long courseId) {
        cartService.removeFromCart(courseId);
        String message = messageSource.getMessage("cart.remove.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.noData(HttpStatus.OK, message);
    }

    @GetMapping("/count")
    public ResponseEntity<ResponseData<Integer>> getCartItemCount() {
        int count = cartService.getCartItemCount();
        String message = messageSource.getMessage("cart.count.success", null, LocaleContextHolder.getLocale());
        return ResponseBuilder.withData(HttpStatus.OK, message, count);
    }
}