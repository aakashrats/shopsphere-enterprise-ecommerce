
package com.shopsphere.controller;

import com.shopsphere.dto.request.AddCartItemRequest;
import com.shopsphere.dto.request.UpdateCartItemRequest;
import com.shopsphere.dto.response.CartItemResponse;
import com.shopsphere.dto.response.CartResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.User;
import com.shopsphere.mapper.CartMapper;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        Cart cart = cartService.getOrCreateCart(user.getId());
        List<CartItem> items = cartService.getCartItems(user.getId());

        return CartMapper.toCartResponse(cart, items);
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public CartItemResponse addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request) {

        User user = getAuthenticatedUser(authentication);

        CartItem item = cartService.addItem(
                user.getId(),
                request.getProductId(),
                request.getQuantity()
        );

        return CartMapper.toItemResponse(item);
    }

    @PutMapping("/items/{productId}")
    public CartItemResponse updateItemQuantity(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        User user = getAuthenticatedUser(authentication);

        CartItem item = cartService.updateItemQuantity(
                user.getId(),
                productId,
                request.getQuantity()
        );

        return CartMapper.toItemResponse(item);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(
            Authentication authentication,
            @PathVariable Long productId) {

        User user = getAuthenticatedUser(authentication);
        cartService.removeItem(user.getId(), productId);
    }

    @DeleteMapping("/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        cartService.clearCart(user.getId());
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
