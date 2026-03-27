package DSA.Queue;

import java.util.LinkedList;
import java.util.Queue;

/*
You are given a stream of API hit timestamps in seconds. Design a data structure that supports:

hit(timestamp) → record a hit at timestamp
getHits(timestamp) → return number of hits in the past 5 minutes from timestamp

example:
hit(1)
hit(2)
hit(3)
getHits(4)   -> 3

hit(300)
getHits(300) -> 4
getHits(301) -> 3

At 301, the hit at 1 is outside the last 300 seconds window.
 */
public class HitCounter {
    private final Queue<Integer> queue;

    public HitCounter() {
        queue = new LinkedList<>();
    }

    public void hit(int timestamp) {
        queue.offer(timestamp);
    }

    public int getHits(int timestamp) {
        // remove hits older than 5 mins / 300 secs
        while (!queue.isEmpty() && queue.peek() <= timestamp - 300) {
            queue.poll();
        }
        return queue.size();
    }

    public static void main(String[] args) {
        HitCounter counter = new HitCounter();
        counter.hit(1);
        counter.hit(2);
        counter.hit(3);

        System.out.println(counter.getHits(4));

        counter.hit(300);
        System.out.println(counter.getHits(300));
        System.out.println(counter.getHits(301));
    }
}
