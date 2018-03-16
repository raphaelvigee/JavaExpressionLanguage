package com.raphaelvigee.el.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArgumentsNode extends ListNode
{
    public ArgumentsNode()
    {
        super(LinkedList.class);
    }

    public ArgumentsNode(List<Node> args)
    {
        this();
        for (Node arg : args) {
            addElement(arg);
        }
    }

    public Object[] evaluateArray(Map<String, Object> env)
    {
        return entries
                .stream()
                .map(node -> node.evaluate(env))
                .toArray();
    }
}
