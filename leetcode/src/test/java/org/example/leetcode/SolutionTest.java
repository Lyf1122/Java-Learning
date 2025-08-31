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

  @Test
  public void test4() {
    int[] nums = {-1,0,1,2,-1,-4,3};
    List<List<Integer>> lists = solution.threeSum(nums);
  }

  @Test
  public void test5() {
    String s = "au";
    int i = solution.lengthOfLongestSubstring(s);
  }

  @Test
  public void test6() {
    String s = "abba";
    int i = solution.lengthOfLongestSubstring2(s);
    System.out.println(i);
  }

  @Test
  public void test7() {
    String s = "cbaebabacd", p = "abc";
    List<Integer> anagrams = solution.findAnagrams(s, p);
    assertEquals(2, anagrams.size());
  }

  @Test
  public void test8() {
    int[] nums = new int[]{1, 1, 1};
    int i = solution.subarraySum(nums, 2);
    System.out.println(i);
  }

  @Test
  public void test9() {
    int[][] intervals = {{1,4},{0,6}};
    int[][] merge = solution.merge(intervals);
    assertEquals(1, merge.length);
  }

  @Test
  public void test10() {
    int[] nums = new int[]{1,2,3,4,5,6,7};
    solution.rotate(nums, 3);
//    System.out.println(Arrays.toString(nums));
  }

  @Test
  public void test11() {
    int[] nums = new int[]{-1,1,0,-3,3};
    solution.productExceptSelf(nums);
  }

  @Test
  public void test12() {
    int[] nums = new int[]{1,2,0};
    int i = solution.firstMissingPositive(nums);
    assertEquals(3, i);
  }

}