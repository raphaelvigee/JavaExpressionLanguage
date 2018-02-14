package com.raphaelvigee.el.Node;

public class GetAttrNode extends Node
{
    public enum CallType
    {
        PROPERTY, METHOD, ARRAY
    }

    public GetAttrNode(Node node, Node attribute, ListNode arguments, CallType type)
    {
        super();

        nodes.put("node", node);
        nodes.put("attribute", attribute);
        nodes.put("arguments", arguments);

        attributes.put("type", type);
    }
}
