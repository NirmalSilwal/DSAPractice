package DSA.DynamicProgramming;

/*
Given a m x n grid filled with non-negative numbers, find a path from top left to bottom right,
which minimizes the sum of all numbers along its path.
Note: You can only move either down or right at any point in time.

Input: grid = [[1,3,1],[1,5,1],[4,2,1]]
Output: 7
Explanation: Because the path 1 → 3 → 1 → 1 → 1 minimizes the sum.
 */
public class MinimumPathSum {

    public static int minPathSum(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        int[][] dp = new int[rows][cols];

        // starting cell
        dp[0][0] = grid[0][0];

        // first row - can only come from left cell
        for (int col = 1; col < cols; col++) {
            dp[0][col] = grid[0][col] + dp[0][col - 1];
        }

        // first column - can only come from top cell
        for (int row = 1; row < rows; row++) {
            dp[row][0] = grid[row][0] + dp[row - 1][0];
        }

        // all other cells
        for (int row = 1; row < rows; row++) {
            for (int col = 1; col < cols; col++) {
                dp[row][col] = grid[row][col] + Math.min(dp[row - 1][col], dp[row][col - 1]);
            }
        }

        return dp[rows - 1][cols - 1];
    }

    public static void main(String[] args) {
        int[][] grid = {
                {1, 3, 1},
                {1, 5, 1},
                {4, 2, 1}
        };
        System.out.println(minPathSum(grid));
    }
}
