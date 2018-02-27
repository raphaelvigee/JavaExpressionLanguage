package com.raphaelvigee.el;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class EvaluateTest
{
    private final Map<String, Object> env;

    private final String expression;

    private final Object expected;

    public EvaluateTest(Map<String, Object> env, String expression, Object expected)
    {
        this.env = env == null ? new HashMap<>() : env;
        this.expression = expression;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]{
                {
                        null,
                        "-3",
                        -3
                },
                {
                        null,
                        "3+2",
                        5
                },
                {
                        null,
                        "3-2",
                        1
                },
                {
                        null,
                        "3*2",
                        6
                },
                {
                        null,
                        "3.0/2",
                        1.5
                },
                {
                        null,
                        "10%3",
                        1
                },
                {
                        null,
                        "!true",
                        false
                },
                {
                        null,
                        "not false",
                        true
                },
                {
                        null,
                        "'test' == 'test'",
                        true
                },
                {
                        null,
                        "not ('test' != 'test')",
                        true
                },
                {
                        null,
                        "'test' === 'test'",
                        false
                },
                {
                        null,
                        "'test' !== 'test'",
                        true
                },
                {
                        null,
                        "'foo' matches '^foo$'",
                        true
                },
                {
                        null,
                        "3<4",
                        true
                },
                {
                        null,
                        "3<=4",
                        true
                },
                {
                        null,
                        "3>4",
                        false
                },
                {
                        null,
                        "3>=4",
                        false
                },
                {
                        null,
                        "'hello' in ['foo', 'bar', 'hello']",
                        true
                },
                {
                        null,
                        "'hello' not in ['foo', 'bar', 'hello']",
                        false
                },
                {
                        null,
                        "3..5",
                        IntStream.range(3, 5).boxed().collect(Collectors.toList())
                },
                {
                        null,
                        "3**4",
                        81.0
                },
                {
                        null,
                        "true or FALSE",
                        true
                },
                {
                        null,
                        "false || true",
                        true
                },
                {
                        null,
                        "TRUE and false",
                        false
                },
                {
                        null,
                        "true && false",
                        false
                },
                {
                        null,
                        "3~ \" hello\"",
                        "3 hello"
                },
                {
                        null,
                        "{a: 3, b: true}",
                        new HashMap<Object, Object>()
                        {{
                            put("a", 3);
                            put("b", true);
                        }}
                },
                {
                        null,
                        "[1, 2, 3, 4]",
                        Arrays.asList(1, 2, 3, 4)
                },
                {
                        new HashMap<String, Integer>()
                        {{
                            put("a", 3);
                            put("b", 2);
                        }},
                        "a + b",
                        5
                },
                {
                        new HashMap<String, Object>()
                        {{
                            put("bar", TestClasses.create());
                        }},
                        "bar.foo().baz[0].value",
                        5
                },
                {
                        null,
                        "hello('world')",
                        "world"
                },
                {
                        null,
                        "add(1, 2)",
                        3
                },
                {
                        null,
                        "concat(1, ' hello ', 2)",
                        "1 hello 2"
                },
                {
                        null,
                        "true ? 1 : 2",
                        1
                },
                {
                        null,
                        "false ? 1 : 2",
                        2
                },
                {
                        null,
                        "'foo' ~ \"bar\"",
                        "foobar"
                }
        });
    }

    @Test
    public void test()
    {
        ExpressionLanguage el = new ExpressionLanguage();

        Map<String, Function> functions = new LinkedHashMap<>();
        functions.put("hello", request -> {
            request.arguments(1);

            return request.get(0);
        });

        functions.put("add", request ->
        {
            request.arguments(2);

            Number left = request.get(0);
            Number right = request.get(1);

            return NumberUtils.add(left, right);
        });

        functions.put("concat", request ->
        {
            request.arguments(0, 3);

            StringBuilder sb = new StringBuilder();

            for (Object o : request.getArgs()) {
                sb.append(String.valueOf(o));
            }

            return sb.toString();
        });

        Object result = el.evaluate(expression, env, functions);

        assertEquals(expected, result);
    }
}
