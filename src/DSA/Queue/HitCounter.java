package DSA.Queue;

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
}
