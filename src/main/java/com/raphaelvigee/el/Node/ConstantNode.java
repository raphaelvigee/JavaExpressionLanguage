package com.raphaelvigee.el.Node;

public class ConstantNode extends Node
{
    private boolean isIdentifier;

    public ConstantNode(Object value)
    {
        this(value, false);
    }

    public ConstantNode(Object value, boolean isIdentifier)
    {
        super();
        this.isIdentifier = isIdentifier;

        attributes.put("value", value);
    }

    @Override
    public Object evaluate()
    {
        return attributes.get("value");
    }
}
