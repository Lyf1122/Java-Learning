package com.lyf.techtools.controller;

import com.lyf.techtools.entity.Order;
import com.lyf.techtools.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  public ResponseEntity<Order> create(@RequestBody Order order) {
    Order saved = orderService.createOrder(order);
    return ResponseEntity.created(URI.create("/orders/" + saved.getId())).body(saved);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Order> get(@PathVariable Long id) {
    return orderService.getOrderById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public List<Order> list(@RequestParam(value = "userId", required = false) Long userId) {
    if (userId != null) {
      return orderService.listOrdersByUser(userId);
    }
    return orderService.listOrders();
  }

  @PutMapping("/{id}")
  public ResponseEntity<Order> update(@PathVariable Long id, @RequestBody Order order) {
    try {
      return ResponseEntity.ok(orderService.updateOrder(id, order));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      orderService.deleteOrder(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }
}

