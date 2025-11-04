package com.lyf.techtools.gql;

import com.lyf.techtools.entity.Order;
import com.lyf.techtools.entity.User;
import com.lyf.techtools.service.UserService;
import graphql.schema.DataFetcher;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class UserDataFetcher {

  @Autowired
  private UserService userService;

  @Autowired
  private UserDataLoader userDataLoader;

  @Autowired
  private OrderDataLoader orderDataLoader;

  /**
   * 查询单个用户
   */
  public DataFetcher<CompletableFuture<User>> getUser() {
    return environment -> {
      String userId = environment.getArgument("id");
      if (userId == null) {
        userId = environment.getArgument("userId");
      }
      log.info("查询用户: {}", userId);

      // 使用DataLoader异步加载
      DataLoader<String, User> userLoader = environment.getDataLoader("user");
      return userLoader.load(userId);
    };
  }

  /**
   * 查询用户列表
   */
  public DataFetcher<CompletableFuture<List<User>>> getUsers() {
    return environment -> {
      List<String> userIds = environment.getArgument("ids");
      log.info("查询用户列表: {}", userIds);

      DataLoader<String, User> userLoader = environment.getDataLoader("user");
      return userLoader.loadMany(userIds);
    };
  }

  /**
   * 解析User类型中的orders字段
   */
  public DataFetcher<CompletableFuture<List<Order>>> getOrders() {
    return environment -> {
      User user = environment.getSource();
      log.info("为用户 {} 加载订单", user.getId());

      // 使用DataLoader批量加载订单
      DataLoader<String, List<Order>> orderLoader = environment.getDataLoader("order");
      return orderLoader.load(String.valueOf(user.getId()));
    };
  }

}