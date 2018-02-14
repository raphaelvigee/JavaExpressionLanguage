package com.raphaelvigee.el.Node;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Node<R>
{
    public Map<String, Node> nodes = new LinkedHashMap<>();

    Map<String, Object> attributes = new LinkedHashMap<>();

    public Node(Map<String, Node> nodes, Map<String, Object> attributes)
    {
        super();

        this.nodes = nodes;
        this.attributes = attributes;
    }

    public Node()
    {

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

        Node node = (Node) o;
        return Objects.equals(nodes, node.nodes) &&
                Objects.equals(attributes, node.attributes);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(nodes, attributes);
    }

    public R evaluate(Map<String, Object> env)
    {
        throw new RuntimeException("Unable to evaluate " + this.getClass());
    }
}
