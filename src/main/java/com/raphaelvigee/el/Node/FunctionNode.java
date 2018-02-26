package com.raphaelvigee.el.Node;

import com.raphaelvigee.el.Function;
import com.raphaelvigee.el.FunctionRequest;

import java.util.Map;

public class FunctionNode extends Node<Object>
{
    private final Map<String, Function> functions;

    public FunctionNode(String name, ArgumentsNode arguments, Map<String, Function> functions)
    {
        super();
        this.functions = functions;

        nodes.put("arguments", arguments);
        attributes.put("name", name);
    }

    @Override
    public Object evaluate(Map<String, Object> env)
    {
        ArgumentsNode argumentsNode = (ArgumentsNode) nodes.get("arguments");
        String name = (String) attributes.get("name");

        Object[] arguments = argumentsNode.evaluateArray(env);

        return functions.get(name).run(new FunctionRequest(arguments));
    }
}
