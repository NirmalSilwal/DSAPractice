package DSA.HashMap;

import java.util.HashSet;
import java.util.Set;

/*
https://leetcode.com/problems/longest-consecutive-sequence/description/

Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence.
Input: nums = [100,4,200,1,3,2]
Output: 4
Explanation: The longest consecutive elements sequence is [1, 2, 3, 4]. Therefore, its length is 4.
 */
public class LongestConsecutiveSequence {

    // 0(n) time and space each
    public static int longestConsecutive(int[] nums) {
        Set<Integer> set  = new HashSet<>();
        for (int n : nums) {
            set.add(n);
        }

        int longest = 0;

        for (int num : set) {
            if (!set.contains(num - 1)) {
                // previous number don't exist in set means its beginning of sequence
                int currentNum = num;
                int currentLen = 1;

                while (set.contains(currentNum + 1)) {
                    currentLen++;
                    currentNum++;
                }
                longest = Math.max(longest, currentLen);
            }
        }
        return longest;
    }

    public static void main(String[] args) {
        int[] nums = {100,4,200,1,3,2};
        System.out.println(longestConsecutive(nums));
    }
}
