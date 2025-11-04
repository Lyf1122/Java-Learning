package com.lyf.techtools.service.impl;

import com.lyf.techtools.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest extends BaseServiceTest{
  @Autowired UserServiceImpl userService;

  @Test
  public void testCRUD() {
    User user = User.builder().name("evan").email("evan@163.com").build();
    User user1 = userService.createUser(user);
    assertNotNull(user1.getId());
  }
}