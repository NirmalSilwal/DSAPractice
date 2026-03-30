package DSA.ArrayAndHashing;

/*
Given a sorted array, reorder the original elements based on the sorted order of their squares.
Input:  [-3, -2, -1, 0, 1, 2, 3, 4]
Output: [0, -1, 1, -2, 2, -3, 3, 4]
 */

import java.util.Arrays;

public class ReorderBySortedSquaresOptimal {

    // O(n) time and space
    public static int[] reorderBySortedSquares(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // find first non-negative number
        int right = 0;
        for (int i = 0; i < n; i++) {
            if (nums[i] >= 0) {
                right = i;
                break;
            }
        }

        int left = right - 1; // left points to last negative number
        int index = 0;

        while (left >= 0 && right < n) {
            int leftAbs = Math.abs(nums[left]);
            int rightAbs = Math.abs(nums[right]);

            if (leftAbs <= rightAbs) {
                result[index++] = nums[left--];
            } else {
                result[index++] = nums[right++];
            }
        }

        while (left >= 0) {
            result[index++] = nums[left--];
        }

        while (right < n) {
            result[index++] = nums[right++];
        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = {-3, -2, -1, 0, 1, 2, 3, 4};
        System.out.println(Arrays.toString(reorderBySortedSquares(nums)));
    }
}
