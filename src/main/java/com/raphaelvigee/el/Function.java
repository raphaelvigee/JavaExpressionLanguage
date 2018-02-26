package com.raphaelvigee.el;

@FunctionalInterface
public interface Function<R>
{
    R run(FunctionRequest request);
}
