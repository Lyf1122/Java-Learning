package org.example.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  public void processRequest(HttpServletRequest request) {
    Integer userId = getUserIdFromRequest(request);
    UserContext.setUserId(userId);
    // do something
    String userName = getUserNameByUserId(userId);
    logger.info("UserName = [{}]", userName);
    UserContext.clear();
  }

  private Integer getUserIdFromRequest(HttpServletRequest request) {
    return Integer.valueOf(request.getParameter("userId"));
  }

  private String getUserNameByUserId(Integer userId) {
    return "UserName" + userId;
  }

}
