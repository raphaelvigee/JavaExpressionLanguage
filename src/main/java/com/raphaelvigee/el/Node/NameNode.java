package com.raphaelvigee.el.Node;

import java.util.Map;

public class NameNode extends Node<Object>
{
    public NameNode(String name)
    {
        attributes.put("name", name);
    }

    @Override
    public Object evaluate(Map<String, Object> env)
    {
        String name = (String) attributes.get("name");

        return env.get(name);
    }
}
