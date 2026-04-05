package DSA.Stack;

/*
Given an integer array nums, return an array result where:
result[i] = first greater element to the right of nums[i]
if no greater element exists, return -1

Input:  [2, 1, 2, 4, 3]
Output: [4, 2, 4, -1, -1]
 */

import java.util.Arrays;
import java.util.Stack;

public class NextGreaterElement {

    // brute force approach
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

    // optimal approach

    public static int[] nextGreaterElementLeftToRightTraverseInArray(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        Arrays.fill(result, -1);

        // stack stores indices of elements waiting for a next greater element
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {

            while (!stack.isEmpty() && arr[i] > arr[stack.peek()]) {
                int index = stack.pop();
                result[index] = arr[i];
            }
            // push current index for future comparision
            stack.push(i);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr = {2, 1, 2, 4, 3};
        System.out.println(Arrays.toString(findNextGreaterBruterForce(arr)));
        System.out.println(Arrays.toString(nextGreaterElementLeftToRightTraverseInArray(arr)));

    }
}
