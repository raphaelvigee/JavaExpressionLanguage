package com.raphaelvigee.el.Node;

import java.util.List;

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
}
