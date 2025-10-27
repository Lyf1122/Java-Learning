package org.example.leetcode;

public class Solution {

  public void merge(int[] nums1, int m, int[] nums2, int n) {
    // nums1 length: m + n, nums2 length: n
    int i = 0, j = 0;
    int[] nums = new int[m + n];
    int cur;
    while (i < m || j < n) {
      if (i == m) {
        cur = nums2[j++];
      } else if (j == n) {
        cur = nums1[i++];
      } else if (nums1[i] < nums2[j]) {
        cur = nums1[i++];
      } else {
        cur = nums2[j++];
      }
      nums[i+j-1] = cur;
    }
    for (int k = 0; k != (m + n); k++) {
      nums1[k] = nums[k];
    }
  }

}