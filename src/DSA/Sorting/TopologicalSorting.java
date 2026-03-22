package DSA.Sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TopologicalSorting {

    public static List<Integer> topologicalSort(int V, List<List<Integer>> adj) {
        boolean[] visited = new boolean[V];
        Stack<Integer> stack = new Stack<>();

        // run dfs from every unvisited node
        for (int i = 0; i < V; i++) {
            if (!visited[i]) {
                dfs(i, adj, visited, stack);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty())  {
            result.add(stack.pop());
        }
        return result;
    }

    public static void dfs(int node, List<List<Integer>> adj, boolean[] visited, Stack<Integer> stack) {
        visited[node] = true;

        // visit all neighbors
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                dfs (neighbor, adj, visited, stack);
            }
        }
        stack.push(node);
    }
}
