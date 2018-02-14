package com.raphaelvigee.el.Node;

import java.util.HashMap;
import java.util.Map;

public class UnaryNode extends Node
{
    private static Map<String, String> operators = new HashMap<String, String>()
    {{
        put("!", "!");
        put("not", "!");
        put("+", "+");
        put("-", "-");
    }};

    public UnaryNode(String operator, Node node)
    {
        nodes.put("node", node);
        attributes.put("operator", operator);
    }
}
