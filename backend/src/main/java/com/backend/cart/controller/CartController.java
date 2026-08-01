package com.backend.cart.controller;

import com.backend.cart.dto.request.AddToCartRequest;
import com.backend.cart.dto.request.UpdateCartItemRequest;
import com.backend.cart.dto.response.CartItemResponse;
import com.backend.cart.dto.response.CartResponse;
import com.backend.cart.service.CartService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // GET CART
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        return ResponseEntity.ok(
                cartService.getCart()
        );
    }

    // ADD PRODUCT TO CART
    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        cartService.addToCart(request)
                );
    }

    // UPDATE CART ITEM
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {

        return ResponseEntity.ok(
                cartService.updateCartItem(
                        cartItemId,
                        request
                )
        );
    }

    // REMOVE CART ITEM
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long cartItemId
    ) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.noContent().build();
    }

    // CLEAR CART
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {

        cartService.clearCart();

        return ResponseEntity.noContent().build();
    }
}