
package com.shopsphere.service;

import com.shopsphere.entity.Order;

import java.util.List;

public interface OrderService {

    Order checkout(Long userId);

    Order getOrderById(Long userId, Long orderId);

    List<Order> getOrderHistory(Long userId);

    Order cancelOrder(Long userId, Long orderId);
}
