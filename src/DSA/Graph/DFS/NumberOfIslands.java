package DSA.Graph.DFS;

/*
Given a 2D grid of '1's (land) and '0's (water), return the number of islands.
An island is formed by connecting adjacent lands horizontally or vertically.
grid =
[
  ['1','1','0','0'],
  ['1','0','0','1'],
  ['0','0','1','1'],
  ['0','0','0','0']
]
output: 2
 */

public class NumberOfIslands {

    public static int numIslands(char[][] grid) {
        // if empty grid
        if (grid == null || grid.length == 0) return 0;

        int rows = grid.length;
        int cols = grid[0].length;
        int islandCount = 0;

        // visit every cell in the grid
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {

                // if we find land, this is a new island
                if (grid[row][col] == '1') {
                    islandCount++;

                    // mark the whole connected islands as visited
                    dfs(grid, row, col);
                }
            }
        }

        return islandCount;
    }

    private static void dfs(char[][] grid, int row, int col) {
        int rows = grid.length;
        int cols = grid[0].length;

        if (row < 0 || col < 0 || row >= rows || col >= cols || grid[row][col] == '0') {
            return;
        }
        grid[row][col] = '0'; // mark as visited, make this land as water now

        dfs (grid, row - 1, col); // up
        dfs (grid, row + 1, col); // down
        dfs (grid, row, col - 1); // left
        dfs (grid, row, col + 1); // right
    }

    public static void main(String[] args) {
        char[][] grid =
                {
                        {'1', '1', '0', '0'},
                        {'1', '0', '0', '1'},
                        {'0', '0', '1', '1'},
                        {'0', '0', '0', '0'}
                };
        System.out.println(numIslands(grid)); // output: 2
    }
}
