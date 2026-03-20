package DSA.ArrayAndHashing;

import java.util.HashMap;
import java.util.Map;

/*
Input: nums = [2,7,11,15], target = 9
Output: [0,1] // return indices
https://leetcode.com/problems/two-sum/description/
 */
public class TwoSum {
    public int[] twoSum(int[] nums, int target) {

        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int compliment = target - nums[i];

            if (map.containsKey(compliment)) {
                return new int[]{i, map.get(compliment)};
            }
            map.put(nums[i], i);
        }
        return new int[]{}; // if no solution return empty array
    }
}
