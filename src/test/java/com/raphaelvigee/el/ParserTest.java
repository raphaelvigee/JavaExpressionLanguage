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
                        "a",
                        new NameNode("a")
                },
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
                {
                        "foo.bar()",
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode("bar", true),
                                new ArgumentsNode(),
                                GetAttrNode.CallType.METHOD
                        ),
                },
                {
                        "foo.not()",
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode("not", true),
                                new ArgumentsNode(),
                                GetAttrNode.CallType.METHOD
                        ),
                },
                {
                        "foo.bar(\"arg1\", 2, true)",
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode("bar", true),
                                arguments,
                                GetAttrNode.CallType.METHOD
                        ),
                },
                {
                        "foo[3]",
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode(3),
                                new ArgumentsNode(),
                                GetAttrNode.CallType.ARRAY
                        ),
                },
                {
                        "true ? true : false",
                        new ConditionalNode(new ConstantNode(true), new ConstantNode(true), new ConstantNode(false)),
                },
                {
                        "\"foo\" matches \"/foo/\"",
                        new BinaryNode("matches", new ConstantNode("foo"), new ConstantNode("/foo/")),
                },
                {
                        "foo.bar().foo().baz[3]",
                        createGetAttrNode(
                                createGetAttrNode(
                                        createGetAttrNode(
                                                createGetAttrNode(
                                                        new NameNode("foo"),
                                                        "bar",
                                                        GetAttrNode.CallType.METHOD
                                                ),
                                                "foo",
                                                GetAttrNode.CallType.METHOD
                                        ),
                                        "baz",
                                        GetAttrNode.CallType.PROPERTY
                                ),
                                3,
                                GetAttrNode.CallType.ARRAY
                        ),
                }
        });
    }

    @Test
    public void test()
    {
        TokenStream stream = lexer.tokenize(this.expression);
        Node parsed = parser.parse(stream);

        assertEquals(expected, parsed);
    }

    private static Node createGetAttrNode(Node node, Object item, GetAttrNode.CallType type)
    {
        return new GetAttrNode(node, new ConstantNode(item, GetAttrNode.CallType.ARRAY != type), new ArgumentsNode(), type);
    }
}
