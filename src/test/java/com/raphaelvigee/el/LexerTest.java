package com.raphaelvigee.el;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LexerTest
{
    private final String expression;

    private final Token[] expected;

    private Lexer lexer;

    public LexerTest(String expression, Token[] expected)
    {
        this.expression = expression;
        this.expected = expected;
    }

    @Before
    public void setUp()
    {
        lexer = new Lexer();
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]{
                {
                        "   a    ",
                        a(new Token<>("a", TokenType.NAME_TYPE, 4))
                },
                {
                        "a",
                        a(new Token<>("a", TokenType.NAME_TYPE, 1))
                },
                {
                        "\"foo\"",
                        a(new Token<>("foo", TokenType.STRING_TYPE, 1))
                },
                {
                        "3",
                        a(new Token<>(3, TokenType.INT_TYPE, 1))
                },
                {
                        "-3",
                        a(
                                new Token<>("-", TokenType.OPERATOR_TYPE, 1),
                                new Token<>(3, TokenType.INT_TYPE, 2)
                        )
                },
                {
                        "537346.3420845",
                        a(
                                new Token<>(537346.3420845, TokenType.DOUBLE_TYPE, 1)
                        )

                },
                {
                        "+",
                        a(new Token<>("+", TokenType.OPERATOR_TYPE, 1))
                },
                {
                        ".",
                        a(new Token<>(".", TokenType.PUNCTUATION_TYPE, 1))

                },
                {
                        "..",
                        a(new Token<>("..", TokenType.OPERATOR_TYPE, 1))
                },
                {
                        "\"#foo\"",
                        a(new Token<>("#foo", TokenType.STRING_TYPE, 1))
                },
                {
                        "'#foo'",
                        a(new Token<>("#foo", TokenType.STRING_TYPE, 1))
                },
                {
                        "''",
                        a(new Token<>("", TokenType.STRING_TYPE, 1))
                },
                {
                        "'\''",
                        a(new Token<>("'", TokenType.STRING_TYPE, 1))
                },
                {
                        "(3 + 5) ~ foo(\"bar\").baz[4]",
                        a(
                                new Token<>("(", TokenType.PUNCTUATION_TYPE, 1),
                                new Token<>(3, TokenType.INT_TYPE, 2),
                                new Token<>("+", TokenType.OPERATOR_TYPE, 4),
                                new Token<>(5, TokenType.INT_TYPE, 6),
                                new Token<>(")", TokenType.PUNCTUATION_TYPE, 7),
                                new Token<>("~", TokenType.OPERATOR_TYPE, 9),
                                new Token<>("foo", TokenType.NAME_TYPE, 11),
                                new Token<>("(", TokenType.PUNCTUATION_TYPE, 14),
                                new Token<>("bar", TokenType.STRING_TYPE, 15),
                                new Token<>(")", TokenType.PUNCTUATION_TYPE, 20),
                                new Token<>(".", TokenType.PUNCTUATION_TYPE, 21),
                                new Token<>("baz", TokenType.NAME_TYPE, 22),
                                new Token<>("[", TokenType.PUNCTUATION_TYPE, 25),
                                new Token<>(4, TokenType.INT_TYPE, 26),
                                new Token<>("]", TokenType.PUNCTUATION_TYPE, 27)
                        )
                },
        });
    }

    @Test
    public void test()
    {
        TokenStream stream = lexer.tokenize(this.expression);

        List<Token> tokens = stream.getTokens();

        assertEquals(TokenType.EOF_TYPE, tokens.get(tokens.size() - 1).type);

        Token[] tokensArray = tokens.toArray(new Token[tokens.size()]);

        Token[] tokensArrayNoEOF = Arrays.copyOf(tokensArray, tokensArray.length - 1);

        assertArrayEquals(expected, tokensArrayNoEOF);
    }

    private static Token[] a(Token... args)
    {
        return args;
    }
}
