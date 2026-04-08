package DSA.PrefixSum;

import java.util.Arrays;

/*
Given an integer array nums, return an array output where output[i] is the product of all the elements of nums except nums[i].

Input: nums = [1,2,4,6]

Output: [48,24,12,8]
 */
public class ProductExceptSelf {

    public static int[] productExceptSelf(int[] nums) {
        int n =  nums.length;
        int[] prefix = new int[n];
        int[] suffix = new int[n];
        int[] output = new int[n];

        prefix[0] = 1;
        for (int i = 1; i < n; i++) {
            prefix[i] = prefix[i - 1] * nums[i - 1];
        }
        suffix[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            suffix[i] = suffix[i + 1] * nums[i + 1];
        }

        for (int i = 0; i < n; i++) {
            output[i] = prefix[i] * suffix[i];
        }

        return output;
    }

    public static int[] productExceptSelfOptimized(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        int prefix = 1;
        for (int i = 0; i < n; i++) {
            result[i] = prefix;
            prefix *= nums[i];
        }

        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = {1, 2,4, 6};
        System.out.println(Arrays.toString(productExceptSelf(nums)));
        System.out.println(Arrays.toString(productExceptSelfOptimized(nums)));
    }
}
