package com.lyf.techtools.dto;

import com.lyf.techtools.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrdersDTO {
  private Long userId;
  private String userName;
  private List<Order> orders;
}

