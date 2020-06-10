/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;

import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.Required;

/**
 * Provides Swagger documentation support for SORMAS' custom annotations.
 *
 * @author Jan-Niklas Brandes
 */
public class SwaggerExtension extends AbstractOpenAPIExtension implements ModelConverter {

	public static final String XPROP_PREFIX = "x-sormas-";
	public static final String XPROP_PERSONAL_DATA = XPROP_PREFIX + "personal-data";
	public static final String XPROP_FOR_COUNTRIES = XPROP_PREFIX + "countries-for";
	public static final String XPROP_EXCEPT_COUNTRIES = XPROP_PREFIX + "countries-except";
	public static final String XPROP_DEPENDS_ON = XPROP_PREFIX + "depends-on";

	@Override
	public void decorateOperation(Operation operation, Method method, Iterator<OpenAPIExtension> chain) {
		super.decorateOperation(operation, method, chain);

		if (operation.getTags() == null || operation.getTags().size() == 0) {
			// Add name of the declaring controller as tag for operation grouping
			operation.addTagsItem(this.getControllerLabel(method.getDeclaringClass()));
		}
	}

	@Override
	public Schema<?> resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> iterator) {

		if (!iterator.hasNext()) {
			return null;
		} else {
			ModelConverter next = iterator.next();
			Schema<?> schema = next.resolve(annotatedType, modelConverterContext, iterator);

			if (schema != null && annotatedType.getCtxAnnotations() != null) {
				// Required field documentation
				if (annotatedType.isSchemaProperty()) {
					boolean isRequired =
						Arrays.stream(annotatedType.getCtxAnnotations()).anyMatch((Annotation a) -> a.annotationType() == Required.class);

					String propName = annotatedType.getPropertyName();
					List<String> currRequired = annotatedType.getParent().getRequired();
					if (isRequired && (currRequired == null || currRequired.stream().noneMatch((String prop) -> prop.equals(propName)))) {
						annotatedType.getParent().addRequiredItem(propName);
					}
				}

				// Personal data documentation
				if (Arrays.stream(annotatedType.getCtxAnnotations()).anyMatch((Annotation a) -> a.annotationType() == PersonalData.class)) {
					schema.addExtension(XPROP_PERSONAL_DATA, true);
				}

				// Geo-filtering documentation
				Set<String> includeCountries = new HashSet<>();
				Set<String> excludeCountries = new HashSet<>();

				List<Annotation> hfcs = Arrays.stream(annotatedType.getCtxAnnotations()).filter((Annotation a) ->
					//@formatter:off
						a.annotationType() == HideForCountries.class ||
						a.annotationType() == HideForCountriesExcept.class)
						//@formatter:on
					.collect(Collectors.toList());

				for (Annotation hfcAnnotation : hfcs) {
					if (hfcAnnotation instanceof HideForCountries) {
						for (String country : ((HideForCountries) hfcAnnotation).countries()) {
							excludeCountries.add(country);
							includeCountries.remove(country);
						}
					} else if (hfcAnnotation instanceof HideForCountriesExcept) {
						for (String country : ((HideForCountriesExcept) hfcAnnotation).countries()) {
							includeCountries.add(country);
							excludeCountries.remove(country);
						}
					}
				}

				if (includeCountries.size() > 0) {
					schema.addExtension(XPROP_FOR_COUNTRIES, includeCountries);
				}
				if (excludeCountries.size() > 0) {
					schema.addExtension(XPROP_EXCEPT_COUNTRIES, excludeCountries);
				}

				// Field dependency
				List<String> dependencies = Arrays.stream(annotatedType.getCtxAnnotations())
					.filter((Annotation a) -> a.annotationType() == DependantOn.class)
					.map((Annotation a) -> ((DependantOn) a).value())
					.collect(Collectors.toList());
				if (dependencies.size() > 0) {
					schema.addExtension(XPROP_DEPENDS_ON, dependencies);
				}
			}

			return schema;
		}
	}

	/**
	 * Generate a user-friendly name label for the given controller class.
	 */
	public String getControllerLabel(Class<?> clazz) {
		return clazz.getSimpleName().replaceAll("Resource$", "").replaceAll("(?<!^)[A-Z]", " $0") + " Controller";
	}
}
