package com.raphaelvigee.el;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
                        "\"test\" == \"test\"",
                        true
                },
                {
                        null,
                        "\"test\" != \"test\"",
                        false
                },
                {
                        null,
                        "\"test\" === \"test\"",
                        false
                },
                {
                        null,
                        "\"test\" !== \"test\"",
                        true
                },
                {
                        null,
                        "\"foo\" matches \"^foo$\"",
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
                        "\"hello\" in [\"foo\", \"bar\", \"hello\"]",
                        true
                },
                {
                        null,
                        "\"hello\" not in [\"foo\", \"bar\"]",
                        true
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
                        "TRUE and false",
                        false
                },
                {
                        null,
                        "3~ \" hello\"",
                        "3 hello"
                },
                {
                        new HashMap<String, Integer>()
                        {{
                            put("a", 3);
                            put("b", 2);
                        }},
                        "a + b",
                        5
                }
        });
    }

    @Test
    public void test()
    {
        ExpressionLanguage el = new ExpressionLanguage();

        Object result = el.evaluate(expression, env);

        assertEquals(expected, result);
    }
}
