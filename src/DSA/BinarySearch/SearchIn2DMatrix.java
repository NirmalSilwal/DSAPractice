package DSA.BinarySearch;

public class SearchIn2DMatrix {

    // O(log(m * n)) time where m is total rows and n is total columns
    public static boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return false;

        int rows = matrix.length;
        int cols = matrix[0].length;

        int left = 0, right = rows * cols - 1;

        while (rows <= cols) {
            int mid = left + (right - left) / 2;

            int currentRow = mid / cols;
            int currentCol = mid % cols;

            int value = matrix[currentRow][currentCol];
            if (value == target) {
                return true;
            } else if (value < target) {
                left = mid + 1;
            } else {
               right = mid - 1;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int[][] matrix = {
                {1,3,5,7},
                {10,11,16,20},
                {23,30,34,60}
        };
        int target = 3;
        System.out.println(searchMatrix(matrix, target));
    }
}
