package DSA.HashMap;

import java.util.Map;
import java.util.HashMap;

class CountCharacters {
    public static void main(String[] args) {
        String str = "This is an actual Test";
        Map<Character, Integer> countMap = getCharCount(str);

        for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
            Character key = entry.getKey();
            Integer value = entry.getValue();

            System.out.println(key + " : " + value);
        }

    }

    public static Map<Character, Integer> getCharCount(String str) {
        Map<Character, Integer> countMap = new HashMap<>();
        if (str == null || str.length() == 0) return countMap;

        for (char ch : str.toCharArray()) {
            if (ch != ' ') {
                countMap.put(ch, countMap.getOrDefault(ch, 0) + 1);
            }
        }

        return countMap;
    }

}