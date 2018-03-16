package com.raphaelvigee.el.Node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CollectionNode extends Node<Collection<Object>>
{
    public Collection<Node> entries = new LinkedList<>();

    private Class<? extends Collection> type;

    public CollectionNode(Class<? extends Collection> type)
    {
        super();

        this.type = type;
    }

    public void addElement(Node value)
    {
        entries.add(value);
    }

    @Override
    public Collection<Object> evaluate(Map<String, Object> env)
    {
        return entries
                .stream()
                .map(e -> e.evaluate(env))
                .collect(Collectors.toCollection(() -> {
                    try {
                        return (Collection<Object>) type.newInstance();
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

        CollectionNode collectionNode = (CollectionNode) o;
        return Objects.equals(entries, collectionNode.entries);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), entries);
    }
}
