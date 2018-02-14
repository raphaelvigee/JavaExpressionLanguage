package com.raphaelvigee.el;

public abstract class Operator
{
    public int precedence;

    public Operator(int precedence)
    {
        this.precedence = precedence;
    }
}
