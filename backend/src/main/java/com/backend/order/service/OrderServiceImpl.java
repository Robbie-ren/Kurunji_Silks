package com.backend.order.service;

import com.backend.auth.entity.User;
import com.backend.auth.repository.UserRepository;
import com.backend.cart.entity.CartItem;
import com.backend.cart.repository.CartItemRepository;
import com.backend.order.dto.request.AddressRequest;
import com.backend.order.dto.request.CreateOrderRequest;
import com.backend.order.dto.response.*;
import com.backend.order.entity.*;
import com.backend.order.enums.OrderStatus;
import com.backend.order.enums.PaymentStatus;
import com.backend.order.exception.EmptyCartException;
import com.backend.order.exception.InvalidOrderStatusException;
import com.backend.order.exception.OrderNotFoundException;
import com.backend.order.repository.*;
import com.backend.order.service.OrderService;
import com.backend.product.entity.Product;
import com.backend.product.repository.ProductRepository;
import com.backend.product.exception.InsufficientStockException;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final OrderStatusHistoryRepository historyRepository;

    // ============================================================
    // CURRENT USER
    // ============================================================

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Logged-in user not found"
                        )
                );
    }

    // ============================================================
    // CREATE ORDER
    // ============================================================

    @Override
    public OrderResponse createOrder(
            CreateOrderRequest request
    ) {

        User user = getCurrentUser();

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new EmptyCartException(
                    "Cannot create order because cart is empty"
            );
        }

        // --------------------------------------------------------
        // CREATE ADDRESS
        // --------------------------------------------------------

        AddressRequest addressRequest =
                request.getAddress();

        Address address = Address.builder()
                .user(user)
                .fullName(addressRequest.getFullName())
                .phone(addressRequest.getPhone())
                .addressLine(addressRequest.getAddressLine())
                .city(addressRequest.getCity())
                .state(addressRequest.getState())
                .pincode(addressRequest.getPincode())
                .landmark(addressRequest.getLandmark())
                .build();

        address = addressRepository.save(address);

        // --------------------------------------------------------
        // CREATE ORDER
        // --------------------------------------------------------

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .user(user)
                .address(address)
                .orderStatus(OrderStatus.PLACED)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        BigDecimal totalAmount = BigDecimal.ZERO;

        // --------------------------------------------------------
        // CREATE ORDER ITEMS
        // --------------------------------------------------------

        for (CartItem cartItem : cartItems) {

            Product product = productRepository.findByIdForUpdate(
                            cartItem.getProduct().getId()
                    )
                    .orElseThrow(() ->
                            new OrderNotFoundException(
                                    "Product not found with id: " + cartItem.getProduct().getId()
                            )
                    );

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new InvalidOrderStatusException(
                        "Product is no longer active: " + product.getName()
                );
            }

            if (cartItem.getQuantity() >
                    product.getStockQuantity()) {

                throw new InsufficientStockException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal unitPrice =
                    product.getDiscountPrice() != null
                            ? product.getDiscountPrice()
                            : product.getPrice();

            BigDecimal subtotal =
                    unitPrice.multiply(
                            BigDecimal.valueOf(
                                    cartItem.getQuantity()
                            )
                    );

            OrderItem orderItem =
                    OrderItem.builder()
                            .order(order)
                            .product(product)
                            .productName(product.getName())
                            .unitPrice(unitPrice)
                            .quantity(cartItem.getQuantity())
                            .subtotal(subtotal)
                            .build();

            orderItemRepository.save(orderItem);

            totalAmount =
                    totalAmount.add(subtotal);

            // Reduce stock
            product.setStockQuantity(
                    product.getStockQuantity()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        // --------------------------------------------------------
        // UPDATE TOTAL
        // --------------------------------------------------------

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // --------------------------------------------------------
        // CREATE PAYMENT
        // --------------------------------------------------------

        Payment payment = Payment.builder()
                .order(order)
                .amount(totalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);

        // --------------------------------------------------------
        // CREATE STATUS HISTORY
        // --------------------------------------------------------

        OrderStatusHistory history =
                OrderStatusHistory.builder()
                        .order(order)
                        .status(OrderStatus.PLACED)
                        .note("Order placed successfully")
                        .updatedBy(user)
                        .build();

        historyRepository.save(history);

        // --------------------------------------------------------
        // CLEAR CART
        // --------------------------------------------------------

        cartItemRepository.deleteByUserId(user.getId());

        return mapToResponse(order);
    }

    // ============================================================
    // GET ORDER BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {

        User user = getCurrentUser();

        Order order =
                orderRepository
                        .findByIdAndUserId(
                                id,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: " + id
                                )
                        );

        return mapToResponse(order);
    }

    // ============================================================
    // GET MY ORDERS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {

        User user = getCurrentUser();

        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // CANCEL ORDER
    // ============================================================

    @Override
    public void cancelOrder(Long id) {

        User user = getCurrentUser();

        Order order =
                orderRepository
                        .findByIdAndUserId(
                                id,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new OrderNotFoundException(
                                        "Order not found with id: " + id
                                )
                        );

        if (order.getOrderStatus() != OrderStatus.PLACED &&
                order.getOrderStatus() != OrderStatus.CONFIRMED) {

            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled in status: "
                            + order.getOrderStatus()
            );
        }

        List<OrderItem> items =
                orderItemRepository.findByOrderId(order.getId());

        // Return stock
        for (OrderItem item : items) {

            Product product = productRepository.findByIdForUpdate(
                            item.getProduct().getId()
                    )
                    .orElseThrow(() ->
                            new OrderNotFoundException(
                                    "Product not found with id: " + item.getProduct().getId()
                            )
                    );

            product.setStockQuantity(
                    product.getStockQuantity() + item.getQuantity()
            );

            productRepository.save(product);
        }

        order.setOrderStatus(
                OrderStatus.CANCELLED
        );

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        } else {
            order.setPaymentStatus(PaymentStatus.FAILED);
        }

        orderRepository.save(order);

        OrderStatusHistory history =
                OrderStatusHistory.builder()
                        .order(order)
                        .status(OrderStatus.CANCELLED)
                        .note("Order cancelled by customer")
                        .updatedBy(user)
                        .build();

        historyRepository.save(history);
    }

    // ============================================================
    // ORDER NUMBER
    // ============================================================

    private String generateOrderNumber() {

        return "ORD-"
                + LocalDateTime.now()
                .toString()
                .replaceAll("[^0-9]", "")
                .substring(0, 14)
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

    // ============================================================
    // MAPPING
    // ============================================================

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> items =
                orderItemRepository
                        .findByOrderId(order.getId())
                        .stream()
                        .map(item ->
                                OrderItemResponse.builder()
                                        .id(item.getId())
                                        .productId(
                                                item.getProduct().getId()
                                        )
                                        .productName(
                                                item.getProductName()
                                        )
                                        .productImage(
                                                item.getProductImage()
                                        )
                                        .unitPrice(
                                                item.getUnitPrice()
                                        )
                                        .quantity(
                                                item.getQuantity()
                                        )
                                        .subtotal(
                                                item.getSubtotal()
                                        )
                                        .build()
                        )
                        .toList();

        Address address = order.getAddress();

        AddressResponse addressResponse =
                AddressResponse.builder()
                        .id(address.getId())
                        .fullName(address.getFullName())
                        .phone(address.getPhone())
                        .addressLine(address.getAddressLine())
                        .city(address.getCity())
                        .state(address.getState())
                        .pincode(address.getPincode())
                        .landmark(address.getLandmark())
                        .build();

        PaymentResponse paymentResponse =
                paymentRepository
                        .findByOrderId(order.getId())
                        .map(payment ->
                                PaymentResponse.builder()
                                        .id(payment.getId())
                                        .amount(payment.getAmount())
                                        .paymentMethod(
                                                payment.getPaymentMethod()
                                        )
                                        .paymentStatus(
                                                payment.getPaymentStatus()
                                        )
                                        .transactionId(
                                                payment.getTransactionId()
                                        )
                                        .paidAt(
                                                payment.getPaidAt()
                                        )
                                        .build()
                        )
                        .orElse(null);

        List<OrderStatusHistoryResponse> history =
                historyRepository
                        .findByOrderIdOrderByCreatedAtAsc(
                                order.getId()
                        )
                        .stream()
                        .map(h ->
                                OrderStatusHistoryResponse.builder()
                                        .id(h.getId())
                                        .status(h.getStatus())
                                        .note(h.getNote())
                                        .createdAt(h.getCreatedAt())
                                        .build()
                        )
                        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderStatus(order.getOrderStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .totalAmount(order.getTotalAmount())
                .address(addressResponse)
                .items(items)
                .payment(paymentResponse)
                .statusHistory(history)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}