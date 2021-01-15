package de.symeda.sormas.api.utils.fieldvisibility.checkers;

import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

import java.lang.reflect.AccessibleObject;
import java.util.List;

public class FeatureTypeFieldVisibilityChecker implements FieldVisibilityCheckers.FieldBasedChecker {

	private final List<FeatureType> featureTypes;

	public FeatureTypeFieldVisibilityChecker(List<FeatureType> featureTypes) {
		this.featureTypes = featureTypes;
	}

	@Override
	public boolean isVisible(AccessibleObject field) {
		if (field.isAnnotationPresent(DependingOnFeatureType.class)) {
			return featureTypes.contains(field.getAnnotation(DependingOnFeatureType.class).featureType());
		}

		return true;
	}
}
