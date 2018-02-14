package com.raphaelvigee.el.Node;

import java.util.Map;

public class ConstantNode extends Node<Object>
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
    public Object evaluate(Map<String, Object> env)
    {
        return attributes.get("value");
    }
}
