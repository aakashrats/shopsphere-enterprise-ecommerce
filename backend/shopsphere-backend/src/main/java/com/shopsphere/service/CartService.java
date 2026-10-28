
package com.shopsphere.service;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;

import java.util.List;

public interface CartService {

    Cart getOrCreateCart(Long userId);

    List<CartItem> getCartItems(Long userId);

    CartItem addItem(Long userId, Long productId, Integer quantity);

    CartItem updateItemQuantity(
            Long userId,
            Long productId,
            Integer quantity
    );

    void removeItem(Long userId, Long productId);

    void clearCart(Long userId);
}
