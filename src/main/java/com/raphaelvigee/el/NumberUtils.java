package com.raphaelvigee.el;

public class NumberUtils
{
    public static Number add(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() + b.doubleValue();
        } else if (a instanceof Float || b instanceof Float) {
            return a.floatValue() + b.floatValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() + b.longValue();
        } else {
            return a.intValue() + b.intValue();
        }
    }

    public static Number subtract(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() - b.doubleValue();
        } else if (a instanceof Float || b instanceof Float) {
            return a.floatValue() - b.floatValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() - b.longValue();
        } else {
            return a.intValue() - b.intValue();
        }
    }

    public static Number multiply(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() * b.doubleValue();
        } else if (a instanceof Float || b instanceof Float) {
            return a.floatValue() * b.floatValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() * b.longValue();
        } else {
            return a.intValue() * b.intValue();
        }
    }

    public static Number modulo(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() % b.doubleValue();
        } else if (a instanceof Float || b instanceof Float) {
            return a.floatValue() % b.floatValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() % b.longValue();
        } else {
            return a.intValue() % b.intValue();
        }
    }

    public static Number divide(Number a, Number b)
    {
        if (a instanceof Double || b instanceof Double) {
            return a.doubleValue() / b.doubleValue();
        } else if (a instanceof Float || b instanceof Float) {
            return a.floatValue() / b.floatValue();
        } else if (a instanceof Long || b instanceof Long) {
            return a.longValue() / b.longValue();
        } else {
            return a.intValue() / b.intValue();
        }
    }

    public static Number or(Number a, Number b)
    {
        if (a instanceof Long || b instanceof Long) {
            return a.longValue() | b.longValue();
        } else if (a instanceof Integer || b instanceof Integer) {
            return a.intValue() | b.intValue();
        } else if (a instanceof Short || b instanceof Short) {
            return a.shortValue() | b.shortValue();
        } else {
            return a.byteValue() | b.byteValue();
        }
    }

    public static Number and(Number a, Number b)
    {
        if (a instanceof Long || b instanceof Long) {
            return a.longValue() & b.longValue();
        } else if (a instanceof Integer || b instanceof Integer) {
            return a.intValue() & b.intValue();
        } else if (a instanceof Short || b instanceof Short) {
            return a.shortValue() & b.shortValue();
        } else {
            return a.byteValue() & b.byteValue();
        }
    }

    public static Number xor(Number a, Number b)
    {
        if (a instanceof Long || b instanceof Long) {
            return a.longValue() ^ b.longValue();
        } else if (a instanceof Integer || b instanceof Integer) {
            return a.intValue() ^ b.intValue();
        } else if (a instanceof Short || b instanceof Short) {
            return a.shortValue() ^ b.shortValue();
        } else {
            return a.byteValue() ^ b.byteValue();
        }
    }
}
