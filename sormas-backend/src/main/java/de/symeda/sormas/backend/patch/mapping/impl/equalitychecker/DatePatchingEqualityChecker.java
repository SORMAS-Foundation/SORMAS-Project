package de.symeda.sormas.backend.patch.mapping.impl.equalitychecker;

import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.backend.patch.mapping.PatchingEqualityChecker;

/**
 * Dates are stored as {@link Date} but only the day is relevant, not time therefore using this approach.
 */
@ApplicationScoped
public class DatePatchingEqualityChecker implements PatchingEqualityChecker {

	public static final Set<Class<?>> SUPPORTED_TYPES = Set.of(Date.class);

	@Override
	public boolean areEqual(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null || b == null) {
			return false;
		}
		return toLocalDate((Date) a).equals(toLocalDate((Date) b));
	}

	@Override
	public Set<Class<?>> getSupportedTypes() {
		return SUPPORTED_TYPES;
	}

	@Override
	public int getOrder() {
		return HIGH_PRECEDENCE;
	}

	private java.time.LocalDate toLocalDate(Date date) {
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
