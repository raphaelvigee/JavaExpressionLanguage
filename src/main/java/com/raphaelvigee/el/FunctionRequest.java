package com.raphaelvigee.el;

public class FunctionRequest
{
    private Object[] args;

    public FunctionRequest(Object[] args)
    {
        this.args = args;
    }

    public Object[] getArgs()
    {
        return args;
    }

    public <R> R get(int n)
    {
        return (R) getArgs()[n];
    }

    public <R> R get(int n, R d)
    {
        try {
            return get(n);
        } catch (IndexOutOfBoundsException ignored) {
            return d;
        }
    }

    public void minArguments(int n)
    {
        if (args.length < n) {
            throw new RuntimeException(String.format("Expect at least %d arguments, got %d", n, args.length));
        }
    }

    public void maxArguments(int n)
    {
        if (args.length > n) {
            throw new RuntimeException(String.format("Expect at most %d arguments, got %d", n, args.length));
        }
    }

    public void arguments(int n)
    {
        minArguments(n);
        maxArguments(n);
    }

    public void arguments(int min, int max)
    {
        minArguments(min);
        maxArguments(max);
    }
}
