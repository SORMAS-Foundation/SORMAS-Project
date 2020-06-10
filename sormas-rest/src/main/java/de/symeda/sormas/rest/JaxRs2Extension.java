package de.symeda.sormas.rest;

import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Operation;

import java.lang.reflect.Method;
import java.util.Iterator;

public class JaxRs2Extension extends AbstractOpenAPIExtension {
	@Override
	public void decorateOperation(Operation operation, Method method, Iterator<OpenAPIExtension> chain) {
		super.decorateOperation(operation, method, chain);

		if (operation.getTags() == null || operation.getTags().size() == 0) {
			// Add name as Controller as Tag
			operation.addTagsItem(this.getControllerLabel(method.getDeclaringClass()));
		}
	}

	public String getControllerLabel(Class<?> clazz) {
		return clazz.getSimpleName()
				.replaceAll("Resource$", "")
				.replaceAll("(?<!^)[A-Z]", " $0")
				+ " Controller";
	}
}
