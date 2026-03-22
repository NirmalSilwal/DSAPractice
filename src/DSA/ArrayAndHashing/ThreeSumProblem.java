package DSA.ArrayAndHashing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
https://leetcode.com/problems/3sum/description/
 */
public class ThreeSumProblem {

    public static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2 ; i++) {
            if (i > 0 && nums[i-1] == nums[i]) continue; // lets not count duplicate entry

            // using two pointers on sorted array
            int left = i + 1, right = nums.length - 1;

            while (left < right) {
                int threeSum = nums[i] + nums[left] + nums[right];

                if (threeSum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    while (left < right && nums[left] == nums[left + 1]) left++;

                    while (left < right && nums[right] == nums[right - 1]) right--;

                    left++;
                    right--;
                } else if (threeSum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = {-1,0,1,2,-1,-4};
        System.out.println(threeSum(nums));
    }
}
