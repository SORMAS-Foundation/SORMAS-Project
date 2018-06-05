package de.symeda.sormas.app.core;

import java.lang.reflect.Constructor;

/**
 * Created by Orson on 28/11/2017.
 */

public class ClassUtil {

    public static boolean hasParameterlessPublicConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getConstructors()) {
            // In Java 7-, use getParameterTypes and check the length of the array returned
            if (constructor.getParameterTypes().length == 0) {
                return true;
            }
        }
        return false;
    }
}
