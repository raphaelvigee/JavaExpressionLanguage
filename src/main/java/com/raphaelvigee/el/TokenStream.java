package com.raphaelvigee.el;

import com.raphaelvigee.el.Exception.SyntaxError;

import java.util.Arrays;
import java.util.List;

public class TokenStream
{
    private List<Token> tokens;

    private int position = 0;

    private String expression;

    public TokenStream(List<Token> tokens, String expression)
    {
        this.tokens = tokens;
        this.expression = expression;
    }

    public void next()
    {
        position++;

        try {
            tokens.get(position);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxError("Unexpected end of expression", getCurrent().cursor, expression);
        }
    }

    public Token getNext()
    {
        return tokens.get(position + 1);
    }

    public void expect(TokenType type, String value)
    {
        expect(type, value, null);
    }

    public void expect(TokenType type, String value, String message)
    {
        Token token = getCurrent();

        if (!token.test(type, value)) {
            throw new SyntaxError(String.format(
                    "%sUnexpected token \"%s\" of value \"%s\" (\"%s\" expected%s)",
                    message != null ? message + ". " : "",
                    token.type,
                    token.value,
                    type,
                    value != null ? String.format(" with value \"%s\"", value) : ""
            ), token.cursor, expression);
        }

        next();
    }

    public Token getCurrent()
    {
        return tokens.get(position);
    }

    public boolean isEOF()
    {
        return TokenType.EOF == getCurrent().type;
    }

    public String getExpression()
    {
        return expression;
    }

    @Override
    public String toString()
    {
        return String.join("\n", Arrays.asList(toArray()));
    }

    public String[] toArray()
    {
        return tokens.stream().map(Token::toString).toArray(String[]::new);
    }

    public List<Token> getTokens()
    {
        return tokens;
    }
}
