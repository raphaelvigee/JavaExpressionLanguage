package com.raphaelvigee.el.Node;

import com.raphaelvigee.el.NumberUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BinaryNode extends Node<Object>
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
        put("..", (Integer l, Integer r) -> IntStream.range(l, r).boxed().collect(Collectors.toList()));
        put("in", (Object l, Collection<Object> r) -> r.contains(l));
        put("not in", (Object l, Collection<Object> r) -> !r.contains(l));
        put("===", (Object l, Object r) -> l == r);
        put("!==", (Object l, Object r) -> l != r);
        put(">=", (Number l, Number r) -> l.doubleValue() >= r.doubleValue());
        put(">", (Number l, Number r) -> l.doubleValue() > r.doubleValue());
        put("<=", (Number l, Number r) -> l.doubleValue() <= r.doubleValue());
        put("<", (Number l, Number r) -> l.doubleValue() < r.doubleValue());
        put("matches", (String l, String r) -> Pattern.compile(l).matcher(r).find());
        put("==", Objects::equals);
        put("!=", (Object l, Object r) -> !Objects.equals(l, r));
        put("+", (BiFunction<Number, Number, Number>) NumberUtils::add);
        put("-", (BiFunction<Number, Number, Number>) NumberUtils::subtract);
        put("*", (BiFunction<Number, Number, Number>) NumberUtils::multiply);
        put("%", (BiFunction<Number, Number, Number>) NumberUtils::modulo);
        put("/", (BiFunction<Number, Number, Number>) NumberUtils::divide);
        put("&", (BiFunction<Number, Number, Number>) NumberUtils::and);
        put("|", (BiFunction<Number, Number, Number>) NumberUtils::or);
        put("^", (BiFunction<Number, Number, Number>) NumberUtils::xor);
    }};

    public BinaryNode(String operator, Node left, Node right)
    {
        super();
        nodes.put("left", left);
        nodes.put("right", right);

        attributes.put("operator", operator);
    }

    @Override
    public Object evaluate(Map<String, Object> env)
    {
        String operator = (String) attributes.get("operator");
        if (aliases.containsKey(operator)) {
            operator = aliases.get(operator);
        }

        Node left = nodes.get("left");
        Node right = nodes.get("right");

        Object el = left.evaluate(env);
        Object er = right.evaluate(env);

        return ((BiFunction<Object, Object, Object>) functions.get(operator)).apply(el, er);
    }
}
