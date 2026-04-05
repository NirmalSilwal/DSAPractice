package DSA.Stack;

/*
Given an integer array nums, return an array result where:
result[i] = first greater element to the right of nums[i]
if no greater element exists, return -1

Input:  [2, 1, 2, 4, 3]
Output: [4, 2, 4, -1, -1]
 */

import java.util.Arrays;

public class NextGreaterElement {

    public static int[] findNextGreaterBruterForce(int[] arr) {

        int n = arr.length;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (arr[j] > arr[i]) {
                    result[i] = arr[j];
                    break;
                }
            }
            if (result[i] == 0) result[i] = -1; // if no such element exist
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr = {2, 1, 2, 4, 3};
        System.out.println(Arrays.toString(findNextGreaterBruterForce(arr)));
    }
}
