package de.symeda.sormas.api.utils.fieldvisibility.checkers;

import java.lang.reflect.AccessibleObject;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.symeda.sormas.api.feature.FeatureConfigurationDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;

public class FeatureTypeFieldVisibilityChecker implements FieldVisibilityCheckers.FieldBasedChecker {

	private final Supplier<List<FeatureConfigurationDto>> featureConfigurationsSupplier;

	private List<FeatureConfigurationDto> featureConfigurations;

	public FeatureTypeFieldVisibilityChecker(Supplier<List<FeatureConfigurationDto>> featureConfigurationsSupplier) {
		this.featureConfigurationsSupplier = featureConfigurationsSupplier;
	}

	@Override
	public boolean isVisible(AccessibleObject field) {
		if (field.isAnnotationPresent(DependingOnFeatureType.class)) {
			DependingOnFeatureType annotation = field.getAnnotation(DependingOnFeatureType.class);
			FeatureType featureType = annotation.featureType();

			Optional<FeatureConfigurationDto> featureConfiguration =
				getFeatureConfigurations().stream().filter(c -> c.getFeatureType() == featureType).findFirst();

			return featureConfiguration.map(c -> {
				DependingOnFeatureType.FeatureProperty[] properties = annotation.properties();
				if (properties.length > 0) {
					// check if all properties are set as required by the annotation
					return Stream.of(properties).allMatch(p -> {
						Object propertyValue = c.getProperties() != null ? c.getProperties().get(p.property()) : null;
						if (propertyValue == null) {
							return false;
						}

						return p.value().equals(propertyValue.toString());
					});
				}

				return true;
			}).orElse(false);
		}

		return true;
	}

	private List<FeatureConfigurationDto> getFeatureConfigurations() {
		if (featureConfigurations == null) {
			featureConfigurations = featureConfigurationsSupplier.get();
		}
		return featureConfigurations;
	}
}
