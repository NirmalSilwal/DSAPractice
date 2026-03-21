package DSA.Heaps;

/*
https://leetcode.com/problems/kth-largest-element-in-an-array/
Input: nums = [3,2,1,5,6,4], k = 2
Output: 5
 */

import java.util.PriorityQueue;

public class KthLargestElement {

    public int findKthLargest(int[] nums, int k) {

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int n : nums) {
            minHeap.offer(n);

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }
}
