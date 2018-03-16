package com.raphaelvigee.el.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListNode extends Node<List<Object>>
{
    public List<Node> entries = new LinkedList<>();

    private Class<? extends List> type;

    public ListNode(Class<? extends List> type)
    {
        super();

        this.type = type;
    }

    public void addElement(Node value)
    {
        entries.add(value);
    }

    @Override
    public List<Object> evaluate(Map<String, Object> env)
    {
        return entries
                .stream()
                .map(e -> e.evaluate(env))
                .collect(Collectors.toCollection(() -> {
                    try {
                        return (List<Object>) type.newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }));
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
