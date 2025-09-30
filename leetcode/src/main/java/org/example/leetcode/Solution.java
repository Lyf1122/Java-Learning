package org.example.leetcode;

import org.example.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Solution {

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

  public List<List<Integer>> threeSum(int[] nums) {
    int n = nums.length;
    List<List<Integer>> res = new LinkedList<>();
    if (n < 3)  return res;
    Arrays.sort(nums);
    for (int i = 0; i < n-2; i++) {
      if (nums[i] > 0)  continue;
      if (i > 0 && nums[i] == nums[i-1]) continue;
      int left = i+1; int right = n-1;
      while (left < right) {
        int sum = nums[i] + nums[left] + nums[right];
        if (sum == 0) {
          res.add(Arrays.asList(nums[i], nums[left], nums[right]));
          // 跳过重复的元素
          while (left < right && nums[left] == nums[left+1]) left++;
          while (left < right && nums[right] == nums[right-1]) right--;
          left++; right--;
        }
        if (sum < 0) {
          left++;
        }
        if (sum > 0) {
          right--;
        }
      }
    }
    log.info(JsonUtil.renderWithPrettyPrinter(res));
    return res;
  }

  public int trap(int[] height) {
    int n = height.length;
    int[] leftMax = new int[n];
    int[] rightMax = new int[n];
    leftMax[0] = height[0]; rightMax[n-1] = height[n-1];
    for (int i = 1; i < n; i++) {
      leftMax[i] = Math.max(leftMax[i-1], height[i]);
    }
    for (int i = n-2; i >= 0; i--) {
      rightMax[i] = Math.max(rightMax[i+1], height[i]);
    }
    int ans = 0;
    for (int i = 0; i < n; i++) {
      ans += Math.min(leftMax[i], rightMax[i]) - height[i];
    }
    return ans;
  }

  public int lengthOfLongestSubstring(String s) {
    int n = s.length();
    if (n < 2) return n;
    int ans = 1;
    char[] charArray = s.toCharArray();
    Set<Character> set = new HashSet<>();
    for (int i = 0; i < n; i++) {
      set.add(charArray[i]);
      for (int j = i + 1; j < n; j++) {
        if (set.contains(charArray[j])) {
          ans = Math.max(ans, set.size());
          log.info(JsonUtil.renderWithPrettyPrinter(set));
          break;
        } else  {
          set.add(charArray[j]);
        }
      }
      ans = Math.max(ans, set.size());
      set.clear();
    }
    log.info("ans: {}", ans);
    return ans;
  }

  public int lengthOfLongestSubstring2(String s) {
    int n = s.length();int ans = 0;
    int left = 0;int right = 0;
    if (n < 2) return n;
    Map<Character, Integer> map = new HashMap<>();
    while (right < n) {
      if (map.containsKey(s.charAt(right)) && (map.get(s.charAt(right)) > left)) {
        left = map.get(s.charAt(right)) + 1;
      }
      map.put(s.charAt(right), right);
      ans = Math.max(ans, right - left + 1);
      right++;
    }
    return ans;
  }

  public List<Integer> findAnagrams(String s, String p) {
    List<Integer> ans = new ArrayList<>();
    int n = s.length(); int m = p.length();
    if (n < m) return ans;
    int[] sFreq = new int[26];
    int[] pFreq = new int[26];  // 储存p中各个字符出现的次数
    // set up pFreq
    for (char c : p.toCharArray()) {
      pFreq[c - 'a']++;
    }
    // set up sFreq (the longer one)
    for (int i = 0; i < m; i++) {
      // 从i开始 m个字符的频率
      sFreq[s.charAt(i) - 'a']++;
    }
    // 初始窗口
    if (Arrays.equals(sFreq,  pFreq)) {
      ans.add(0);
    }
    //  滑动窗口
    for (int i = 1; i <= n - m; i++) {
      // 移除窗口左端的字符
      sFreq[s.charAt(i - 1) - 'a']--;
      // 添加窗口右端的新字符
      sFreq[s.charAt(i + m - 1) - 'a']++;
      // 检查当前窗口频率是否匹配
      if (Arrays.equals(pFreq, sFreq)) {
        ans.add(i);
      }
    }
    return ans;
  }

  static boolean isAnagram(String a, String b) {
    char[] arrayA = a.toCharArray();
    Arrays.sort(arrayA);
    char[] arrayB = b.toCharArray();
    Arrays.sort(arrayB);
    return Arrays.equals(arrayA, arrayB);
  }

  public int subarraySum(int[] nums, int k) {
    int n =  nums.length;
    // 创建前序和数组
    int[] prefixSum = new int[n + 1];
    prefixSum[0] = 0;
    for (int i = 1; i <= n; i++) {
      prefixSum[i] = prefixSum[i - 1] + nums[i - 1];
    }

    int ans = 0;
    Map<Integer, Integer> map = new HashMap<>();
    for(int prefix: prefixSum) {
      int target = prefix - k;
      if (map.containsKey(target)) {
        ans += map.get(target);
      }
      map.put(prefix, map.getOrDefault(prefix, 0) + 1);
    }
    return ans;
  }

  public int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] res = new int[n - k + 1];
    if (nums.length == 0 || k < 0) {
      return res;
    }

    Deque<Integer> deque = new LinkedList<>();
    for (int i = 0; i < n; i++) {
      // 移除不在窗口的值，也就是窗口向右移动
      while (!deque.isEmpty() && deque.peek() < i - k + 1) {
        deque.poll();
      }
      while (!deque.isEmpty() && nums[deque.getLast()] < nums[i]) {
        deque.pollLast();
      }
      // 将当前索引加入到队列尾部
      deque.offer(i); // equal to offerLast
      // 如果窗口已经形成（i >= k-1），将队首对应的值加入结果
      if (i >= k - 1) {
        res[i - k + 1] = nums[deque.peek()];
      }
    }
    return res;
  }

  public int maxSubArray(int[] nums) {
    int n = nums.length;
    int[] preSum = new int[n + 1];
    int minPreSum = 0; int maxPreSum = nums[0];
    preSum[0] = 0;
    for (int i = 1; i <= n; i++) {
      preSum[i] = preSum[i - 1] + nums[i - 1];
    }

    for (int i = 1; i <= n; i++) {
      int curSum = preSum[i] - minPreSum;
      maxPreSum = Math.max(curSum, maxPreSum);
      minPreSum = Math.min(preSum[i], minPreSum);
    }
    return maxPreSum;
  }

  public int[][] merge(int[][] intervals) {
    int n = intervals.length;
    if (n < 2) {
      return intervals;
    }
    // 按照区间起始点排序
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    List<int[]> merged = new ArrayList<>();
    int[] current = intervals[0];
    for (int i = 1; i < n; i++) {
      if (intervals[i][0] <= current[1]) {
        // 重叠，合并区间
        current[1] = Math.max(current[1], intervals[i][1]);
      } else {
        // 不重叠，添加当前区间并更新
        merged.add(current);
        current = intervals[i];
      }
    }
    merged.add(current); // 添加最后一个区间

    // 直接转换为int[][]
    return merged.toArray(new int[merged.size()][]);
  }

  public void rotate(int[] nums, int k) {
    int n = nums.length;
    int[] temp = Arrays.copyOf(nums, n);
    for (int i = 0; i < n; i++) {
      if (i + k < n) {
        nums[i + k] = temp[i];
      } else {
        nums[(i + k) % n] = temp[i];
      }
    }
    log.info(JsonUtil.render(nums));
  }

  public int[] productExceptSelf(int[] nums) {
    int n = nums.length;
    int [] left = new int[n];  left[0] = 1;
    for (int i = 1; i < n; i++) {
      left[i] = left[i - 1] * nums[i-1];
    }
    int [] right = new int[n];  right[n-1] = 1;
    for (int i = n-2; i >= 0; i--) {
      right[i] = right[i+1] * nums[i+1];
    }
    int[] ans = new int[n];
    for (int i = 0; i < n; i++) {
      ans[i] = left[i] * right[i];
    }
    log.info(JsonUtil.render(ans));
    return ans;
  }

  public int firstMissingPositive(int[] nums) {
    Arrays.sort(nums);
    int[] array = Arrays.stream(nums)
      .filter(i -> i > 0)
      .distinct()
      .toArray();
    for (int i = 0; i < array.length; i++) {
      if (array[i] != i + 1) {
        return i + 1;
      }
    }
    return array.length + 1;
  }

  static class ListNode {
    int val;
    ListNode next;
    ListNode(int x) {
        val = x;
        next = null;
    }
 }

  public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
    if (headA == null || headB == null) {
      return null;
    }
    ListNode a = headA;
    ListNode b = headB;
    // 令a b定位到对方起点重新走到一遍，则必定在交汇处相遇
    while (a != b) {
      if (a != null) {
        a = a.next;
        a = headB;
      }
      if (b != null) {
        b = b.next;
        b = headB;
      }
    }

    return a;
  }

  public ListNode reverseList(ListNode head) {
    if (head == null || head.next == null) {
      return head;
    }
    ListNode prev = null;
    ListNode cur = head;
    while (cur.next != null) {
      ListNode tempNext = cur.next;
      cur.next = prev;
      prev = cur;
      cur = tempNext;
    }
    cur.next = prev;
    return cur;
  }

  public boolean isPalindrome(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;
    while (fast.next != null && fast.next.next != null) {
      slow = slow.next;
      fast = fast.next.next;
    }
    ListNode secondHalf = reverseList(slow);
    ListNode firstHalf = head;
    while (secondHalf != null && firstHalf != null) {
      if (secondHalf.val != firstHalf.val) {
        return false;
      }
      firstHalf = firstHalf.next;
      secondHalf = secondHalf.next;
    }
    return true;
  }

  public boolean hasCycle(ListNode head) {
    if (head == null || head.next == null) {
      return false;
    }
    ListNode slow = head;
    ListNode fast = head;
    while (fast.next != null && fast.next.next != null) {
      slow = slow.next;
      fast = fast.next.next;
      if (slow == fast) {
        return true;
      }
    }
    return false;
  }

  public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
    ListNode dummyHead = new ListNode(0);
    ListNode cur = dummyHead;
    while (list1 != null && list2 != null) {
      if (list1.val > list2.val) {
        cur.next = list2;
        list2 = list2.next;
      } else {
        cur.next = list1;
        list1 = list1.next;
      }
      cur = cur.next;
    }
    if (list1 != null) {
      cur.next = list1;
    } else  {
      cur.next = list2;
    }
    return dummyHead.next;
  }

  public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    ListNode dummyHead = new ListNode(0);
    ListNode cur = dummyHead;
    int carry = 0;
    while (l1 != null && l2 != null) {
      int sum = (l1.val + l2.val + carry) % 10;
      carry = (l1.val + l2.val + carry) >= 10 ? 1 : 0;
      cur.next = new ListNode(sum);
      cur = cur.next;
      l1 = l1.next;
      l2 = l2.next;
    }
    while (l1 != null) {
      int sum = (l1.val + carry) % 10;
      carry = (l1.val + carry) >= 10 ? 1 : 0;
      cur.next = new ListNode(sum);
      cur = cur.next;
      l1 = l1.next;
    }

    while (l2 != null) {
      int sum = (l2.val + carry) % 10;
      carry = (l2.val + carry) >= 10 ? 1 : 0;
      cur.next = new ListNode(sum);
      cur = cur.next;
      l2 = l2.next;
    }
    if (carry > 0) {
      cur.next = new ListNode(carry);
    }
    return dummyHead.next;
  }

  public ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummyHead = new ListNode(0);
    dummyHead.next = head;
    ListNode fast = dummyHead;
    ListNode slow = dummyHead;
    for (int i = 1; i <= n; i++) {
      fast = fast.next;
    }

    while (fast.next != null) {
      fast = fast.next;
      slow = slow.next;
    }

    slow.next = slow.next.next;
    return dummyHead.next;
  }

  public ListNode swapPairs(ListNode head) {
    if (head == null || head.next == null) {
      return head;
    }
    ListNode dummyHead = new ListNode(0);
    dummyHead.next = head;
    ListNode prev = dummyHead;
    while (prev.next != null && prev.next.next != null) {
      ListNode first = prev.next;
      ListNode second = prev.next.next;
      // swap node
      prev.next = second;
      first.next = second.next;
      second.next = first;

      prev = first;
    }
    return dummyHead.next;
  }

  public ListNode sortList(ListNode head) {
    if (head == null || head.next == null) {
      return head;
    }
    // 快慢指针找到中间位置
    ListNode mid = findMid(head);
    ListNode rightStart = mid.next;
    mid.next = null;  // 断开链表
    // 递归
    ListNode left = sortList(head);
    ListNode right = sortList(rightStart);
    return merge(left, right);
  }

  private ListNode findMid(ListNode head) {
    ListNode slow = head;
    ListNode fast = head.next; // 为了确保在偶数个节点时，慢指针指向第一个中间节点。

    while (fast != null && fast.next != null) {
      slow = slow.next;
      fast = fast.next.next;
    }
    return slow;
  }

  private ListNode merge(ListNode l1, ListNode l2) {
    ListNode dummyHead = new ListNode(0);
    ListNode cur = dummyHead;
    while (l1 != null && l2 != null) {
      if (l1.val < l2.val) {
        cur.next = l1;
        l1 = l1.next;
      } else {
        cur.next = l2;
        l2 = l2.next;
      }
      cur = cur.next;
    }
    // while结束后，将剩余的直接连接在后面
    if (l1 != null) {
      cur.next = l1;
    }
    if (l2 != null) {
      cur.next = l2;
    }
    return dummyHead.next;
  }

  static class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
    TreeNode() {}
    TreeNode(int x) {
      val = x;
    }
    TreeNode(int x, TreeNode left, TreeNode right) {
      val = x;
      this.left = left;
      this.right = right;
    }
  }

  public List<Integer> inorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    inorder(root, result);
    return result;
  }

  public void inorderTraversal2(TreeNode root) {
    if (root == null) {
      return;
    }
    Stack<TreeNode> stack = new Stack<>();
    TreeNode cur = root;
    while (cur != null || !stack.isEmpty()) {
      while (cur != null) {
        stack.push(cur);
        cur = cur.left;
      }
      cur = stack.pop();
      log.info("Pop value = {}", cur.val);
      cur = cur.right;
    }
  }

  public void inorder(TreeNode root, List<Integer> res) {
    if (root == null) {
      return;
    }
    inorder(root.left, res);
    res.add(root.val);
    inorder(root.right, res);
  }

  public void levelOrder(TreeNode root) {
    if (root == null) return;
    Queue<TreeNode> queue = new LinkedList<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      TreeNode poll = queue.poll();
      log.info("poll value = {}", poll);
      if (poll.left != null) {
        queue.add(poll.left);
      }
      if (poll.right != null) {
        queue.add(poll.right);
      }
    }
  }

  public int maxDepth(TreeNode root) {
    int res = 0;
    if (root == null) {
      return res;
    }
    return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
  }

  public TreeNode invertTree(TreeNode root) {
    if (root == null) {
      return root;
    }
    TreeNode left = invertTree(root.left);
    TreeNode right = invertTree(root.right);

    root.left = right;
    root.right = left;
    return root;
  }

  public boolean isSymmetric(TreeNode root) {
    if (root == null) {
      return true;
    }
    return isSymmetric(root.left, root.right);
  }

  static boolean isSymmetric(TreeNode t1, TreeNode t2) {
    // 判断两棵树是否互为镜像
    if (t1 == null && t2 == null) return true;
    if (t1 == null || t2 == null) return false;
    return t1.val == t2.val && isSymmetric(t1.left, t2.right) && isSymmetric(t1.right, t2.left);
  }

  public TreeNode sortedArrayToBST(int[] nums) {
    if (nums == null || nums.length == 0) {
      return null;
    }
    return buildTree(nums, 0, nums.length - 1);
  }

  static TreeNode buildTree(int[] nums, int left, int right) {
    if (left > right) return null;
    int mid = (left + right) / 2;
    // 中间元素作为根节点
    TreeNode root = new TreeNode(nums[mid]);
    root.left = buildTree(nums, left, mid - 1);
    root.right = buildTree(nums, mid + 1, right);
    return root;
  }

  public boolean isValidBST(TreeNode root) {
    return isValidBST(root, null, null);
  }

  static boolean isValidBST(TreeNode root, Integer min, Integer max) {
    if (root == null) return true;
    boolean checkMin = min == null || root.val > min;
    boolean checkMax = max == null || root.val < max;
    if (!checkMin || !checkMax) return false;
    boolean checkLeft = isValidBST(root.left, min, root.val);
    boolean checkRight = isValidBST(root.right, root.val, max);
    return checkLeft && checkRight;
  }

  public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(res, new ArrayList<>(), nums, new boolean[nums.length]);
    return res;
  }

  private void backtrack(List<List<Integer>> result, List<Integer> tempList, int[] nums, boolean[] visited) {
    if (tempList.size() == nums.length) {
      result.add(new ArrayList<>(tempList));
      return;
    }
    for (int i = 0; i < nums.length; i++) {
      if (visited[i]) continue; // 代表已经使用过
      visited[i] = true;
      tempList.add(nums[i]);
      backtrack(result, tempList, nums, visited);
      tempList.removeLast();
      visited[i] = false; // 回溯，将最后的元素标记为未使用过
    }
  }

  public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(res, new ArrayList<>(), nums, 0);
    return res;
  }

  private void backtrack(List<List<Integer>> result, List<Integer> tempList, int[] nums, int start) {
    result.add(new ArrayList<>(tempList));
    for (int i = start; i< nums.length; i++) {
      tempList.add(nums[i]);
      backtrack(result, tempList, nums, i+1);
      tempList.remove(tempList.size() - 1);
    }
  }

  public List<String> letterCombinations(String digits) {
    Map<Character, String > map = new HashMap<>();
    map.put('2', "abc");  map.put('3', "def");  map.put('4', "ghi");
    map.put('5', "jkl");  map.put('6', "mno");  map.put('7', "pqrs");
    map.put('8', "tuv");  map.put('9', "wxyz");
    List<String> res = new ArrayList<>();
    if (digits == null || digits.isEmpty()) {
      return res;
    }
    backtrackCombine(0, digits, new StringBuilder(), res, map);
    return res;
  }

  private void backtrackCombine(int index, String digits, StringBuilder current, List<String> result, Map<Character, String> map) {
    if (index == digits.length()) {
      result.add(current.toString());
      return;
    }
    char digit = digits.charAt(index);
    String letters = map.get(digit);
    for (char c : letters.toCharArray()) {
      current.append(c);
      backtrackCombine(index + 1, digits, current, result, map);
      current.deleteCharAt(current.length() - 1);
    }
  }

  public List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    Arrays.sort(candidates);
    backtrackCombinationSum(result, path, target, candidates, 0);
    return result;
  }

  void backtrackCombinationSum(List<List<Integer>> result, List<Integer> path, int target, int[] candidates, int start) {
    if (target == 0) {
      result.add(new ArrayList<>(path));  // 必须创建副本，否则指向的是path的引用，导致所有path均为空
      return;
    }
    for (int i = start; i < candidates.length; i++){
      if (candidates[i] > target) break;

      path.add(candidates[i]);
      // 传入的参数应为target - candidates[i] 而不能直接将 target -= candidates[i] 传递
      backtrackCombinationSum(result, path, target - candidates[i], candidates, i); // 不是i+1 代表可以重复使用当前数字
      path.removeLast();
    }
  }

  public List<List<String>> partition(String s) {
    List<List<String>> result = new ArrayList<>();
    backTrackPartition(0, new ArrayList<>(), result, s);
    return result;
  }

  void backTrackPartition(int start, List<String> path, List<List<String>> result, String s) {
    if (start == s.length()) {
      // 说明已经处理完整个字符串(子串递归）
      result.add(new ArrayList<>(path));
      return;
    }
    for (int end = start; end < s.length(); end++) {
      if (isMirror(s, start, end)) {
        // 如果当前子串是回文，递归继续处理
        String sub = s.substring(start, end + 1);
        path.add(sub);
        backTrackPartition(end + 1, path, result, s);
        path.removeLast();
      }
    }
  }

  boolean isMirror(String s, int left, int right) {
    while (left < right) {
      if (s.charAt(left) != s.charAt(right)) {
        return false;
      }
      left++;
      right--;
    }
    return true;
  }

  public int maxProfit(int[] prices) {
    int length = prices.length;
    if (length < 2) return 0;
    int minCost = Integer.MAX_VALUE, profit = 0;
    for (int price : prices) {
      minCost = Math.min(price, minCost);
      profit = Math.max(profit, price - minCost);
    }
    return profit;
  }

}