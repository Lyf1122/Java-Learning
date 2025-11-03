package com.lyf.techtools.service;

import com.lyf.techtools.entity.Order;
import com.lyf.techtools.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
  Optional<User> getUserById(Long id);
  List<Order> getTop5Orders(Long userId);
  User createUser(User user);
  User updateUser(Long id, User user);
  void deleteUser(Long id);
  List<User> listUsers();
}
