package de.symeda.sormas.backend.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields whose getters are annotated with this annotation are ignored when building .csv templates 
 * and importing entities.
 * 
 * @author Mate Strysewske
 */
@Target({
	ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ImportIgnore {

}
