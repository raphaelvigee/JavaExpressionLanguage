package com.raphaelvigee.el.Exception;

import java.util.Set;

public class SyntaxError extends RuntimeException
{
    public SyntaxError(String message)
    {
        this(message, 0, null);
    }

    public SyntaxError(String message, int cursor, String expression)
    {
        this(message, cursor, expression, null, null);
    }

    public SyntaxError(String message, int cursor, String expression, Object value, Set<String> function)
    {
        super(message);
    }
}
