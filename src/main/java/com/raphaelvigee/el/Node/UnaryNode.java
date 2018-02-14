package com.raphaelvigee.el.Node;

import com.raphaelvigee.el.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class UnaryNode extends Node<Object>
{
    private static Map<String, String> aliases = new HashMap<String, String>()
    {{
        put("!", "not");
    }};

    private static Map<String, Function<?, ?>> functions = new HashMap<String, Function<?, ?>>()
    {{
        put("not", (Boolean v) -> !v);
        put("+", (Number v) -> v);
        put("-", (Number v) -> NumberUtils.multiply(v, -1));
    }};

    public UnaryNode(String operator, Node node)
    {
        nodes.put("node", node);
        attributes.put("operator", operator);
    }

    @Override
    public Object evaluate(Map<String, Object> env)
    {
        String operator = (String) attributes.get("operator");
        if (aliases.containsKey(operator)) {
            operator = aliases.get(operator);
        }

        Node node = nodes.get("node");

        return ((Function<Object, Object>) functions.get(operator)).apply(node.evaluate(env));
    }
}
