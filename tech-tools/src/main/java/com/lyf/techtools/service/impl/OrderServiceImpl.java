package com.lyf.techtools.service.impl;

import com.lyf.techtools.dao.OrderRepository;
import com.lyf.techtools.entity.Order;
import com.lyf.techtools.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;

  public OrderServiceImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  @Transactional
  public Order createOrder(Order order) {
    order.setId(null);
    return orderRepository.save(order);
  }

  @Override
  @Transactional
  public Order updateOrder(Long id, Order order) {
    return orderRepository.findById(id)
        .map(existing -> {
          existing.setUserId(order.getUserId());
          existing.setTotal(order.getTotal());
          existing.setCreateAt(order.getCreateAt());
          return orderRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
  }

  @Override
  @Transactional
  public void deleteOrder(Long id) {
    if (!orderRepository.existsById(id)) {
      throw new IllegalArgumentException("Order not found: " + id);
    }
    orderRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Order> getOrderById(Long id) {
    return orderRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> listOrders() {
    return orderRepository.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> listOrdersByUser(Long userId) {
    return orderRepository.findByUserIdOrderByCreateAtDesc(userId);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, List<Order>> findByUserIds(List<String> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return Map.of();
    }
    List<Long> ids = userIds.stream()
        .filter(id -> id != null && !id.isBlank())
        .map(Long::valueOf)
        .collect(Collectors.toList());
    List<Order> orders = orderRepository.findByUserIdInOrderByCreateAtDesc(ids);
    return orders.stream()
        .collect(Collectors.groupingBy(o -> String.valueOf(o.getUserId()), Collectors.toList()));
  }
}
