package DSA;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class TopKBuildingIndices {

    public static List<Integer> topKIndices (int[] heights, int k) {
        List<Integer> result = new ArrayList<>();
        if (heights == null || heights.length == 0 || k < 0 || k > heights.length) {
            return result;
        }
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        for (int i = 0; i < heights.length; i++) {
            minHeap.offer(new int[]{heights[i], i}); // heights, index

            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<int[]> temp = new ArrayList<>(minHeap);
        temp.sort((a, b) -> b[0] - a[0]);

        for (int[] pair : temp) {
            result.add(pair[1]); // returns indices of top K elements
        }

        return result;
    }

}
