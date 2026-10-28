
package com.shopsphere.mapper;

import com.shopsphere.dto.response.CartItemResponse;
import com.shopsphere.dto.response.CartResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public final class CartMapper {

    private CartMapper() {
    }

    public static CartItemResponse toItemResponse(CartItem item) {
        BigDecimal subtotal = item.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .sku(item.getProduct().getSku())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(subtotal)
                .build();
    }

    public static CartResponse toCartResponse(
            Cart cart,
            List<CartItem> cartItems) {

        List<CartItemResponse> items = cartItems.stream()
                .map(CartMapper::toItemResponse)
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .build();
    }
}
