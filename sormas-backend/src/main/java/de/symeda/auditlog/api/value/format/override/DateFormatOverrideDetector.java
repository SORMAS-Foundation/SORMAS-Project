package de.symeda.auditlog.api.value.format.override;

import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.Temporal;

import de.symeda.auditlog.api.value.format.UtilDateFormatter;
import de.symeda.auditlog.api.value.format.ValueFormatter;

/**
 * @author Oliver Milke
 * @since 11.04.2016
 */
public class DateFormatOverrideDetector implements OverrideDetector<Date> {

	/**
	 * Returns an adjusted {@link ValueFormatter} for an entity property as long as this is applicable according to the the method description.
	 * 
	 * @param m
	 * 			The method that describes the property.
	 * @return	Returns <code>null</code> if no reasonable derivation can be found. Returns a ValueFormatter that is adjusted to the value of the
	 * 			{@link Temporal} annotation, if present.
	 */
	@Override
	public ValueFormatter<Date> override(Method m) {

		if (!Date.class.isAssignableFrom(m.getReturnType())) {
			// no reasonable derivation possible
			return null;
		} else {
			// generally possible
			Temporal annotation = m.getAnnotation(Temporal.class);

			if (annotation == null) {
				return new UtilDateFormatter();
			} else {
				return new UtilDateFormatter(annotation.value());
			}
		}
	}
}
