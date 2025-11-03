package com.lyf.techtools.controller;

import com.lyf.techtools.entity.User;
import com.lyf.techtools.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public ResponseEntity<User> create(@RequestBody User user) {
    User saved = userService.createUser(user);
    return ResponseEntity.created(URI.create("/users/" + saved.getId())).body(saved);
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> get(@PathVariable Long id) {
    return userService.getUserById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public List<User> list() {
    return userService.listUsers();
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
    try {
      return ResponseEntity.ok(userService.updateUser(id, user));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    try {
      userService.deleteUser(id);
      return ResponseEntity.noContent().build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }
}

