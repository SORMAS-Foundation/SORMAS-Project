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

import io.swagger.v3.jaxrs2.ext.AbstractOpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.oas.models.Operation;

import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Provides Swagger documentation support for SORMAS' custom annotations.
 */
public class SormasSwaggerExtensions extends AbstractOpenAPIExtension {

    @Override
    public void decorateOperation(Operation operation, Method method, Iterator<OpenAPIExtension> chain) {
        super.decorateOperation(operation, method, chain);

        if (operation.getTags() == null || operation.getTags().size() == 0) {
            // Add name of the declaring controller as tag for operation grouping
            operation.addTagsItem(this.getControllerLabel(method.getDeclaringClass()));
        }
    }


    /**
     * Generate a user-friendly name label for the given controller class.
     */
    public String getControllerLabel(Class<?> clazz) {
        return clazz.getSimpleName().replaceAll("Resource$", "").replaceAll("(?<!^)[A-Z]", " $0") + " Controller";
    }
}
