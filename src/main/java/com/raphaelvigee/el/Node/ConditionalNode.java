package com.raphaelvigee.el.Node;

import java.util.Map;

public class ConditionalNode extends Node<Object>
{
    public ConditionalNode(Node expr1, Node expr2, Node expr3)
    {
        super();
        nodes.put("expr1", expr1);
        nodes.put("expr2", expr2);
        nodes.put("expr3", expr3);
    }

    @Override
    public Object evaluate(Map<String, Object> env)
    {
        Node expr1 = nodes.get("expr1");
        Node expr2 = nodes.get("expr2");
        Node expr3 = nodes.get("expr3");

        return (Boolean) expr1.evaluate(env) ? expr2.evaluate(env) : expr3.evaluate(env);
    }
}
