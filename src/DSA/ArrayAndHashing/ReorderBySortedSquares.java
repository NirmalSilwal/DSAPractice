package DSA.ArrayAndHashing;

import java.util.Arrays;

/*
Given a sorted array, reorder the original elements based on the sorted order of their squares.
Input:  [-3, -2, -1, 0, 1, 2, 3, 4]
Output: [0, -1, 1, -2, 2, -3, 3, 4]
 */
public class ReorderBySortedSquares {

    static class Pair {
        int value;
        int square;

        Pair(int value) {
            this.value = value;
            this.square = value * value;
        }
    }

    public static int[] reorderBySquares(int[] nums) {
        Pair[] pairs = new Pair[nums.length];

        for (int i = 0; i < nums.length; i++) {
            pairs[i] = new Pair(nums[i]);
        }

        Arrays.sort(pairs, (a, b) -> {
            if (a.square != b.square) {
                return Integer.compare(a.square, b.square);
            }
            // if same square values
            return Integer.compare(a.value, b.value);
        });

        int[] result = new int[nums.length];
        for (int i = 0; i < pairs.length; i++) {
            result[i] = pairs[i].value;
        }
        return result;
    }

    public static void main(String[] args) {
        int[] nums = {-3, -2, -1, 0, 1, 2, 3, 4};
        System.out.println(Arrays.toString(reorderBySquares(nums)));
    }
}
