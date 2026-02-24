package de.symeda.sormas.api.patch.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an enum constant as the default fallback value for {@link ValuePatchMapper} for enums.
 * Takes precedence over the conventional "OTHER" fallback.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueMapperDefault {

}
