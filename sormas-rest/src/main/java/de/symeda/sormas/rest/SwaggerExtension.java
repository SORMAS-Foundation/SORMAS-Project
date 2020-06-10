package de.symeda.sormas.rest;

import de.symeda.sormas.api.utils.*;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			// Add name as Controller as Tag
			operation.addTagsItem(this.getControllerLabel(method.getDeclaringClass()));
		}
	}

	@Override
	public Schema resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> iterator) {
		if (!iterator.hasNext()) {
			return null;
		} else {
			ModelConverter next = iterator.next();
			Schema schema = next.resolve(annotatedType, modelConverterContext, iterator);

			if (schema != null && annotatedType.getCtxAnnotations() != null) {
				// Required field documentation
				if (annotatedType.isSchemaProperty()) {
					boolean isRequired = Arrays.stream(annotatedType.getCtxAnnotations())
							.anyMatch((Annotation a) -> a.annotationType() == Required.class);

					String propName = annotatedType.getPropertyName();
					List<String> currRequired = annotatedType.getParent().getRequired();
					if (isRequired && (currRequired == null || currRequired.stream()
							.noneMatch((String prop) -> prop.equals(propName)))) {
						annotatedType.getParent().addRequiredItem(propName);
					}
				}

				// Personal data documentation
				if (Arrays.stream(annotatedType.getCtxAnnotations())
						.anyMatch((Annotation a) -> a.annotationType() == PersonalData.class)) {
					schema.addExtension(XPROP_PERSONAL_DATA, true);
				}

				// Geoblocking documentation
				List<Annotation> hfcs = Arrays.stream(annotatedType.getCtxAnnotations())
						.filter((Annotation a) ->
								a.annotationType() == HideForCountries.class
								|| a.annotationType() == HideForCountriesExcept.class)
						.collect(Collectors.toList());
				Set<String> includeCountries = new HashSet<>();
				Set<String> excludeCountries = new HashSet<>();
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

	public String getControllerLabel(Class<?> clazz) {
		return clazz.getSimpleName()
				.replaceAll("Resource$", "")
				.replaceAll("(?<!^)[A-Z]", " $0")
				+ " Controller";
	}
}
