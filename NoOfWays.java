/* Dynamic Programming (DP) Subproblem Definition
The essence of the solution involves breaking down the larger problem 
(constructing the entire target string) into smaller, manageable problems.
 Each subproblem focuses on determining all possible ways to form a specific 
 prefix of the target string using the words provided in the wordbank. This 
 breakdown into subproblems allows the solution to build incrementally, starting 
 from the smallest prefix and extending to the full length of the target string. */

/* DP Decisions
 For each prefix of the target string, the program evaluates each word in the wordbank to 
see if it matches the start of the prefix. If a word matches, it is considered as a potential 
starting piece of the construction, and the remaining part of the target string (after this word)
becomes the new problem to solve. This is a classic divide-and-conquer approach where the original
 problem is divided into smaller pieces each time a word is successfully matched. */

/* Recursion
The program uses recursion to explore all possible constructions. When a word from the 
wordbank is used as a prefix, the function is recursively called with the remainder of the 
target string, subtracting the prefix that matched. This recursive call continues until the 
entire target string is either constructed or determined to be impossible to construct with 
the given words. During recursion, every combination of words that can potentially form the target is explored. */

/* Memoization
To enhance efficiency, memoization is utilized. This technique stores the results of previously solved
subproblems in a cache (often implemented as a hash table or dictionary). If a subproblem reoccurs, instead
of recalculating it, the result is retrieved from the cache. This drastically reduces the computation time, 
especially in cases where there are many overlapping subproblems, which is common in string manipulation tasks. */


/* Base Case
The base case in this context occurs when the target string becomes empty. This happens after successively 
subtracting prefixes that match words in the wordbank, and it signals that a successful combination of words has 
been found to construct the original target string. The function needs to recognize this condition and handle it 
appropriately to ensure correct and efficient computation. */

/*How the Base Case Works
Empty Target String: When the recursive function is called with an empty target string, it indicates that the preceding
series of word selections have completely and exactly formed the target. This is a direct result of all previous decisions
in the recursion chain correctly matching the entire target string with words from the wordbank.

Return Value: In this scenario, the function should return a list containing an empty list. 
This specific return format is important because it represents a valid pathway of construction: a combination that led 
to the target string being fully constructed. The empty list inside the main list is a placeholder that symbolizes the end 
of a successful construction path.

Integration in the Recursive Approach
Here’s how the base case integrates into the overall recursive approach:

Check for Empty Target: At the beginning of the recursive function, check if the target string is empty.
Handle the Base Case: If the target is empty, return [[]] to signify that this path of recursion has successfully constructed the target using words from the wordbank.
Proceed if Not Base Case: If the target is not empty, the function continues to check each word in the wordbank to see if it matches the beginning of the current target substring.
Recursive Decomposition: For each matching word, the function recursively calls itself with the remainder of the target string (the target minus the matched prefix). It accumulates all successful combinations from these recursive calls.
Memoization: Results of recursive calls (including those from the base case scenario) are stored in a memoization cache to avoid redundant calculations for the same substring in future calls.*/



import java.util.*;
import java.util.stream.Collectors;

public class NoOfWays {

    // Method to find all combinations to form the target using words from the wordbank.
    static List<List<String>> combine(String target, String[] wordBank, HashMap<String, List<List<String>>> memo) {
        // Check if the result for this target has already been calculated and cached.
        if (memo.containsKey(target)) {
            return memo.get(target);
        }
        // Check for an empty word in the wordbank and handle edge cases.
        if (wordBank.length == 1 && wordBank[0].isEmpty()) {
            return new ArrayList<>(); // Return an empty list if wordbank is effectively empty.
        }

        // Base case: if the target string is empty, return a list containing an empty list.
        if (target.isEmpty()) {
            List<List<String>> baseResult = new ArrayList<>();
            baseResult.add(new ArrayList<>());
            return baseResult;
        }
        List<List<String>> result = new ArrayList<>();

        // Iterate over each word in the word bank to see if it matches the start of the target string.
        for (String word : wordBank) {
            if (target.startsWith(word)) {
                // Get the substring of the target after removing the word.
                String con = target.substring(word.length());
                // Recursive call to process the remainder of the target string.
                List<List<String>> suffixWays = combine(con, wordBank, memo);
                List<List<String>> targetWays = new ArrayList<>();
                for (List<String> suffixWay : suffixWays) {
                    List<String> targetWay = new ArrayList<>(List.of(word));
                    targetWay.addAll(suffixWay); // Add the current word to the beginning of each combination found in the recursive call.
                    targetWays.add(targetWay);
                }
                result.addAll(targetWays);
            }
        }

        // Cache the result for this target to avoid recomputation in the future.
        memo.put(target, result);
        return result;
    }

    // Utility method to check if a string contains only valid characters.
    private static boolean isValidString(String input) {
        return input.matches("[a-zA-Z0-9 ]*"); // Regular expression for valid characters.
    }

    public static void main(String[] args) {
        long start = System.nanoTime(); // Start timing the execution.

        HashMap<String, String> parameters = new HashMap<>();
        List<String> wordBankList = new ArrayList<>();
        String key = null;

        // Parse command-line arguments.
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    if (key != null && key.equals("wordbank")) {
                        parameters.put(key, String.join(" ", wordBankList));
                        wordBankList.clear();
                    }
                    key = args[i].substring(1);
                } else if (key != null) {
                    if (key.equals("wordbank")) {
                        wordBankList.add(args[i]);
                    } else {
                        parameters.put(key, args[i]);
                        key = null;
                    }
                }
            }
            if (key != null && key.equals("wordbank")) {
                parameters.put(key, String.join(" ", wordBankList));
            }

            // Validate required parameters.
            if (parameters.isEmpty() || !parameters.containsKey("target") || !parameters.containsKey("wordbank")) {
                throw new IllegalArgumentException(
                        "Required arguments: -target <target_string> -wordbank <word1 word2 ...>");
            }

            // Process the target string by removing spaces and converting to lowercase.
            String target = parameters.get("target").toLowerCase().replaceAll("\\s+", "");
            // Split and process the wordbank by converting to lowercase.
            String[] wordBank = parameters.get("wordbank").toLowerCase().split(" ");

            // Validate the target and wordbank for invalid characters.
            if (!isValidString(target)) {
                throw new IllegalArgumentException("Target contains invalid characters.");
            }

            for (String word : wordBank) {
                if (!isValidString(word)) {
                    throw new IllegalArgumentException("Word bank contains invalid characters.");
                }
            }

            // Initialize memoization storage.
            HashMap<String, List<List<String>>> memo = new HashMap<>();
            HashMap<String, Integer> memo1 = new HashMap<>();

            // Calculate all combinations using the combine method.
            List<List<String>> combinations = combine(target, wordBank, memo);
            System.out.println("Number of ways: " + combinations.size());
            System.out.println("[");
            for (List<String> combination : combinations) {
                System.out.print("   [ ");
                System.out.print(String.join(", ",
                        combination.stream().map(word -> "\"" + word + "\"").collect(Collectors.toList())));
                System.out.println(" ]");
            }
            System.out.println("]");
            long end = System.nanoTime();
            // Calculate execution time in seconds.
            double exeTime = (end - start) / 1_000_000_000.0;
            System.out.println("Runtime: " + String.format("%.4f", exeTime) + " seconds");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }
}

