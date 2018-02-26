package com.raphaelvigee.el;

import com.raphaelvigee.el.Exception.SyntaxError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;

public class EvaluateErrorTest
{
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private ExpressionLanguage expressionLanguage = new ExpressionLanguage();

    @Test
    public void testTokenizeThrowsErrorWithMessage()
    {
        expectedEx.expect(SyntaxError.class);
        expectedEx.expectMessage("Unexpected character \"'\" around position 8 for expression `foo(test').bar()`.");

        expressionLanguage.evaluate("foo(test').bar()");
    }

    @Test
    public void testTokenizeThrowsErrorOnUnclosedBrace()
    {
        expectedEx.expect(SyntaxError.class);
        expectedEx.expectMessage("Unclosed \"(\" around position 3 for expression `foo(test.bar()`.");

        expressionLanguage.evaluate("foo(test.bar()");
    }

    @Test
    public void testParseWithInvalidName()
    {
        expectedEx.expect(SyntaxError.class);
        expectedEx.expectMessage("Variable \"foo\" is not valid around position 1 for expression `foo`.");

        expressionLanguage.evaluate("foo");
    }

    @Test
    public void testNameProposal()
    {
        expectedEx.expect(SyntaxError.class);
        expectedEx.expectMessage("Variable \"bar\" is not valid around position 7 for expression `foo > bar`. Did you mean \"baz\"?");

        Map<String, Object> env = new HashMap<>();
        env.put("foo", 1);
        env.put("baz", 1);

        expressionLanguage.evaluate("foo > bar", env);
    }
}
