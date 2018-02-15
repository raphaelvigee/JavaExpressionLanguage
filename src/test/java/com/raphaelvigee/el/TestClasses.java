package com.raphaelvigee.el;

public class TestClasses
{
    static class Bar
    {
        public Foo foo;

        public Foo foo()
        {
            return foo;
        }
    }

    static class Foo
    {
        public Baz[] baz;
    }

    static class Baz
    {
        public int value;
    }

    public static Bar create()
    {
        Baz baz = new Baz();
        baz.value = 5;
        Foo foo = new Foo();
        foo.baz = new Baz[]{baz};
        Bar bar = new Bar();
        bar.foo = foo;

        return bar;
    }
}
