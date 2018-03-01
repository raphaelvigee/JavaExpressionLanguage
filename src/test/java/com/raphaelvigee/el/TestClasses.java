package com.raphaelvigee.el;

import java.util.ArrayList;
import java.util.HashMap;

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

    public HashMap<String, ArrayList<String>> returnHashMapOfArrayListOfString()
    {
        HashMap<String, ArrayList<String>> hm = new HashMap<>();
        ArrayList<String> a = new ArrayList<>();
        a.add("baz");
        hm.put("statuses", a);

        return hm;
    }
}
