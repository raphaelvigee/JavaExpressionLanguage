package com.raphaelvigee.el.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BinaryNode extends Node
{
    private static Map<String, String> operators = new HashMap<String, String>()
    {{
        put("~", ".");
        put("and", "&&");
        put("or", "||");
    }};

    private static Map<String, BiFunction<?, ?, ?>> functions = new HashMap<String, BiFunction<?, ?, ?>>()
    {{
        put("**", (BiFunction<Double, Double, Object>) Math::pow);
        put("..", (BiFunction<Integer, Integer, List<Integer>>) (l, r) -> IntStream.range(l, r).boxed().collect(Collectors.toList()));
        put("in", (Object l, List<Object> r) -> r.contains(l));
        put("not in", (Object l, List<Object> r) -> !r.contains(l));
    }};


    public BinaryNode(String operator, Node left, Node right)
    {
        super();
        nodes.put("left", left);
        nodes.put("right", right);

        attributes.put("operator", operator);
    }
}
