package com.lyf.techtools.gql;

import com.lyf.techtools.entity.Order;
import com.lyf.techtools.service.OrderService;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class OrderDataLoader {

  private static final Logger log = LoggerFactory.getLogger(OrderDataLoader.class);

  @Autowired
  private OrderService orderService;

  public BatchLoader<String, List<Order>> orderBatchLoader() {
    return userIds -> CompletableFuture.supplyAsync(() -> {
      try {
        log.info("DataLoader 批量加载订单，用户IDs: {}", userIds);

        // 批量查询
        Map<String, List<Order>> ordersByUserId = orderService.findByUserIds(userIds);

        // 按原始顺序返回，保证与输入userIds顺序一致
        return userIds.stream()
          .map(userId -> ordersByUserId.getOrDefault(userId, new ArrayList<>()))
          .collect(Collectors.toList());

      } catch (Exception e) {
        log.error("DataLoader 批量加载订单失败", e);
        throw new RuntimeException("加载订单失败", e);
      }
    });
  }

  public DataLoader<String, List<Order>> createDataLoader() {
    return DataLoaderFactory.newDataLoader(orderBatchLoader());
  }
}
