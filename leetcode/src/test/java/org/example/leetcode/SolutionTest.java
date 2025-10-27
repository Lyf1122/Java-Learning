package org.example.leetcode;

import org.example.util.JsonUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class SolutionTest {

  Solution solution = new Solution();

  @Test
  public void test1() {
    int[] nums1 = {1, 2, 4, 0, 0, 0};
    int[] nums2 = {1, 3, 3};
    solution.merge(nums1, 3, nums2, 3);
    System.out.println(JsonUtil.render(nums1));
  }
}