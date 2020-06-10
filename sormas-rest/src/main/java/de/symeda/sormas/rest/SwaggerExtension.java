package de.symeda.sormas.rest;

import de.symeda.sormas.api.utils.Required;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SwaggerExtension extends AbstractOpenAPIExtension implements ModelConverter {
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

			if (schema != null && annotatedType.isSchemaProperty()
					&& annotatedType.getCtxAnnotations() != null) {
				boolean isRequired = Arrays.stream(annotatedType.getCtxAnnotations())
						.anyMatch((Annotation a) -> a.annotationType() == Required.class);

				String propName = annotatedType.getPropertyName();
				List<String> currRequired = annotatedType.getParent().getRequired();
				if (isRequired && (currRequired == null || currRequired.stream()
						.noneMatch((String prop) -> prop.equals(propName)))) {
					annotatedType.getParent().addRequiredItem(propName);
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
