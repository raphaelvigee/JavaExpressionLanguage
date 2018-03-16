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
                        a(new Token<>("a", TokenType.NAME, 4))
                },
                {
                        "a",
                        a(new Token<>("a", TokenType.NAME, 1))
                },
                {
                        "\"foo\"",
                        a(new Token<>("foo", TokenType.STRING, 1))
                },
                {
                        "3",
                        a(new Token<>(3, TokenType.INT, 1))
                },
                {
                        "-3",
                        a(
                                new Token<>("-", TokenType.OPERATOR, 1),
                                new Token<>(3, TokenType.INT, 2)
                        )
                },
                {
                        "537346.3420845",
                        a(
                                new Token<>(537346.3420845, TokenType.DOUBLE, 1)
                        )

                },
                {
                        "+",
                        a(new Token<>("+", TokenType.OPERATOR, 1))
                },
                {
                        ".",
                        a(new Token<>(".", TokenType.PUNCTUATION, 1))

                },
                {
                        "..",
                        a(new Token<>("..", TokenType.OPERATOR, 1))
                },
                {
                        "\"#foo\"",
                        a(new Token<>("#foo", TokenType.STRING, 1))
                },
                {
                        "'#foo'",
                        a(new Token<>("#foo", TokenType.STRING, 1))
                },
                {
                        "''",
                        a(new Token<>("", TokenType.STRING, 1))
                },
                {
                        "(3 + 5) ~ foo(\"bar\").baz[4]",
                        a(
                                new Token<>("(", TokenType.PUNCTUATION, 1),
                                new Token<>(3, TokenType.INT, 2),
                                new Token<>("+", TokenType.OPERATOR, 4),
                                new Token<>(5, TokenType.INT, 6),
                                new Token<>(")", TokenType.PUNCTUATION, 7),
                                new Token<>("~", TokenType.OPERATOR, 9),
                                new Token<>("foo", TokenType.NAME, 11),
                                new Token<>("(", TokenType.PUNCTUATION, 14),
                                new Token<>("bar", TokenType.STRING, 15),
                                new Token<>(")", TokenType.PUNCTUATION, 20),
                                new Token<>(".", TokenType.PUNCTUATION, 21),
                                new Token<>("baz", TokenType.NAME, 22),
                                new Token<>("[", TokenType.PUNCTUATION, 25),
                                new Token<>(4, TokenType.INT, 26),
                                new Token<>("]", TokenType.PUNCTUATION, 27)
                        )
                },
        });
    }

    @Test
    public void test()
    {
        TokenStream stream = lexer.tokenize(this.expression);

        List<Token> tokens = stream.getTokens();

        assertEquals(TokenType.EOF, tokens.get(tokens.size() - 1).type);

        Token[] tokensArray = tokens.toArray(new Token[tokens.size()]);

        Token[] tokensArrayNoEOF = Arrays.copyOf(tokensArray, tokensArray.length - 1);

        assertArrayEquals(expected, tokensArrayNoEOF);
    }

    private static Token[] a(Token... args)
    {
        return args;
    }
}
