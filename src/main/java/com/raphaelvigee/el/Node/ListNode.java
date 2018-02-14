package com.raphaelvigee.el.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ListNode extends Node
{
    public List<Node> entries = new LinkedList<>();

    public void addElement(Node value)
    {
        entries.add(value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        ListNode listNode = (ListNode) o;
        return Objects.equals(entries, listNode.entries);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), entries);
    }
}
