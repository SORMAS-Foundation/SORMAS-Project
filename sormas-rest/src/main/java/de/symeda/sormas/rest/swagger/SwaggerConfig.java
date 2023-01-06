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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.rest.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;
import io.swagger.v3.oas.integration.api.ObjectMapperProcessor;

/**
 * SORMAS Swagger Configuration.
 * This class is the preferred spot for specifying API-related Swagger metadata
 * via Swagger annotations,
 * as well as for Swagger extension registration.
 */
public class SwaggerConfig implements ObjectMapperProcessor {

    static {
        // Real initialization routine
        registerExtensions();
    }

    public static void init() {
        // Solemnly there for triggering the static initializer
    }

    /**
     * Register Swagger Generator Extensions
     */
    private static void registerExtensions() {
        // rewrite controller strings
        OpenAPIExtensions.getExtensions().add(new SormasSwaggerExtensions());

        // include annotations in openAPI specs
        ModelConverters.getInstance().addConverter(new AttributeConverter(Json.mapper()));
    }

    /**
     * Implementing ObjectMapperProcessor interface as it's a requirement of the
     * swagger maven plugin.
     */
    @Override
    public void processJsonObjectMapper(ObjectMapper mapper) {
        /*
         * The enums toString() method is sometimes used to return internationalized
         * captions that are used in the frontend. To show the Enum values instead of
         * the captions, disable the toString() for serialization and use the name()
         * method instead.
         */
        mapper.disable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        ModelResolver.enumsAsRef = false;
    }
}
