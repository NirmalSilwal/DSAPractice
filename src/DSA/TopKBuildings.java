package DSA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class TopKBuildings {
    public static List<Integer> topK(int k, int[] heights) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int height : heights) {
            maxHeap.offer(height);
        }
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < k; i++) {
            result.add(maxHeap.poll());
        }

        return result;
    }
}
