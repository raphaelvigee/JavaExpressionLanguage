package com.raphaelvigee.el.Node;

public class FunctionNode extends Node
{
    public FunctionNode(String name, Node arguments)
    {
        super();

        nodes.put("arguments", arguments);
        attributes.put("name", name);
    }
}