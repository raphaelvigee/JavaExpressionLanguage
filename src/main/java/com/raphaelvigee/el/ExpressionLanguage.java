package com.raphaelvigee.el;

import com.raphaelvigee.el.Node.Node;

import java.util.LinkedHashMap;

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
        TokenStream stream = lexer.tokenize(expression);

        Node parsed = parser.parse(stream);

        return parsed.evaluate();
    }
}
