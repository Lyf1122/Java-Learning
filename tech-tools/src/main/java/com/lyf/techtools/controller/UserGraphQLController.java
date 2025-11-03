package com.lyf.techtools.controller;

import com.lyf.techtools.entity.Order;
import com.lyf.techtools.entity.User;
import com.lyf.techtools.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserGraphQLController {

  private final UserService userService;

  public UserGraphQLController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  public User user(@Argument Long userId) {
    return userService.getUserById(userId).orElse(null);
  }

  @SchemaMapping(typeName = "User", field = "orders")
  public List<Order> orders(User user) {
    return userService.getTop5Orders(user.getId());
  }
}
