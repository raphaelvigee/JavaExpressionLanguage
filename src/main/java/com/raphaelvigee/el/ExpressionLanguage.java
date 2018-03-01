package com.raphaelvigee.el;

import com.raphaelvigee.el.Node.Node;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionLanguage
{
    private Lexer lexer;

    private Map<String, Function> functions;

    private Map<String, Object> env;

    public ExpressionLanguage()
    {
        this(new HashMap<>(), new HashMap<>());
    }

    public ExpressionLanguage(Map<String, Function> functions, Map<String, Object> env)
    {
        this.lexer = new Lexer();
        this.functions = functions;
        this.env = env;
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

        Map<String, Object> allEnv = new LinkedHashMap<>();
        allEnv.putAll(this.env);
        allEnv.putAll(env);

        Parser parser = new Parser(allFunctions);

        Node parsed = parser.parse(stream, allEnv.keySet());

        return (R) parsed.evaluate(allEnv);
    }

    public Map<String, Function> getFunctions()
    {
        return functions;
    }

    public void addFunction(String name, Function function)
    {
        functions.put(name, function);
    }

    public Map<String, Object> getEnv()
    {
        return env;
    }

    public void add(String name, Object o)
    {
        env.put(name, o);
    }
}
