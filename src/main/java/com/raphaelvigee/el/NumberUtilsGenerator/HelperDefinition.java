package com.raphaelvigee.el.NumberUtilsGenerator;

import java.util.LinkedList;
import java.util.List;

public class HelperDefinition
{
    String name;

    String operator;

    List<TypeDefinition> types = new LinkedList<>();

    public HelperDefinition(String name, String operator)
    {
        this.name = name;
        this.operator = operator;
    }

    public static HelperDefinition arithmetic(String name, String operator)
    {
        HelperDefinition d = new HelperDefinition(name, operator);

        d.types.add(new TypeDefinition(Double.class, "doubleValue"));
        d.types.add(new TypeDefinition(Float.class, "floatValue"));
        d.types.add(new TypeDefinition(Long.class, "longValue"));
        d.types.add(new TypeDefinition(Integer.class, "intValue"));
        d.types.add(new TypeDefinition(Short.class, "shortValue"));
        d.types.add(new TypeDefinition(Byte.class, "byteValue"));

        return d;
    }

    public static HelperDefinition bitwise(String name, String operator)
    {
        HelperDefinition d = new HelperDefinition(name, operator);

        d.types.add(new TypeDefinition(Long.class, "longValue"));
        d.types.add(new TypeDefinition(Integer.class, "intValue"));
        d.types.add(new TypeDefinition(Short.class, "shortValue"));
        d.types.add(new TypeDefinition(Byte.class, "byteValue"));

        return d;
    }
}
