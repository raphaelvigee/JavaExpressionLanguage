package com.raphaelvigee.el.Node;

import com.raphaelvigee.el.NumberUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BinaryNode extends Node
{
    private static Map<String, String> aliases = new HashMap<String, String>()
    {{
        put("&&", "and");
        put("||", "or");
    }};

    private static Map<String, BiFunction<?, ?, ?>> functions = new HashMap<String, BiFunction<?, ?, ?>>()
    {{
        put("~", (Object l, Object r) -> Objects.toString(l) + Objects.toString(r));
        put("and", (Boolean l, Boolean r) -> l && r);
        put("or", (Boolean l, Boolean r) -> l || r);
        put("**", (Number l, Number r) -> Math.pow(l.doubleValue(), r.doubleValue()));
        put("..", (BiFunction<Integer, Integer, List<Integer>>) (l, r) -> IntStream.range(l, r).boxed().collect(Collectors.toList()));
        put("in", (Object l, Collection<Object> r) -> r.contains(l));
        put("not in", (Object l, Collection<Object> r) -> !r.contains(l));
        put("===", (Object l, Object r) -> l == r);
        put("!==", (Object l, Object r) -> l != r);
        put(">=", (Number l, Number r) -> l.doubleValue() >= r.doubleValue());
        put(">", (Number l, Number r) -> l.doubleValue() > r.doubleValue());
        put("<=", (Number l, Number r) -> l.doubleValue() <= r.doubleValue());
        put("<", (Number l, Number r) -> l.doubleValue() < r.doubleValue());
        put("matches", (String l, String r) -> {
            Pattern pattern = Pattern.compile(l);
            Matcher matcher = pattern.matcher(r);

            return matcher.find();
        });
        put("==", Objects::equals);
        put("!=", (Object l, Object r) -> !Objects.equals(l, r));
        put("+", (BiFunction<Number, Number, Object>) NumberUtils::add);
        put("-", (BiFunction<Number, Number, Object>) NumberUtils::subtract);
        put("*", (BiFunction<Number, Number, Object>) NumberUtils::multiply);
        put("%", (BiFunction<Number, Number, Object>) NumberUtils::modulo);
        put("/", (BiFunction<Number, Number, Object>) NumberUtils::divide);
        put("&", (Integer l, Integer r) -> l & r);
        put("|", (Integer l, Integer r) -> l | r);
        put("^", (Integer l, Integer r) -> l ^ r);
    }};

    public BinaryNode(String operator, Node left, Node right)
    {
        super();
        nodes.put("left", left);
        nodes.put("right", right);

        attributes.put("operator", operator);
    }

    @Override
    public Object evaluate()
    {
        String operator = (String) attributes.get("operator");
        if (aliases.containsKey(operator)) {
            operator = aliases.get(operator);
        }

        Node left = nodes.get("left");
        Node right = nodes.get("right");

        return functions.get(operator).apply(left.evaluate(), right.evaluate());
    }
}
