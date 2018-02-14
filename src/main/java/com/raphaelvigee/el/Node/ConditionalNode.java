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
}
