package com.lyf.techtools.gql;

import com.lyf.techtools.entity.User;
import com.lyf.techtools.service.UserService;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserDataLoader {
  private static final Logger log = LoggerFactory.getLogger(UserDataLoader.class);

  @Autowired
  private UserService userService;

  public BatchLoader<String, User> userBatchLoader() {
    return userIds -> CompletableFuture.supplyAsync(() -> {
      try {
        log.info("DataLoader 批量加载用户: {}", userIds);

        List<User> users = userService.findByIds(userIds);
        Map<Long, User> userMap = users.stream()
          .collect(Collectors.toMap(User::getId, Function.identity()));

        // 按原始顺序返回
        return userIds.stream()
          .map(userId -> userMap.getOrDefault(Long.valueOf(userId), null))
          .collect(Collectors.toList());

      } catch (Exception e) {
        log.error("DataLoader 批量加载用户失败", e);
        throw new RuntimeException("加载用户失败", e);
      }
    });
  }

  public DataLoader<String, User> createDataLoader() {
    return DataLoaderFactory.newDataLoader(userBatchLoader());
  }
}

