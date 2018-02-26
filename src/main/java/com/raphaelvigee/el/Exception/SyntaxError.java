package com.raphaelvigee.el.Exception;

import java.util.Arrays;
import java.util.Set;

public class SyntaxError extends RuntimeException
{
    public SyntaxError(String message, int cursor, String expression)
    {
        this(message, cursor, expression, null, null);
    }

    public SyntaxError(String message, int cursor, String expression, String subject, Set<String> proposals)
    {
        super(buildMessage(message, cursor, expression, subject, proposals));
    }

    public static String buildMessage(String message, int cursor, String expression, String subject, Set<String> proposals)
    {
        message = String.format("%s around position %d", message, cursor);

        if (expression != null) {
            message = String.format("%s for expression `%s`", message, expression);
        }

        message += ".";
        String guess = "";

        int minScore = Integer.MAX_VALUE;
        if (subject != null && proposals != null) {

            for (String proposal : proposals) {
                int distance = levenshtein(subject, proposal);

                if (distance < minScore) {
                    guess = proposal;
                    minScore = distance;
                }
            }
        }

        if (!guess.isEmpty() && minScore < 3) {
            message += String.format(" Did you mean \"%s\"?", guess);
        }

        return message;
    }

    static int levenshtein(String x, String y)
    {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    public static int costOfSubstitution(char a, char b)
    {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers)
    {
        return Arrays.stream(numbers)
                .min().orElse(Integer.MAX_VALUE);
    }
}
