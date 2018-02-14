package com.raphaelvigee.el.Node;

public class ConditionalNode extends Node
{
    public ConditionalNode(Node expr1, Node expr2, Node expr3)
    {
        super();
        nodes.put("expr1", expr1);
        nodes.put("expr2", expr2);
        nodes.put("expr3", expr3);
    }

    @Override
    public Object evaluate()
    {
        Node expr1 = nodes.get("expr1");
        Node expr2 = nodes.get("expr2");
        Node expr3 = nodes.get("expr3");

        return expr1.evaluate() ? expr2.evaluate() : expr3.evaluate();
    }
}
