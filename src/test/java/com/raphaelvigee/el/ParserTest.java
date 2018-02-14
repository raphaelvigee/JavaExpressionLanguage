package com.raphaelvigee.el;

import com.raphaelvigee.el.Node.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParserTest
{
    private final String expression;

    private final Node expected;

    private final Set<String> names;

    private Lexer lexer;

    private Parser parser;

    public ParserTest(String expression, String[] names, Node expected)
    {
        this.expression = expression;
        this.names = new HashSet<>(Arrays.asList(names));
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
                        new String[]{"a"},
                        new NameNode("a")
                },
                {
                        "'a'",
                        new String[0],
                        new ConstantNode("a")
                },
                {
                        "3",
                        new String[0],
                        new ConstantNode(3)
                },
                {
                        "false",
                        new String[0],
                        new ConstantNode(false)
                },
                {
                        "true",
                        new String[0],
                        new ConstantNode(true)
                },
                {
                        "null",
                        new String[0],
                        new ConstantNode(null)
                },
                {
                        "-3",
                        new String[0],
                        new UnaryNode("-", new ConstantNode(3))
                },
                {
                        "3 - 2",
                        new String[0],
                        new BinaryNode("-", new ConstantNode(3), new ConstantNode(2))
                },
                {
                        "(3 - 3) * 2",
                        new String[0],
                        new BinaryNode(
                                "*",
                                new BinaryNode("-", new ConstantNode(3), new ConstantNode(3)),
                                new ConstantNode(2)
                        )
                },
                {
                        "foo.bar()",
                        new String[]{"foo"},
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode("bar", true),
                                new ArgumentsNode(),
                                GetAttrNode.CallType.METHOD
                        ),
                },
                {
                        "foo.not()",
                        new String[]{"foo"},
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode("not", true),
                                new ArgumentsNode(),
                                GetAttrNode.CallType.METHOD
                        ),
                },
                {
                        "foo.bar(\"arg1\", 2, true)",
                        new String[]{"foo"},
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode("bar", true),
                                arguments,
                                GetAttrNode.CallType.METHOD
                        ),
                },
                {
                        "foo[3]",
                        new String[]{"foo"},
                        new GetAttrNode(
                                new NameNode("foo"),
                                new ConstantNode(3),
                                new ArgumentsNode(),
                                GetAttrNode.CallType.ARRAY
                        ),
                },
                {
                        "true ? true : false",
                        new String[0],
                        new ConditionalNode(new ConstantNode(true), new ConstantNode(true), new ConstantNode(false)),
                },
                {
                        "\"foo\" matches \"/foo/\"",
                        new String[0],
                        new BinaryNode("matches", new ConstantNode("foo"), new ConstantNode("/foo/")),
                },
                {
                        "foo.bar().foo().baz[3]",
                        new String[]{"foo"},
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
        Node parsed = parser.parse(stream, this.names);

        assertEquals(expected, parsed);
    }

    private static Node createGetAttrNode(Node node, Object item, GetAttrNode.CallType type)
    {
        return new GetAttrNode(node, new ConstantNode(item, GetAttrNode.CallType.ARRAY != type), new ArgumentsNode(), type);
    }
}
