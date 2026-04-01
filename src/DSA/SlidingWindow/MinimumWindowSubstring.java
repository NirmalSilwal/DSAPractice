package DSA.SlidingWindow;

import java.util.HashMap;
import java.util.Map;

/*
Given two strings s and t, return the smallest substring of s that contains all characters of t including duplicates.
Input: s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"

https://leetcode.com/problems/minimum-window-substring/description/
 */
public class MinimumWindowSubstring {

    public String minWindow(String s, String t) {

        if (s == null || t == null || t.length() > s.length()) return "";

        Map<Character, Integer> need = new HashMap<>(); // frequency of t which you need to form substring answer
        for (char ch : t.toCharArray()) {
            need.put(ch, need.getOrDefault(ch, 0) + 1);
        }

        int left = 0, right  = 0;
        Map<Character, Integer> window = new HashMap<>();
        int required = need.size(), formed = 0;

        int minLen = Integer.MAX_VALUE, startIndex = 0;

        while (right < s.length()) {
            char currCh = s.charAt(right);
            window.put(currCh, window.getOrDefault(currCh, 0) + 1);

            // see if current character is of our interest to form a substring
            if (need.containsKey(currCh) && window.get(currCh).intValue() == need.get(currCh).intValue()) {
                formed++;
            }

            // shrink window from left
            while (left <= right && formed == required) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    startIndex = left;
                }
                char leftChar = s.charAt(left);
                window.put(leftChar, window.get(leftChar) - 1); // remove leftmost char from window
                if (need.containsKey(leftChar) && window.get(leftChar) < need.get(leftChar)) {
                    formed--;
                }
                left++;
            }
            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(startIndex, startIndex + minLen);
    }
}
