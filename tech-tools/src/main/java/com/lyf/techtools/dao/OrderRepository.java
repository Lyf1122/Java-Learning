package com.lyf.techtools.dao;

import com.lyf.techtools.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findTop5ByUserIdOrderByCreateAtDesc(Long userId);
  List<Order> findByUserIdOrderByCreateAtDesc(Long userId);
}
