package com.raphaelvigee.el;

public class ClassFinder
{
    public static Class<?> findClassByName(String classname, String[] searchPackages)
    {
        for (String searchPackage : searchPackages) {
            try {
                return Class.forName(searchPackage + "." + classname);
            } catch (ClassNotFoundException e) {
                // Not in this package, try another
            }
        }

        return null;
    }
}
