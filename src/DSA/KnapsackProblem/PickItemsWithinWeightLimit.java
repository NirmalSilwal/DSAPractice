package DSA.KnapsackProblem;

/*
you are given weights, values, capacity. Each item can be picked only once, and it has weight and value
return the maximum total value you can get without exceeding capacity
 */
public class PickItemsWithinWeightLimit {

    public static int knapsack(int[] weights, int[] values, int capacity) {
        // dp[i] is max value we can get using capacity i
        int[] dp = new int[capacity + 1];

        for (int k = 0; k < weights.length; k++) {
            int w = weights[k];
            int v = values[k];

            for (int i = capacity; i >= w; i--) {
                dp[i] = Math.max(dp[i], v + dp[i - w]);
            }
        }
        return dp[capacity];
    }

    public static void main(String[] args) {
        int[] weights = {1, 3, 4, 5};
        int[] values = {1, 4, 5, 7};
        int capacity = 7;
        System.out.println(knapsack(weights, values, capacity));
    }
}
