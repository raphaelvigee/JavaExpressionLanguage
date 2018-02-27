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
    private final static Pattern NAMES = Pattern.compile("^[\\da-zA-Z0-9_]+");

    private final static Pattern INT = Pattern.compile("^\\d+");

    private final static Pattern DOUBLE = Pattern.compile("^[0-9]*\\.[0-9]+");

    private final static Pattern OPERATORS = Pattern.compile("^(?:not in(?=[\\s(])|\\!\\=\\=|not(?=[\\s(])|and(?=[\\s(])|\\=\\=\\=|\\>\\=|or(?=[\\s(])|\\<\\=|\\*\\*|\\.\\.|in(?=[\\s(])|&&|\\|\\||matches|\\=\\=|\\!\\=|\\*|~|%|\\/|\\>|\\||\\!|\\^|&|\\+|\\<|\\-)");

    private final static Pattern STRING = Pattern.compile("^(?:\"([^\"]*(?:\\.[^\"]*)*)\"|'([^']*(?:\\.[^']*)*)')");

    private final static Pattern TEMPLATE_STRING = Pattern.compile("^`([^`]*(?:\\.[^`]*)*)`");

    static class BracketDefinition
    {
        Bracket bracket;

        int cursor;

        public BracketDefinition(Bracket bracket, int cursor)
        {
            this.bracket = bracket;
            this.cursor = cursor;
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

            String[] openingBrackets = Arrays.stream(Bracket.Type.values()).map(type -> type.open).toArray(String[]::new);
            String[] closingBrackets = Arrays.stream(Bracket.Type.values()).map(type -> type.close).toArray(String[]::new);
            String[] punctuations = {".", ",", "?", ":"};

            if (doubleMatcher.find()) {
                String match = doubleMatcher.group(0);

                Double value = Double.valueOf(match);

                tokens.add(new Token<>(value, TokenType.DOUBLE_TYPE, cursor + 1));
                cursor += match.length();
            } else if (intMatcher.find()) {
                String match = intMatcher.group(0);

                Integer value = Integer.valueOf(match);

                tokens.add(new Token<>(value, TokenType.INT_TYPE, cursor + 1));
                cursor += match.length();
            } else if (contains(openingBrackets, current)) {
                brackets.add(new BracketDefinition(new Bracket(current), cursor));

                tokens.add(new Token<>(current, TokenType.PUNCTUATION_TYPE, cursor + 1));
                cursor++;
            } else if (contains(closingBrackets, current)) {
                if (brackets.isEmpty()) {
                    throw new SyntaxError(String.format("Unexpected \"%s\"", current), cursor, expression);
                }

                BracketDefinition lastBracketDefinition = brackets.pop();

                String expected = lastBracketDefinition.bracket.opposite();

                if (!current.equals(expected)) {
                    throw new SyntaxError(String.format("Unclosed \"%s\"", lastBracketDefinition.bracket.bracket), lastBracketDefinition.cursor, expression);
                }

                tokens.add(new Token<>(current, TokenType.PUNCTUATION_TYPE, cursor + 1));
                cursor++;
            } else if (stringMatcher.find()) {
                String match = stringMatcher.group(0);
                String value = stringMatcher.group(match.startsWith("\"") ? 1 : 2);

                // TODO: Remove " and ' escape

                tokens.add(new Token<>(value, TokenType.STRING_TYPE, cursor + 1));
                cursor += match.length();
            } else if (templateStringMatcher.find()) {
                String match = templateStringMatcher.group(0);
                String value = templateStringMatcher.group(1);

                // TODO: Remove ` escape
                int childCursor = cursor;
                childCursor++; // `

                String childReg = "\\$\\{(.+)}";
                Pattern reg = Pattern.compile(childReg);
                Matcher matcher = reg.matcher(value);
                String[] strings = value.split(childReg, Integer.MAX_VALUE);

                List<Token> templateTokens = new LinkedList<>();

                templateTokens.add(new Token<>(strings[0], TokenType.STRING_TYPE, cursor));
                childCursor += strings[0].length();

                int i = 1;
                while (matcher.find()) {
                    String childExpression = matcher.group(1);

                    templateTokens.add(new Token<>("~", TokenType.OPERATOR_TYPE, cursor));
                    templateTokens.add(new Token<>("(", TokenType.PUNCTUATION_TYPE, cursor));

                    childCursor += 2; // ${
                    TokenStream childStream = tokenize(expression, childCursor, childCursor + childExpression.length(), false);

                    templateTokens.addAll(childStream.getTokens());

                    childCursor += childExpression.length();
                    childCursor++; // }

                    templateTokens.add(new Token<>(")", TokenType.PUNCTUATION_TYPE, cursor));
                    templateTokens.add(new Token<>("~", TokenType.OPERATOR_TYPE, cursor));

                    String string = strings[i++];
                    templateTokens.add(new Token<>(string, TokenType.STRING_TYPE, cursor));

                    childCursor += string.length();
                }

                tokens.addAll(templateTokens);

                cursor += match.length();
            } else if (operatorsMatcher.find()) {
                String match = operatorsMatcher.group(0);

                tokens.add(new Token<>(match, TokenType.OPERATOR_TYPE, cursor + 1));
                cursor += match.length();
            } else if (contains(punctuations, current)) {
                tokens.add(new Token<>(current, TokenType.PUNCTUATION_TYPE, cursor + 1));
                cursor++;
            } else if (namesMatcher.find()) {
                String match = namesMatcher.group(0);

                tokens.add(new Token<>(match, TokenType.NAME_TYPE, cursor + 1));
                cursor += match.length();
            } else {
                throw new SyntaxError(String.format("Unexpected character \"%s\"", current), cursor, expression);
            }
        }

        if (addEOF) {
            tokens.add(new Token<>(null, TokenType.EOF_TYPE, cursor + 1));
        }

        if (!brackets.isEmpty()) {
            BracketDefinition latest = brackets.pop();
            throw new SyntaxError(String.format("Unclosed \"%s\"", latest.bracket), latest.cursor, expression);
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
