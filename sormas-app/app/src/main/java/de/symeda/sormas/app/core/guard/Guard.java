package de.symeda.sormas.app.core.guard;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Orson on 01/01/2018.
 */

public class Guard {

   /* static{
        //THAT = new GuardType();
    }

    //public static final GuardType THAT;*/

    public static <T> boolean isOneOfSupplied(T obj, List<T> possibles) {
        return isOneOfSupplied(obj, possibles, "The object does not have one of the supplied values.");
    }

    public static <T> boolean isOneOfSupplied(T obj, List<T> possibles, String message) {
        for (T possible : possibles)
            if (possible.equals(obj))
                return true;

        throw new IllegalArgumentException(message);
    }

    public static String format(@Nullable String template, @Nullable Object... args) {
        template = String.valueOf(template); // null -> "null"

        args = args == null ? new Object[]{"(Object[])null"} : args;

        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template, templateStart, placeholderStart);
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template, templateStart, template.length());

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append(']');
        }

        return builder.toString();
    }



    public static final class That {
/*        static {
            ARGUMENT = new ArgumentCondition();
            STATE = new StateCondition();
            NOT_NULL = new NotNullCondition();
            IS_NULL = new IsNullCondition();
            ELEMENT_INDEX = new ElementIndexCondition();
            POSITION_INDEX = new PositionIndexCondition();
        }

        public static final ArgumentCondition ARGUMENT;
        public static final StateCondition STATE;
        public static final NotNullCondition NOT_NULL;
        public static final IsNullCondition IS_NULL;
        public static final ElementIndexCondition ELEMENT_INDEX;
        public static final PositionIndexCondition POSITION_INDEX;*/


        public static class Argument {

            public static void isTrue(boolean condition) {
                if (!condition) {
                    throw new IllegalArgumentException("The argument condition supplied is false");
                }
            }

            public static void isTrue(boolean condition, String message) {
                if (!condition) {
                    throw new IllegalArgumentException(String.valueOf(message));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, errorMessageArgs));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, Object p1) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, char p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, int p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, long p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, char p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, int p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, long p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, char p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, int p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, long p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2, p3));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
                if (!condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2, p3, p4));
                }
            }




            public static void isFalse(boolean condition, String message) {
                if (condition) {
                    throw new IllegalArgumentException(String.valueOf(message));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, errorMessageArgs));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, Object p1) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, char p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, int p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, long p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, char p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, int p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, long p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, char p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, int p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, long p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2, p3));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
                if (condition) {
                    throw new IllegalArgumentException(Guard.format(errorMessageTemplate, p1, p2, p3, p4));
                }
            }
        }

        public static class State {

            public static void isTrue(boolean condition) {
                if (!condition) {
                    throw new IllegalStateException("The state condition supplied is false");
                }
            }

            public static void isTrue(boolean condition, String message) {
                if (!condition) {
                    throw new IllegalStateException(String.valueOf(message));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, errorMessageArgs));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, Object p1) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, char p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, int p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, long p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, char p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, int p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, long p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, char p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, int p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, long p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2, p3));
                }
            }

            public static void isTrue(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
                if (!condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2, p3, p4));
                }
            }







            public static void isFalse(boolean condition) {
                if (condition) {
                    throw new IllegalStateException("The state condition supplied is false");
                }
            }

            public static void isFalse(boolean condition, String message) {
                if (condition) {
                    throw new IllegalStateException(String.valueOf(message));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, errorMessageArgs));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, Object p1) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, char p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, int p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, long p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, char p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, int p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, long p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, char p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, int p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, long p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2, p3));
                }
            }

            public static void isFalse(boolean condition, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
                if (condition) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2, p3, p4));
                }
            }
        }

        public static class NotNull {


            public static <T> T isTrue(T reference) {
                if (reference == null) {
                    throw new NullPointerException("The argument provided cannot be null.");
                }
                return reference;
            }

            public static <T> T isTrue(T reference, @Nullable Object errorMessage) {
                if (reference == null) {
                    throw new NullPointerException(String.valueOf(errorMessage));
                }
                return reference;
            }

            public static <T> T isTrue(T reference, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
                if (reference == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, errorMessageArgs));
                }
                return reference;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, char p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, int p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, long p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, char p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, int p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, long p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, char p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, int p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, long p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2, p3));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
                if (obj == null) {
                    throw new NullPointerException(Guard.format(errorMessageTemplate, p1, p2, p3, p4));
                }
                return obj;
            }
        }

        public static class IsNull {


            public static <T> T isTrue(T reference) {
                if (reference != null) {
                    throw new IllegalStateException("The argument provided cannot be null.");
                }
                return reference;
            }

            public static <T> T isTrue(T reference, @Nullable Object errorMessage) {
                if (reference != null) {
                    throw new IllegalStateException(String.valueOf(errorMessage));
                }
                return reference;
            }

            public static <T> T isTrue(T reference, @Nullable String errorMessageTemplate, @Nullable Object... errorMessageArgs) {
                if (reference != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, errorMessageArgs));
                }
                return reference;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, char p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, int p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, long p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, char p1, @Nullable Object p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, char p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, int p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, long p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, int p1, @Nullable Object p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, char p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, int p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, long p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, long p1, @Nullable Object p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, char p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, int p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, long p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2, p3));
                }
                return obj;
            }

            public static <T> T isTrue(T obj, @Nullable String errorMessageTemplate, @Nullable Object p1, @Nullable Object p2, @Nullable Object p3, @Nullable Object p4) {
                if (obj != null) {
                    throw new IllegalStateException(Guard.format(errorMessageTemplate, p1, p2, p3, p4));
                }
                return obj;
            }



        }

        public static class ElementIndex {

            public static int isValid(int index, int size) {
                return isValid(index, size, "index");
            }

            public static int isValid(int index, int size, @Nullable String desc) {
                // Carefully optimized for execution by hotspot (explanatory comment above)
                if (index < 0 || index >= size) {
                    throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
                }
                return index;
            }

            private static String badElementIndex(int index, int size, @Nullable String desc) {
                if (index < 0) {
                    return Guard.format("%s (%s) must not be negative", desc, index);
                } else if (size < 0) {
                    throw new IllegalArgumentException("negative size: " + size);
                } else { // index >= size
                    return Guard.format("%s (%s) must be less than size (%s)", desc, index, size);
                }
            }
        }

        public static class PositionIndex {


            public static int isValid(int index, int size) {
                return isValid(index, size, "index");
            }

            public static int isValid(int index, int size, @Nullable String desc) {
                // Carefully optimized for execution by hotspot (explanatory comment above)
                if (index < 0 || index > size) {
                    throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
                }
                return index;
            }

            private static String badPositionIndex(int index, int size, @Nullable String desc) {
                if (index < 0) {
                    return Guard.format("%s (%s) must not be negative", desc, index);
                } else if (size < 0) {
                    throw new IllegalArgumentException("negative size: " + size);
                } else { // index > size
                    return Guard.format("%s (%s) must not be greater than size (%s)", desc, index, size);
                }
            }

            public static void isValid(int start, int end, int size) {
                // Carefully optimized for execution by hotspot (explanatory comment above)
                if (start < 0 || end < start || end > size) {
                    throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
                }
            }

            private static String badPositionIndexes(int start, int end, int size) {
                if (start < 0 || start > size) {
                    return badPositionIndex(start, size, "start index");
                }
                if (end < 0 || end > size) {
                    return badPositionIndex(end, size, "end index");
                }
                // end < start
                return Guard.format("end index (%s) must not be less than start index (%s)", end, start);
            }

        }

        public static class Bundle {
            public static void contains(android.os.Bundle arguments, String key) {
                if (arguments.containsKey(key)) {
                    throw new IllegalArgumentException("The bundle does not contain key; " + key);
                }
            }

        }

    }


    public static final class Does {

        public static class Bundle {
            public static boolean contains(android.os.Bundle arguments, String key) {
                return arguments.containsKey(key);
            }

        }

    }


}
