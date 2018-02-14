package com.raphaelvigee.el;

import java.util.Objects;

public class Token<T>
{
    public T value;

    public TokenType type;

    public int cursor;

    public Token(T value, TokenType type, int cursor)
    {
        this.value = value;
        this.type = type;
        this.cursor = cursor;
    }

    public boolean test(TokenType type)
    {
        return test(type, null);
    }

    public boolean test(TokenType type, String value)
    {
        return this.type == type && (null == value || Objects.equals(this.value, value));
    }

    public String stringValue()
    {
        return String.valueOf(value);
    }

    public String toString()
    {
        return String.format("%3d %s %s", this.cursor, this.type, this.value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Token<?> token = (Token<?>) o;
        return cursor == token.cursor &&
                Objects.equals(value, token.value) &&
                type == token.type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(value, type, cursor);
    }
}
