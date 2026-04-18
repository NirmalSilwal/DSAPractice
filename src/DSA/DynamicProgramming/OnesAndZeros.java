package DSA.DynamicProgramming;

/*
https://leetcode.com/problems/ones-and-zeroes/description/
 */
public class OnesAndZeros {

    public static int findMaxForm(String[] strs, int m, int n) {
        // dp[i][j] = max number of strings that can be formed using at most i 0's and j 1's
        int[][] dp = new int[m + 1][n + 1];

        for (String str : strs) {
            int zeros = 0;
            int ones = 0;

            // count zeros and ones in current string
            for (char ch : str.toCharArray()) {
                if (ch == '0') {
                    zeros++;
                } else {
                    ones++;
                }
            }

            for (int i = m; i >= zeros; i--) {
                for (int j = n; j >= ones; j--) {

                    dp[i][j] = Math.max(dp[i][j], 1 + dp[i - zeros][j - ones]);
                }
            }
        }
        return dp[m][n];
    }

    public static void main(String[] args) {
        String[] strs = {"10","0001","111001","1","0"};
        int m = 5, n = 3;
        System.out.println(findMaxForm(strs, m, n)); // 4
    }
}
