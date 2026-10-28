
package com.shopsphere.service.impl;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Cart getOrCreateCart(Long userId) {
        validateUserId(userId);

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "User not found with ID: " + userId
                            ));

                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = findExistingCart(userId);
        return cartItemRepository.findAllByCartId(cart.getId());
    }

    @Override
    public CartItem addItem(
            Long userId,
            Long productId,
            Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        Cart cart = getOrCreateCart(userId);
        Product product = findActiveProduct(productId);

        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    return newItem;
                });

        int updatedQuantity = item.getQuantity() + quantity;

        if (updatedQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Cart quantity exceeds available stock"
            );
        }

        item.setQuantity(updatedQuantity);
        return cartItemRepository.save(item);
    }

    @Override
    public CartItem updateItemQuantity(
            Long userId,
            Long productId,
            Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        Cart cart = findExistingCart(userId);
        Product product = findActiveProduct(productId);

        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Requested quantity exceeds available stock"
            );
        }

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart item not found for product ID: " + productId
                ));

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Override
    public void removeItem(Long userId, Long productId) {
        validateProductId(productId);
        Cart cart = findExistingCart(userId);

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart item not found for product ID: " + productId
                ));

        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(Long userId) {
        Cart cart = findExistingCart(userId);
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

    private Cart findExistingCart(Long userId) {
        validateUserId(userId);

        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cart not found for user ID: " + userId
                ));
    }

    private Product findActiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found with ID: " + productId
                ));

        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new IllegalArgumentException(
                    "Product is not active: " + productId
            );
        }

        return product;
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID must be positive"
            );
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Product ID must be positive"
            );
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }
}
