package com.raphaelvigee.el.Node;

import java.util.List;
import java.util.Map;

public class ArgumentsNode extends ListNode
{
    public ArgumentsNode()
    {
    }

    public ArgumentsNode(List<Node> args)
    {
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
