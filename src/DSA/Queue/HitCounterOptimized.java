package DSA.Queue;

public class HitCounterOptimized {
    private final int[] times;
    private final int[] hits;

    public HitCounterOptimized() {
        times = new int[300];
        hits = new int[300];
    }

    public void hit(int timestamp) {
        int index = timestamp % 300;

        if (times[index] != timestamp) {
            // this bucket belongs to older timestamp so reset it
            times[index] = timestamp;
            hits[index] = 1;
        } else {
            hits[index]++;
        }
    }

    public int getHits(int timestamp) {
        int totalHits = 0;
        for (int i = 0; i < 300; i++) {
            if (timestamp - times[i] < 300) {
                totalHits += hits[i];
            }
        }
        return totalHits;
    }
}
