package de.symeda.sormas.api.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.symeda.sormas.api.Disease;

// This annotation is used to mark/show the complicated symptoms per disease.
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Complication {

    Disease[] value() default {};

}
