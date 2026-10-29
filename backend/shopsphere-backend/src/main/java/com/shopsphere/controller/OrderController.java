
package com.shopsphere.controller;

import com.shopsphere.dto.response.OrderItemResponse;
import com.shopsphere.dto.response.OrderResponse;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.User;
import com.shopsphere.mapper.OrderMapper;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Order order = orderService.checkout(user.getId());

        List<OrderItem> items =
                orderItemRepository.findAllByOrderId(order.getId());

        return OrderMapper.toOrderResponse(order, items);
    }

    @GetMapping
    public List<OrderResponse> getOrderHistory(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication);

        return orderService.getOrderHistory(user.getId())
                .stream()
                .map(order -> OrderMapper.toOrderResponse(
                        order,
                        orderItemRepository.findAllByOrderId(order.getId())
                ))
                .toList();
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(
            Authentication authentication,
            @PathVariable Long orderId) {

        User user = getAuthenticatedUser(authentication);
        Order order = orderService.getOrderById(user.getId(), orderId);

        List<OrderItem> items =
                orderItemRepository.findAllByOrderId(order.getId());

        return OrderMapper.toOrderResponse(order, items);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Authenticated user not found"
                ));
    }
}
