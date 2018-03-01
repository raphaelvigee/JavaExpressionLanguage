package com.raphaelvigee.el.Node;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

public class GetAttrNode extends Node<Object>
{
    public enum CallType
    {
        PROPERTY, METHOD, ARRAY
    }

    public GetAttrNode(Node node, Node attribute, ArgumentsNode arguments, CallType type)
    {
        super();

        nodes.put("node", node);
        nodes.put("attribute", attribute);
        nodes.put("arguments", arguments);

        attributes.put("type", type);
    }

    @Override
    public Object evaluate(Map<String, Object> env)
    {
        Node node = nodes.get("node");
        Node attributeNode = nodes.get("attribute");
        ArgumentsNode argumentsNode = (ArgumentsNode) nodes.get("arguments");
        CallType typeNode = (CallType) attributes.get("type");

        Object nodeValue = node.evaluate(env);
        Object attribute = attributeNode.evaluate(env);
        Object[] arguments = argumentsNode.evaluateArray(env);
        Class<?>[] argumentClasses = (Class[]) Stream.of(arguments).map(Object::getClass).toArray(Class[]::new);
        Class nodeClass = nodeValue.getClass();

        switch (typeNode) {
            case PROPERTY:
                try {
                    Field field = nodeClass.getField(String.valueOf(attribute));
                    field.setAccessible(true);

                    return field.get(nodeValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            case METHOD:
                try {
                    Method method = getMethodForArgs(nodeValue.getClass(), argumentClasses);

                    if(method == null) {
                        throw new  NoSuchMethodException();
                    }

                    method.setAccessible(true);

                    return method.invoke(nodeValue, arguments);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            case ARRAY:
                return ((Object[]) nodeValue)[(int) attribute];
        }

        throw new RuntimeException("Unhandled node type");
    }

    private static <T> Method getMethodForArgs(Class<T> klass, Class[] args)
    {
        //Get all the methods from given class
        Method[] methods = klass.getMethods();

        for (Method method : methods) {
            //Walk through all the methods, matching parameter amount and parameter types with given types (args)
            Class<?>[] types = method.getParameterTypes();
            if (types.length == args.length) {
                boolean argumentsMatch = true;
                for (int i = 0; i < args.length; i++) {
                    //Note that the types in args must be in same order as in the constructor if the checking is done this way
                    if (args[i] == null) {
                        continue;
                    }

                    if (!types[i].isAssignableFrom(args[i])) {
                        argumentsMatch = false;
                        break;
                    }
                }

                if (argumentsMatch) {
                    //We found a matching constructor, return it
                    return method;
                }
            }
        }

        //No matching constructor
        return null;
    }
}
