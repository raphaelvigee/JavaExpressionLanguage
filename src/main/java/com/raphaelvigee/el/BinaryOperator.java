package com.raphaelvigee.el;

public class BinaryOperator extends Operator
{
    Associativity associativity;

    public BinaryOperator(int precedence, Associativity associativity)
    {
        super(precedence);
        this.associativity = associativity;
    }
}
