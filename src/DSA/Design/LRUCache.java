package DSA.Design;

import java.util.HashMap;
import java.util.Map;

/*
Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.
https://leetcode.com/problems/lru-cache/
 */
public class LRUCache {

    class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> map;
    private final Node head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();

        // creating dummy head and tail to cover null cases
        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }



}
