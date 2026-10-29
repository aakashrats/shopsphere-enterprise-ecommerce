
package com.shopsphere.service.impl;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Order checkout(Long userId) {
        validatePositiveId(userId, "User ID");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with ID: " + userId
                ));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart not found for user ID: " + userId
                ));

        List<CartItem> cartItems =
                cartItemRepository.findAllByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("ORD-" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 20)
                .toUpperCase());
        order.setStatus("PLACED");

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Long productId = cartItem.getProduct().getId();

            Product product = productRepository.findByIdForUpdate(productId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with ID: " + productId
                    ));

            if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                throw new IllegalArgumentException(
                        "Product is not active: " + productId
                );
            }

            int quantity = cartItem.getQuantity();

            if (quantity <= 0 || product.getStockQuantity() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock for product: " + product.getName()
                );
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(quantity)
            );

            product.setStockQuantity(product.getStockQuantity() - quantity);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductName(product.getName());
            orderItem.setSku(product.getSku());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        for (OrderItem orderItem : orderItems) {
            orderItemRepository.save(orderItem);
        }

        cartItemRepository.deleteAllByCartId(cart.getId());

        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long userId, Long orderId) {
        validatePositiveId(userId, "User ID");
        validatePositiveId(orderId, "Order ID");

        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found with ID: " + orderId
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrderHistory(Long userId) {
        validatePositiveId(userId, "User ID");

        return orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    private void validatePositiveId(Long id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " must be positive"
            );
        }
    }
}
