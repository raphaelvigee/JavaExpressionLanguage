package com.raphaelvigee.el;

import com.raphaelvigee.el.Exception.SyntaxError;
import com.raphaelvigee.el.Node.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser
{
    private Map<String, Function> functions;

    private Map<String, UnaryOperator> unaryOperators = new HashMap<>();

    private Map<String, BinaryOperator> binaryOperators = new HashMap<>();

    private TokenStream stream;

    private Set<String> names;

    public Parser(Map<String, Function> functions)
    {
        this.functions = functions;

        unaryOperators.put("not", new UnaryOperator(50));
        unaryOperators.put("!", new UnaryOperator(50));
        unaryOperators.put("-", new UnaryOperator(500));
        unaryOperators.put("+", new UnaryOperator(500));

        binaryOperators.put("or", new BinaryOperator(10, Associativity.LEFT));
        binaryOperators.put("||", new BinaryOperator(10, Associativity.LEFT));
        binaryOperators.put("and", new BinaryOperator(15, Associativity.LEFT));
        binaryOperators.put("&&", new BinaryOperator(15, Associativity.LEFT));
        binaryOperators.put("|", new BinaryOperator(16, Associativity.LEFT));
        binaryOperators.put("^", new BinaryOperator(17, Associativity.LEFT));
        binaryOperators.put("&", new BinaryOperator(18, Associativity.LEFT));
        binaryOperators.put("==", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("===", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("!=", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("!==", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("<", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put(">", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put(">=", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("<=", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("not in", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("in", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("matches", new BinaryOperator(20, Associativity.LEFT));
        binaryOperators.put("..", new BinaryOperator(25, Associativity.LEFT));
        binaryOperators.put("+", new BinaryOperator(30, Associativity.LEFT));
        binaryOperators.put("-", new BinaryOperator(30, Associativity.LEFT));
        binaryOperators.put("~", new BinaryOperator(40, Associativity.LEFT));
        binaryOperators.put("*", new BinaryOperator(60, Associativity.LEFT));
        binaryOperators.put("/", new BinaryOperator(60, Associativity.LEFT));
        binaryOperators.put("%", new BinaryOperator(60, Associativity.LEFT));
        binaryOperators.put("**", new BinaryOperator(200, Associativity.RIGHT));
    }

    public Node parse(TokenStream stream, Set<String> names)
    {
        this.stream = stream;
        this.names = names;

        Token current = stream.getCurrent();

        Node node = parseExpression();

        if (!stream.isEOF()) {
            throw new SyntaxError(String.format("Unexpected token \"%s\" of value \"%s\"", current.type, current.value), current.cursor, stream.getExpression());
        }

        return node;
    }

    public Node parseExpression()
    {
        return parseExpression(0);
    }

    public Node parseExpression(int precedence)
    {
        Node expr = getPrimary();
        Token token = stream.getCurrent();

        String tokenValue = token.stringValue();

        while (token.test(TokenType.OPERATOR) &&
                binaryOperators.containsKey(tokenValue) &&
                binaryOperators.get(tokenValue).precedence >= precedence) {
            BinaryOperator op = binaryOperators.get(tokenValue);

            stream.next();

            Node expr1 = parseExpression(op.associativity == Associativity.LEFT ? op.precedence + 1 : op.precedence);

            expr = new BinaryNode(tokenValue, expr, expr1);
            token = stream.getCurrent();
        }

        if (precedence == 0) {
            return parseConditionalExpression(expr);
        }

        return expr;
    }

    public Node getPrimary()
    {
        Token token = stream.getCurrent();

        String tokenValue = token.stringValue();

        if (token.test(TokenType.OPERATOR) && unaryOperators.containsKey(tokenValue)) {
            UnaryOperator operator = unaryOperators.get(tokenValue);
            stream.next();
            Node expr = parseExpression(operator.precedence);

            return parsePostfixExpression(new UnaryNode(tokenValue, expr));
        }

        if (token.test(TokenType.PUNCTUATION, "(")) {
            stream.next();
            Node expr = parseExpression();
            stream.expect(TokenType.PUNCTUATION, ")", "An opened parenthesis is not properly closed");

            return parsePostfixExpression(expr);
        }

        return parsePrimaryExpression();
    }

    public Node parseConditionalExpression(Node expr)
    {
        while (stream.getCurrent().test(TokenType.PUNCTUATION, "?")) {
            stream.next();

            Node expr2;
            Node expr3;
            if (!stream.getCurrent().test(TokenType.PUNCTUATION, ":")) {
                expr2 = parseExpression();

                if (stream.getCurrent().test(TokenType.PUNCTUATION, ":")) {
                    stream.next();

                    expr3 = parseExpression();
                } else {
                    expr3 = new ConstantNode(null);
                }
            } else {
                stream.next();
                expr2 = expr;
                expr3 = parseExpression();
            }

            expr = new ConditionalNode(expr, expr2, expr3);
        }

        return expr;
    }

    public Node parsePrimaryExpression()
    {
        Token token = stream.getCurrent();

        Node node;
        switch (token.type) {
            case NAME:
                stream.next();

                String tokenValue = token.stringValue();

                switch (tokenValue) {
                    case "true":
                    case "TRUE":
                        return new ConstantNode(true);
                    case "false":
                    case "FALSE":
                        return new ConstantNode(false);
                    case "null":
                    case "NULL":
                        return new ConstantNode(null);
                }

                Token next = stream.getCurrent();

                if (Objects.equals(next.value, "#")) {
                    stream.next();

                    node = parseComposite(token);
                } else if (Objects.equals(next.value, "(")) {
                    if (!functions.containsKey(tokenValue)) {
                        throw new SyntaxError(String.format("The function \"%s\" does not exist", token.value), token.cursor, stream.getExpression(), String.valueOf(token.value), functions.keySet());
                    }

                    node = new FunctionNode(tokenValue, parseArguments(), functions);
                } else {
                    if (!names.contains(tokenValue)) {
                        throw new SyntaxError(String.format("Variable \"%s\" is not valid", token.value), token.cursor, stream.getExpression(), String.valueOf(token.value), names);
                    }

                    node = new NameNode(token.stringValue());
                }

                break;
            case DOUBLE:
            case INT:
            case STRING:
                stream.next();

                return new ConstantNode(token.value);
            default:
                if (token.test(TokenType.PUNCTUATION, "[")) {
                    node = parseCollectionExpression(ArrayList.class);
                } else if (token.test(TokenType.PUNCTUATION, "{")) {
                    node = parseMapExpression(HashMap.class);
                } else {
                    throw new SyntaxError(String.format("Unexpected token \"%s\" of value \"%s\"", token.type, token.value), token.cursor, this.stream.getExpression());
                }
        }

        return parsePostfixExpression(node);
    }

    public CollectionNode parseCollectionExpression(Class<? extends Collection> type)
    {
        stream.expect(TokenType.PUNCTUATION, "[", "An array element was expected");

        CollectionNode node = new CollectionNode(type);

        boolean first = true;

        while (!stream.getCurrent().test(TokenType.PUNCTUATION, "]")) {
            if (!first) {
                stream.expect(TokenType.PUNCTUATION, ",", "An array element must be followed by a comma");

                if (stream.getCurrent().test(TokenType.PUNCTUATION, "]")) {
                    break;
                }
            }
            first = false;

            node.addElement(parseExpression());
        }

        stream.expect(TokenType.PUNCTUATION, "]", "An opened array is not properly closed");

        return node;
    }

    public Node parseComposite(Token name)
    {
        Token identifier = stream.getCurrent();

        String[] searchPackages = {
                "java.lang",
                "java.util",
        };

        Class<?> type = ClassFinder.findClassByName((String) name.value, searchPackages);

        if (type == null) {
            throw new SyntaxError("Composite type not found, type \"%s\" got \"%s\"", identifier.cursor, stream.getExpression());
        }

        if (Objects.equals(identifier.value, "{") && Map.class.isAssignableFrom(type)) {
            return parseMapExpression((Class<? extends Map>) type);
        } else if (Objects.equals(identifier.value, "[") && Collection.class.isAssignableFrom(type)) {
            return parseCollectionExpression((Class<? extends Collection>) type);
        }

        throw new SyntaxError(String.format("Invalid composite definition, type: \"%s\", identifier: \"%s\"", type.getSimpleName(), identifier.value), name.cursor, stream.getExpression());
    }

    public MapNode parseMapExpression(Class<? extends Map> type)
    {
        MapNode node = new MapNode(type);

        stream.expect(TokenType.PUNCTUATION, "{", "A hash element was expected");

        boolean first = true;

        while (!stream.getCurrent().test(TokenType.PUNCTUATION, "}")) {
            if (!first) {
                stream.expect(TokenType.PUNCTUATION, ",", "An array element must be followed by a comma");

                if (stream.getCurrent().test(TokenType.PUNCTUATION, "}")) {
                    break;
                }
            }
            first = false;

            Node key;
            if (stream.getCurrent().test(TokenType.STRING) || stream.getCurrent().test(TokenType.NAME) || stream.getCurrent().test(TokenType.INT) || stream.getCurrent().test(TokenType.DOUBLE)) {
                key = new ConstantNode(stream.getCurrent().value);
                stream.next();
            } else if (stream.getCurrent().test(TokenType.PUNCTUATION, "(")) {
                key = parseExpression();
            } else {
                throw new SyntaxError(String.format("A hash key must be a quoted string, a number, a name, or an expression enclosed in parentheses (unexpected token \"%s\" of value \"%s\"", stream.getCurrent().type, stream.getCurrent().value), stream.getCurrent().cursor, stream.getExpression());
            }

            stream.expect(TokenType.PUNCTUATION, ":", "A hash key must be followed by a colon (:)");
            Node value = parseExpression();

            node.addElement(value, key);
        }

        stream.expect(TokenType.PUNCTUATION, "}", "An opened hash is not properly closed");

        return node;
    }

    public Node parsePostfixExpression(Node node)
    {
        Token token = stream.getCurrent();

        Pattern namePattern = Pattern.compile("^[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*$");

        while (token.test(TokenType.PUNCTUATION)) {
            if (Objects.equals(token.value, ".")) {
                stream.next();
                token = stream.getCurrent();
                stream.next();

                Matcher nameMatcher = namePattern.matcher(token.stringValue());

                // https://github.com/symfony/expression-language/blob/master/Parser.php#L317
                if (!token.test(TokenType.NAME) && (!token.test(TokenType.OPERATOR) || !nameMatcher.matches())) {
                    throw new SyntaxError("Expected name", token.cursor, this.stream.getExpression());
                }

                Node arg = new ConstantNode(token.value, true);

                ArgumentsNode arguments = new ArgumentsNode();

                GetAttrNode.CallType type;
                if (stream.getCurrent().test(TokenType.PUNCTUATION, "(")) {
                    type = GetAttrNode.CallType.METHOD;

                    for (Node n : parseArguments().entries) {
                        arguments.addElement(n);
                    }
                } else {
                    type = GetAttrNode.CallType.PROPERTY;
                }

                node = new GetAttrNode(node, arg, arguments, type);
            } else if (Objects.equals(token.value, "[")) {
                stream.next();
                Node arg = parseExpression();
                stream.expect(TokenType.PUNCTUATION, "]");

                node = new GetAttrNode(node, arg, new ArgumentsNode(), GetAttrNode.CallType.ARRAY);
            } else {
                break;
            }

            token = stream.getCurrent();
        }

        return node;
    }

    public ArgumentsNode parseArguments()
    {
        List<Node> args = new LinkedList<>();
        stream.expect(TokenType.PUNCTUATION, "(", "A list of arguments must begin with an opening parenthesis");

        while (!stream.getCurrent().test(TokenType.PUNCTUATION, ")")) {
            if (!args.isEmpty()) {
                stream.expect(TokenType.PUNCTUATION, ",", "Arguments must be separated by a comma");
            }

            args.add(parseExpression());
        }
        stream.expect(TokenType.PUNCTUATION, ")", "A list of arguments must be closed by a parenthesis");

        return new ArgumentsNode(args);
    }
}
