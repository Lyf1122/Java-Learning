package org.example.leetcode;

import org.example.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class Solution {

  private static final Logger log = LoggerFactory.getLogger(Solution.class);

  /*两数之和*/
  public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
      int complement = target - nums[i];
      if (map.containsKey(complement)) {
        return new int[]{map.get(complement), i};
      }
      map.put(nums[i], i);
    }
    return null;
  }

  /*字母异位词分组*/
  public List<List<String>> groupAnagrams(String[] strs) {
    if (strs == null || strs.length == 0) {
      return new ArrayList<>();
    }

    Map<String, List<String>> map = new HashMap<>();
    for (String s : strs) {
      char[] chars = s.toCharArray();
      Arrays.sort(chars);
      String key = String.valueOf(chars);
      if (!map.containsKey(key)) {
        map.put(key, new ArrayList<>());
      }
      map.get(key).add(s);
    }
    log.info(JsonUtil.renderWithPrettyPrinter(map));
    return new ArrayList<>(map.values());
  }

  public int longestConsecutive(int[] nums) {
    if (nums == null || nums.length == 0) {
      return 0;
    }
    Arrays.sort(nums);
    int max = 0;
    int count = 1;
    for (int i = 1; i < nums.length; i++) {
      if (nums[i] == nums[i - 1]) {
        continue;
      }
      if (nums[i] == nums[i - 1] + 1) {
        count += 1;
      } else {
        max = Math.max(max, count);
        count = 1;
      }
    }
    return Math.max(max, count);
  }

  public void moveZeroes(int[] nums) {
    int n = nums.length;
    log.info("Original Array: {}", Arrays.toString(nums));
    if (n < 2) return;
    int count = 0;
    for (int i=0, j= n-1; count < n; count++) {
      if (nums[i] == 0) {
        for (int k = i + 1; k <= j; k++) {
          nums[k-1] = nums[k];
        }
        nums[j] = 0;
      } else {
        i += 1;
      }
    }
    log.info(JsonUtil.renderWithPrettyPrinter(nums));
  }

  public int maxArea(int[] height) {
    int n = height.length;
    int maxArea = 0;
    int left = 0; int right = n - 1;
    while (left < right) {
      maxArea = Math.max(maxArea, (right - left) * Math.min(height[left], height[right]));
      if (height[left] < height[right]) {
        left++;
      }  else {
        right--;
      }
    }
    return maxArea;
  }

}