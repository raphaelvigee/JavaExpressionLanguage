package com.raphaelvigee.el;

import com.raphaelvigee.el.Node.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParserTest
{
    private final String expression;

    private final Node expected;

    private Lexer lexer;

    private Parser parser;

    public ParserTest(String expression, Node expected)
    {
        this.expression = expression;
        this.expected = expected;
    }

    @Before
    public void setUp()
    {
        lexer = new Lexer();
        parser = new Parser(new LinkedHashMap<>());
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        ArgumentsNode arguments = new ArgumentsNode();
        arguments.addElement(new ConstantNode("arg1"));
        arguments.addElement(new ConstantNode(2));
        arguments.addElement(new ConstantNode(true));

        return Arrays.asList(new Object[][]{
                {
                        "'a'",
                        new ConstantNode("a")
                },
                {
                        "3",
                        new ConstantNode(3)
                },
                {
                        "false",
                        new ConstantNode(false)
                },
                {
                        "true",
                        new ConstantNode(true)
                },
                {
                        "null",
                        new ConstantNode(null)
                },
                {
                        "-3",
                        new UnaryNode("-", new ConstantNode(3))
                },
                {
                        "3 - 2",
                        new BinaryNode("-", new ConstantNode(3), new ConstantNode(2))
                },
                {
                        "(3 - 3) * 2",
                        new BinaryNode(
                                "*",
                                new BinaryNode("-", new ConstantNode(3), new ConstantNode(3)),
                                new ConstantNode(2)
                        )
                },
        });
    }

    @Test
    public void test()
    {
        Node parsed = parser.parse(lexer.tokenize(this.expression));

        assertEquals(expected, parsed);
    }
}
