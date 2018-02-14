package com.raphaelvigee.el;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class EvaluateTest
{
    private final String expression;

    private final Object expected;

    public EvaluateTest(String expression, Object expected)
    {
        this.expression = expression;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][]{
                {
                        "3+2",
                        5
                },
                {
                        "3-2",
                        1
                },
                {
                        "3*2",
                        6
                },
                {
                        "3.0/2",
                        1.5
                },
                {
                        "10%3",
                        1
                },
                {
                        "\"test\" == \"test\"",
                        true
                },
                {
                        "\"test\" != \"test\"",
                        false
                },
                {
                        "\"test\" === \"test\"",
                        false
                },
                {
                        "\"test\" !== \"test\"",
                        true
                },
                {
                        "\"foo\" matches \"^foo$\"",
                        true
                },
                {
                        "3<4",
                        true
                },
                {
                        "3<=4",
                        true
                },
                {
                        "3>4",
                        false
                },
                {
                        "3>=4",
                        false
                },
                {
                        "\"hello\" in [\"foo\", \"bar\", \"hello\"]",
                        true
                },
                {
                        "\"hello\" not in [\"foo\", \"bar\"]",
                        true
                },
                {
                        "3..5",
                        IntStream.range(3, 5).boxed().collect(Collectors.toList())
                },
                {
                        "3**4",
                        81.0
                },
                {
                        "true or FALSE",
                        true
                },
                {
                        "TRUE and false",
                        false
                },
                {
                        "3~ \" hello\"",
                        "3 hello"
                }
        });
    }

    @Test
    public void test()
    {
        ExpressionLanguage el = new ExpressionLanguage();

        Object result = el.evaluate(expression);

        assertEquals(expected, result);
    }
}
