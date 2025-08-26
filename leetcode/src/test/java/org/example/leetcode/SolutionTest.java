package org.example.leetcode;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class SolutionTest {

  Solution solution = new Solution();

  @Test
  public void test1() {
    List<List<String>> lists = solution.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"});
    assertEquals(3, lists.size());
  }

  @Test
  public void test2() {
    int[] a = new int[]{0,3,7,2,5,8,4,6,0,1};
    int i = solution.longestConsecutive(a);
    assertEquals(9, i);
  }

  @Test
  public void test3() {
    int[] a = new int[]{0,3,7,2,5,8,4,6,0,1};
    solution.moveZeroes(a);
    System.out.println(Arrays.toString(a));
  }
}