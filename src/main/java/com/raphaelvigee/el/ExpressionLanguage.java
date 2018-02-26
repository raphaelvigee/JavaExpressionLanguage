package com.raphaelvigee.el;

import com.raphaelvigee.el.Node.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionLanguage
{
    private Lexer lexer;

    private Map<String, Function> functions;

    public ExpressionLanguage()
    {
        this(new HashMap<>());
    }

    public ExpressionLanguage(Map<String, Function> functions)
    {
        this.lexer = new Lexer();
        this.functions = functions;
    }

    public <R> R evaluate(String expression)
    {
        return evaluate(expression, new HashMap<>(), new HashMap<>());
    }

    public <R> R evaluate(String expression, Map<String, Object> env)
    {
        return evaluate(expression, env, new HashMap<>());
    }

    public <R> R evaluate(String expression, Map<String, Object> env, Map<String, Function> functions)
    {
        TokenStream stream = lexer.tokenize(expression);

        Map<String, Function> allFunctions = new LinkedHashMap<>();
        allFunctions.putAll(functions);
        allFunctions.putAll(this.functions);

        Parser parser = new Parser(allFunctions);

        Node parsed = parser.parse(stream, env.keySet());

        return (R) parsed.evaluate(env);
    }
}
