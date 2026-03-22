package DSA.PrefixSum;

import java.util.HashMap;
import java.util.Map;

/*
Given array nums and integer k, return length of longest subarray with sum = k.
input: nums = [1, -1, 5, -2, 3], k = 3
output: 4
 */
public class LongestSubarrayWithSumK {

    public static int longestSubarray(int[] nums, int k) {

        // store prefix sum and its first index where it occured
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, -1);

        int runningSum = 0;
        int maxLength = 0;

        for (int i = 0; i < nums.length; i++) {
            runningSum += nums[i];

            int neededPrefix = runningSum - k;

            if (map.containsKey(neededPrefix)) {
                int prevIndex = map.get(neededPrefix);
                // find length of subarray
                int length = i - prevIndex;
                maxLength = Math.max(maxLength, length);
            }

            if (!map.containsKey(runningSum)) {
                map.put(runningSum, i);
            }
        }

        return maxLength;
    }
}
