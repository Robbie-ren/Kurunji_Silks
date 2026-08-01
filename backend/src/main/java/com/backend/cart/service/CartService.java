package com.backend.cart.service;

import com.backend.cart.dto.request.AddToCartRequest;
import com.backend.cart.dto.request.UpdateCartItemRequest;
import com.backend.cart.dto.response.CartItemResponse;
import com.backend.cart.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart();

    CartItemResponse addToCart(AddToCartRequest request);

    CartItemResponse updateCartItem(
            Long cartItemId,
            UpdateCartItemRequest request
    );

    void removeCartItem(Long cartItemId);

    void clearCart();
}