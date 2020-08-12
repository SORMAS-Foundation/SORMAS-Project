package de.symeda.sormas.app.util;

import de.symeda.sormas.api.utils.fieldaccess.FieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers;

public class AppFieldAccessCheckers {

	private final boolean isInJurisdiction;
	private final FieldAccessCheckers fieldAccessCheckers;

	public AppFieldAccessCheckers(boolean isInJurisdiction) {
		this.isInJurisdiction = isInJurisdiction;
		fieldAccessCheckers = new de.symeda.sormas.api.utils.fieldaccess.FieldAccessCheckers();
	}

	public boolean isAccessible(Class<?> parentType, String fieldName) {
		return fieldAccessCheckers.isAccessible(parentType, fieldName, isInJurisdiction);
	}

	public AppFieldAccessCheckers add(FieldAccessChecker accessChecker) {
		fieldAccessCheckers.add(accessChecker);

		return this;
	}

	public static AppFieldAccessCheckers withCheckers(boolean isInJurisdiction, FieldAccessChecker... checkers) {
        AppFieldAccessCheckers fieldAccessCheckers = new AppFieldAccessCheckers(isInJurisdiction);

		for (FieldAccessChecker checker : checkers) {
			fieldAccessCheckers.add(checker);
		}

		return fieldAccessCheckers;
	}
}
