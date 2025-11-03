package com.lyf.techtools.service.impl;

import com.lyf.techtools.dao.OrderRepository;
import com.lyf.techtools.dao.UserRepository;
import com.lyf.techtools.entity.Order;
import com.lyf.techtools.entity.User;
import com.lyf.techtools.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final OrderRepository orderRepository;

  public UserServiceImpl(UserRepository userRepository, OrderRepository orderRepository) {
    this.userRepository = userRepository;
    this.orderRepository = orderRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getTop5Orders(Long userId) {
    return orderRepository.findTop5ByUserIdOrderByCreateAtDesc(userId);
  }

  @Override
  @Transactional
  public User createUser(User user) {
    user.setId(null); // 确保自增
    return userRepository.save(user);
  }

  @Override
  @Transactional
  public User updateUser(Long id, User user) {
    return userRepository.findById(id)
        .map(existing -> {
          existing.setName(user.getName());
          existing.setEmail(user.getEmail());
          return userRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
  }

  @Override
  @Transactional
  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new IllegalArgumentException("User not found: " + id);
    }
    userRepository.deleteById(id);
  }

  @Override
  @Transactional(readOnly = true)
  public List<User> listUsers() {
    return userRepository.findAll();
  }
}
