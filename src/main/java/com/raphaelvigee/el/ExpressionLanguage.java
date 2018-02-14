package com.raphaelvigee.el;

import com.raphaelvigee.el.Node.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionLanguage
{
    private Lexer lexer;

    private Parser parser;

    public ExpressionLanguage()
    {
        lexer = new Lexer();
        parser = new Parser(new LinkedHashMap<>());
    }

    public <R> R evaluate(String expression)
    {
        return evaluate(expression, new HashMap<>());
    }

    public <R> R evaluate(String expression, Map<String, Object> env)
    {
        TokenStream stream = lexer.tokenize(expression);

        Node parsed = parser.parse(stream, env.keySet());

        return (R) parsed.evaluate(env);
    }
}
