package com.raphaelvigee.el;

public class Bracket
{
    enum Type
    {
        PARENTHESIS("(", ")"), CURLY("{", "}"), SQUARED("[", "]");

        final String open;

        final String close;

        Type(String open, String close)
        {
            this.open = open;
            this.close = close;
        }

        public static Type getType(String c)
        {
            for (Type type : Type.values()) {
                if (type.open.equals(c) || type.close.equals(c)) {
                    return type;
                }
            }

            throw new RuntimeException("Unhandled character");
        }
    }

    Type type;

    String bracket;

    public Bracket(String bracket)
    {
        this.bracket = bracket;
        this.type = Type.getType(bracket);
    }

    public String opposite()
    {
        if (type.open.equals(bracket)) {
            return type.close;
        }

        return type.open;
    }
}
