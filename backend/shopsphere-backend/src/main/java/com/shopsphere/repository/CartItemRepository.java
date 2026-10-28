
package com.shopsphere.repository;

import com.shopsphere.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(
            Long cartId,
            Long productId
    );

    void deleteByCartIdAndProductId(Long cartId, Long productId);

    void deleteAllByCartId(Long cartId);
}
