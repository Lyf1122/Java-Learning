package com.lyf.techtools.service;

import com.lyf.techtools.entity.Order;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
  Order createOrder(Order order);
  Order updateOrder(Long id, Order order);
  void deleteOrder(Long id);
  Optional<Order> getOrderById(Long id);
  List<Order> listOrders();
  List<Order> listOrdersByUser(Long userId);
  Map<String, List<Order>> findByUserIds(List<String> userIds);
}
