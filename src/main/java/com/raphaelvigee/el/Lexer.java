package com.raphaelvigee.el;

import com.raphaelvigee.el.Exception.SyntaxError;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer
{
    private final static Pattern NAMES = Pattern.compile("^[\\da-zA-Z0-9_$]+");

    private final static Pattern INT = Pattern.compile("^\\d+");

    private final static Pattern DOUBLE = Pattern.compile("^[0-9]*\\.[0-9]+");

    private final static Pattern OPERATORS = Pattern.compile("^(?:not in(?=[\\s(])|\\!\\=\\=|not(?=[\\s(])|and(?=[\\s(])|\\=\\=\\=|\\>\\=|or(?=[\\s(])|\\<\\=|\\*\\*|\\.\\.|in(?=[\\s(])|&&|\\|\\||matches|\\=\\=|\\!\\=|\\*|~|%|\\/|\\>|\\||\\!|\\^|&|\\+|\\<|\\-)");

    private final static Pattern STRING = Pattern.compile("^(?:\"([^\"]*(?:\\.[^\"]*)*)\"|'([^']*(?:\\.[^']*)*)')");

    private final static Pattern TEMPLATE_STRING = Pattern.compile("^`([^`]*(?:\\.[^`]*)*)`");

    private final static Pattern TEMPLATE_STRING_CHILD = Pattern.compile("\\$\\{(.+)}");

    private final static String[] OPENING_BRACKETS = Arrays.stream(Bracket.Type.values()).map(Bracket.Type::getOpen).toArray(String[]::new);

    private final static String[] CLOSING_BRACKETS = Arrays.stream(Bracket.Type.values()).map(Bracket.Type::getClose).toArray(String[]::new);

    private final static String[] PUNCTUATIONS = {".", ",", "?", ":", "#"};

    static class BracketDefinition
    {
        private Bracket bracket;

        private int cursor;

        public BracketDefinition(Bracket bracket, int cursor)
        {
            this.bracket = bracket;
            this.cursor = cursor;
        }

        public Bracket getBracket()
        {
            return bracket;
        }

        public int getCursor()
        {
            return cursor;
        }
    }

    public TokenStream tokenize(String expression)
    {
        return tokenize(expression, 0, expression.length(), true);
    }

    public TokenStream tokenize(String expression, int from, int end, boolean addEOF)
    {
        expression = expression.replace("\\r", " ");
        expression = expression.replace("\\n", " ");
        expression = expression.replace("\\t", " ");
        expression = expression.replace("\\v", " ");
        expression = expression.replace("\\f", " ");

        int cursor = from;
        List<Token> tokens = new LinkedList<>();
        Stack<BracketDefinition> brackets = new Stack<>();

        while (cursor < end) {
            Character currentChar = expression.charAt(cursor);
            String current = String.valueOf(currentChar);

            if (currentChar.equals(' ')) {
                cursor++;

                continue;
            }

            String candidate = expression.substring(cursor);

            Matcher intMatcher = INT.matcher(candidate);
            Matcher doubleMatcher = DOUBLE.matcher(candidate);
            Matcher stringMatcher = STRING.matcher(candidate);
            Matcher templateStringMatcher = TEMPLATE_STRING.matcher(candidate);
            Matcher operatorsMatcher = OPERATORS.matcher(candidate);
            Matcher namesMatcher = NAMES.matcher(candidate);

            if (doubleMatcher.find()) {
                String match = doubleMatcher.group(0);

                Double value = Double.valueOf(match);

                tokens.add(new Token<>(value, TokenType.DOUBLE, cursor + 1));
                cursor += match.length();
            } else if (intMatcher.find()) {
                String match = intMatcher.group(0);

                Integer value = Integer.valueOf(match);

                tokens.add(new Token<>(value, TokenType.INT, cursor + 1));
                cursor += match.length();
            } else if (contains(OPENING_BRACKETS, current)) {
                brackets.add(new BracketDefinition(new Bracket(current), cursor));

                tokens.add(new Token<>(current, TokenType.PUNCTUATION, cursor + 1));
                cursor++;
            } else if (contains(CLOSING_BRACKETS, current)) {
                if (brackets.isEmpty()) {
                    throw new SyntaxError(String.format("Unexpected \"%s\"", current), cursor, expression);
                }

                BracketDefinition lastBracketDefinition = brackets.pop();

                String expected = lastBracketDefinition.getBracket().opposite();

                if (!current.equals(expected)) {
                    throw new SyntaxError(String.format("Unclosed \"%s\"", lastBracketDefinition.getBracket().getValue()), lastBracketDefinition.getCursor(), expression);
                }

                tokens.add(new Token<>(current, TokenType.PUNCTUATION, cursor + 1));
                cursor++;
            } else if (stringMatcher.find()) {
                String match = stringMatcher.group(0);
                String value = stringMatcher.group(match.startsWith("\"") ? 1 : 2);

                // TODO: Remove " and ' escape

                tokens.add(new Token<>(value, TokenType.STRING, cursor + 1));
                cursor += match.length();
            } else if (templateStringMatcher.find()) {
                String match = templateStringMatcher.group(0);
                String value = templateStringMatcher.group(1);

                // TODO: Remove ` escape

                int childCursor = cursor;
                childCursor++; // `

                Matcher matcher = TEMPLATE_STRING_CHILD.matcher(value);
                String[] strings = TEMPLATE_STRING_CHILD.split(value, Integer.MAX_VALUE);

                tokens.add(new Token<>(strings[0], TokenType.STRING, cursor));
                childCursor += strings[0].length();

                int i = 1;
                while (matcher.find()) {
                    String childExpression = matcher.group(1);

                    tokens.add(new Token<>("~", TokenType.OPERATOR, cursor));
                    tokens.add(new Token<>("(", TokenType.PUNCTUATION, cursor));

                    childCursor += 2; // ${
                    TokenStream childStream = tokenize(expression, childCursor, childCursor + childExpression.length(), false);

                    tokens.addAll(childStream.getTokens());

                    childCursor += childExpression.length();
                    childCursor++; // }

                    tokens.add(new Token<>(")", TokenType.PUNCTUATION, cursor));
                    tokens.add(new Token<>("~", TokenType.OPERATOR, cursor));

                    String string = strings[i++];
                    tokens.add(new Token<>(string, TokenType.STRING, cursor));

                    childCursor += string.length();
                }

                cursor += match.length();
            } else if (operatorsMatcher.find()) {
                String match = operatorsMatcher.group(0);

                tokens.add(new Token<>(match, TokenType.OPERATOR, cursor + 1));
                cursor += match.length();
            } else if (contains(PUNCTUATIONS, current)) {
                tokens.add(new Token<>(current, TokenType.PUNCTUATION, cursor + 1));
                cursor++;
            } else if (namesMatcher.find()) {
                String match = namesMatcher.group(0);

                tokens.add(new Token<>(match, TokenType.NAME, cursor + 1));
                cursor += match.length();
            } else {
                throw new SyntaxError(String.format("Unexpected character \"%s\"", current), cursor, expression);
            }
        }

        if (addEOF) {
            tokens.add(new Token<>(null, TokenType.EOF, cursor + 1));
        }

        if (!brackets.isEmpty()) {
            BracketDefinition latest = brackets.pop();
            throw new SyntaxError(String.format("Unclosed \"%s\"", latest.getBracket()), latest.getCursor(), expression);
        }

        return new TokenStream(tokens, expression);
    }

    public static <T> boolean contains(final T[] array, final T v)
    {
        for (final T e : array)
            if (e == v || v != null && v.equals(e))
                return true;

        return false;
    }
}
