package de.symeda.sormas.backend.patch.mapping.impl.equalitychecker;

import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import de.symeda.sormas.backend.patch.mapping.EqualityChecker;

@ApplicationScoped
public class DateEqualityChecker implements EqualityChecker {

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
