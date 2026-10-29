
package com.shopsphere.repository;

import com.shopsphere.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByOrderNumber(String orderNumber);
}
