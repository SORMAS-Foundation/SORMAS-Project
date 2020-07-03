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
package de.symeda.sormas.rest.swagger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.utils.Complication;
import de.symeda.sormas.api.utils.DependantOn;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.HideForCountries;
import de.symeda.sormas.api.utils.HideForCountriesExcept;
import de.symeda.sormas.api.utils.Outbreaks;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.Required;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;

/**
 * Provides Swagger documentation support for SORMAS' custom annotations.
 *
 * @author Jan-Niklas Brandes
 */
public class SormasSwaggerExtensions extends AbstractOpenAPIExtension implements ModelConverter {

	public static final String XPROP_PREFIX = "x-sormas-";
	public static final String XPROP_PERSONAL_DATA = XPROP_PREFIX + "personal-data";
	public static final String XPROP_FOR_COUNTRIES = XPROP_PREFIX + "countries-for";
	public static final String XPROP_EXCEPT_COUNTRIES = XPROP_PREFIX + "countries-except";
	public static final String XPROP_DEPENDS_ON = XPROP_PREFIX + "depends-on";
	public static final String XPROP_DISEASES = XPROP_PREFIX + "diseases";
	public static final String XPROP_OUTBREAKS = XPROP_PREFIX + "outbreaks";
	public static final String XPROP_COMPLICATIONS = XPROP_PREFIX + "complications";

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

			//@formatter:off
			if (schema != null && annotatedType.getCtxAnnotations() != null
				&& annotatedType.isSchemaProperty()) {

				for (Annotation a : annotatedType.getCtxAnnotations()) {
					if (a.annotationType() == Required.class) {
						// Required field documentation
						String propName = annotatedType.getPropertyName();
						List<String> currRequired = annotatedType.getParent().getRequired();

						if (currRequired == null || currRequired.stream()
							.noneMatch((String prop) -> prop.equals(propName))) {

							annotatedType.getParent().addRequiredItem(propName);
						}

					} else if (a.annotationType() == PersonalData.class) {
						// Outbreak visibility documentation
						schema.addExtension(XPROP_PERSONAL_DATA, true);

					} else if (a.annotationType() == Outbreaks.class) {
						// Complications documentation
						schema.addExtension(XPROP_OUTBREAKS, true);

					} else if (a.annotationType() == Complication.class) {
						// Complications documentation
						schema.addExtension(XPROP_COMPLICATIONS, true);

					} else if (a.annotationType() == Diseases.class) {
						// Disease association documentation
						Disease[] diseases = ((Diseases) a).value();
						if (diseases.length > 0) {
							schema.addExtension(XPROP_DISEASES, diseases);
						}

					} else if (a.annotationType() == DependantOn.class) {
						// Field dependency documentation
						schema.addExtension(XPROP_DEPENDS_ON, ((DependantOn) a).value());

					} else if (a.annotationType() == HideForCountries.class) {
						// Documentation of countries to hide the field from
						if (schema.getExtensions() != null) {
							schema.getExtensions().remove(XPROP_FOR_COUNTRIES);
						}
						schema.addExtension(XPROP_EXCEPT_COUNTRIES, ((HideForCountries) a).countries());

					} else if (a.annotationType() == HideForCountriesExcept.class) {
						// Documentation of countries to show field for
						if (schema.getExtensions() == null || !schema.getExtensions().containsKey(XPROP_EXCEPT_COUNTRIES)) {
							schema.addExtension(XPROP_FOR_COUNTRIES, ((HideForCountriesExcept) a).countries());
						}
					}
				}
				//@formatter:on
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
