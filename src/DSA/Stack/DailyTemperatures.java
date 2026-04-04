package DSA.Stack;

/*
Given an array temperatures, return an array answer such that:

answer[i] = number of days you have to wait after day i to get a warmer temperature
if there is no future warmer day, answer[i] = 0
Example
Input:  [73,74,75,71,69,72,76,73]
Output: [1,1,4,2,1,1,0,0]
 */

import java.util.Arrays;
import java.util.Stack;

public class DailyTemperatures {

    public static int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];

        // store indices
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < n; i++) {

            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevIndex = stack.pop();
                answer[prevIndex] = i - prevIndex;
            }
            stack.push(i);
        }

        return answer;
    }

    public static void main(String[] args) {
        int[] temperatures = {73,74,75,71,69,72,76,73};
        System.out.println(Arrays.toString(dailyTemperatures(temperatures)));
    }
}
