package com.raphaelvigee.el;

import com.raphaelvigee.el.Exception.SyntaxError;

import java.util.List;
import java.util.stream.Collectors;

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

    public void expect(TokenType type, String value, String message)
    {
        expect(type, value);
    }

    public void expect(TokenType type, String value)
    {
        Token token = getCurrent();

        if (!token.test(type, value)) {
            throw new SyntaxError("Unexpected token");
        }

        next();
    }

    public Token getCurrent()
    {
        return tokens.get(position);
    }

    public boolean isEOF()
    {
        return TokenType.EOF_TYPE == getCurrent().type;
    }

    public String getExpression()
    {
        return expression;
    }

    @Override
    public String toString()
    {
        List<String> tokensStr = tokens.stream().map(Token::toString).collect(Collectors.toList());

        return String.join("\n", tokensStr);
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
