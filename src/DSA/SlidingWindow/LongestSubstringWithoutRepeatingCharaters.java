package DSA.SlidingWindow;

import java.util.HashSet;
import java.util.Set;

/*
Given a string s, find the length of the longest substring without repeating characters.

https://leetcode.com/problems/longest-substring-without-repeating-characters/description/
Input: "abcabcbb"
Output: 3 ("abc")
 */
public class LongestSubstringWithoutRepeatingCharaters {

    public static int lengthOfLongestSubstring(String s) {
        Set<Character> windowChars = new HashSet<>();

        int left = 0;
        int maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char currChar = s.charAt(right);

            // if current character already exists in window, the window is invalid
            // shink the window from left and expand from right
            while (windowChars.contains(currChar)) {
                windowChars.remove(s.charAt(left));
                left++;
            }
            windowChars.add(currChar); // add chars at right poisition once duplicate is removed

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstring("abcabcbb")); // 3
    }
}
