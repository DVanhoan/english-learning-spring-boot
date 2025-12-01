package elearningspringboot.service;

import elearningspringboot.dto.request.AddToCartRequest;
import elearningspringboot.dto.response.CartResponse;

public interface CartService {

    void addToCart(AddToCartRequest request);

    CartResponse getMyCart();

    void removeFromCart(Long courseId);

    int getCartItemCount();
}