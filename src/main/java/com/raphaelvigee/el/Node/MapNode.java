package com.raphaelvigee.el.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapNode extends Node<Map<Object, Object>>
{
    Map<Node, Node> entries = new LinkedHashMap<>();

    private Class<? extends Map> type;

    public MapNode(Class<? extends Map> type)
    {
        super();

        this.type = type;
    }

    public void addElement(Node value, Node key)
    {
        entries.put(key, value);
    }

    @Override
    public Map<Object, Object> evaluate(Map<String, Object> env)
    {
        return entries
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().evaluate(env),
                        e -> e.getValue().evaluate(env),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        () -> {
                            try {
                                return (Map<Object, Object>) type.newInstance();
                            } catch (InstantiationException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ));
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

        MapNode mapNode = (MapNode) o;
        return Objects.equals(entries, mapNode.entries);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(super.hashCode(), entries);
    }
}
