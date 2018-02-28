package com.raphaelvigee.el;

public class Bracket
{
    enum Type
    {
        PARENTHESIS("(", ")"), CURLY("{", "}"), SQUARED("[", "]");

        private final String open;

        private final String close;

        Type(String open, String close)
        {
            this.open = open;
            this.close = close;
        }

        public static Type getType(String c)
        {
            for (Type type : Type.values()) {
                if (type.getOpen().equals(c) || type.getClose().equals(c)) {
                    return type;
                }
            }

            throw new RuntimeException("Unhandled character");
        }

        public String getOpen()
        {
            return open;
        }

        public String getClose()
        {
            return close;
        }
    }

    private Type type;

    private String value;

    public Bracket(String value)
    {
        this.setValue(value);
        this.setType(Type.getType(value));
    }

    public String opposite()
    {
        if (getType().getOpen().equals(getValue())) {
            return getType().getClose();
        }

        return getType().getOpen();
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return getValue();
    }
}
