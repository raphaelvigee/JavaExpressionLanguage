package com.raphaelvigee.el.Node;

import java.util.*;

public class Node
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

    public void compile(Compiler compiler)
    {
        for (Node node : nodes.values()) {
            node.compile(compiler);
        }
    }

    public List<Object> evaluate(List<Object> functions, List<Object> values)
    {
        List<Object> results = new LinkedList<>();
        for (Node node : nodes.values()) {
            results.add(node.evaluate(functions, values));
        }

        return results;
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
}
