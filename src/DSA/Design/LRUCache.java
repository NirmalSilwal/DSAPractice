package DSA.Design;

import java.util.HashMap;
import java.util.Map;

/*
Design a data structure that follows the constraints of Least Recently Used (LRU) cache.
https://leetcode.com/problems/lru-cache/
 */
public class LRUCache {

    class Node {
        int key, value;
        Node prev, next;

        Node (int key, int value) {
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

        head = new Node(0, 0);
        tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public void put (int key, int value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            moveToFront(node);
        } else {
            if (map.size() == capacity) {
                // remove least recently used node
                Node lru = tail.prev;
                remove(lru);
                map.remove(lru.key);
            }
            Node newNode = new Node(key, value);
            insertToFront(newNode);
            map.put(key, newNode);
        }
    }

    public int get(int key) {
        if (!map.containsKey(key)) return -1;
        Node node = map.get(key);
        moveToFront(node); // after accessing it, it has to be updated in cache
        return node.value;
    }

    private void moveToFront(Node node) {
        remove(node);
        insertToFront(node);
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertToFront(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
}
