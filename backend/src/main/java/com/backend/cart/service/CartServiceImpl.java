package com.backend.cart.service;

import com.backend.auth.entity.User;
import com.backend.auth.repository.UserRepository;
import com.backend.cart.dto.request.AddToCartRequest;
import com.backend.cart.dto.request.UpdateCartItemRequest;
import com.backend.cart.dto.response.CartItemResponse;
import com.backend.cart.dto.response.CartResponse;
import com.backend.cart.entity.CartItem;
import com.backend.cart.exception.CartItemNotFoundException;
import com.backend.cart.repository.CartItemRepository;
import com.backend.cart.service.CartService;
import com.backend.product.entity.Product;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // ------------------------------------------------------------
    // GET CURRENT USER
    // ------------------------------------------------------------

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new CartItemNotFoundException(
                                "User not found"
                        )
                );
    }

    // ------------------------------------------------------------
    // GET CART
    // ------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {

        User user = getCurrentUser();

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        List<CartItemResponse> items =
                cartItems.stream()
                        .map(this::mapToResponse)
                        .toList();

        BigDecimal total =
                items.stream()
                        .map(CartItemResponse::getItemTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return CartResponse.builder()
                .items(items)
                .totalAmount(total)
                .build();
    }

    // ------------------------------------------------------------
    // ADD TO CART
    // ------------------------------------------------------------

    @Override
    public CartItemResponse addToCart(
            AddToCartRequest request
    ) {

        User user = getCurrentUser();

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Product not found with id: "
                                                + request.getProductId()
                                )
                        );

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new IllegalStateException(
                    "Product is not active"
            );
        }

        if (request.getQuantity() >
                product.getStockQuantity()) {

            throw new IllegalStateException(
                    "Insufficient stock"
            );
        }

        CartItem cartItem =
                cartItemRepository
                        .findByUserIdAndProductId(
                                user.getId(),
                                product.getId()
                        )
                        .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            if (newQuantity >
                    product.getStockQuantity()) {

                throw new IllegalStateException(
                        "Insufficient stock"
                );
            }

            cartItem.setQuantity(newQuantity);

        } else {

            cartItem = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
        }

        return mapToResponse(
                cartItemRepository.save(cartItem)
        );
    }

    // ------------------------------------------------------------
    // UPDATE CART ITEM
    // ------------------------------------------------------------

    @Override
    public CartItemResponse updateCartItem(
            Long cartItemId,
            UpdateCartItemRequest request
    ) {

        User user = getCurrentUser();

        CartItem cartItem =
                cartItemRepository
                        .findByIdAndUserId(
                                cartItemId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found with id: "
                                                + cartItemId
                                )
                        );

        Product product = cartItem.getProduct();

        if (request.getQuantity() >
                product.getStockQuantity()) {

            throw new IllegalStateException(
                    "Insufficient stock"
            );
        }

        cartItem.setQuantity(
                request.getQuantity()
        );

        return mapToResponse(
                cartItemRepository.save(cartItem)
        );
    }

    // ------------------------------------------------------------
    // REMOVE ITEM
    // ------------------------------------------------------------

    @Override
    public void removeCartItem(Long cartItemId) {

        User user = getCurrentUser();

        CartItem cartItem =
                cartItemRepository
                        .findByIdAndUserId(
                                cartItemId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Cart item not found with id: "
                                                + cartItemId
                                )
                        );

        cartItemRepository.delete(cartItem);
    }

    // ------------------------------------------------------------
    // CLEAR CART
    // ------------------------------------------------------------

    @Override
    public void clearCart() {

        User user = getCurrentUser();

        cartItemRepository.deleteByUserId(
                user.getId()
        );
    }

    // ------------------------------------------------------------
    // MAPPING
    // ------------------------------------------------------------

    private CartItemResponse mapToResponse(
            CartItem cartItem
    ) {

        Product product =
                cartItem.getProduct();

        BigDecimal unitPrice =
                product.getDiscountPrice() != null
                        ? product.getDiscountPrice()
                        : product.getPrice();

        BigDecimal itemTotal =
                unitPrice.multiply(
                        BigDecimal.valueOf(
                                cartItem.getQuantity()
                        )
                );

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .quantity(cartItem.getQuantity())
                .itemTotal(itemTotal)
                .build();
    }
}