package DSA.PrefixSum;

import java.util.HashMap;
import java.util.Map;

public class SubarraySumK {

    public static int subarraySumEqualsK(int[] nums, int k) {

        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1);

        int runningSum = 0;
        int count = 0;

        for (int n : nums) {
            runningSum += n;

            int neededPrefix = runningSum - k;

            count += prefixSumCount.getOrDefault(neededPrefix, 0);

            prefixSumCount.put(runningSum, prefixSumCount.getOrDefault(runningSum, 0) + 1);
        }

        return count;
    }
}
