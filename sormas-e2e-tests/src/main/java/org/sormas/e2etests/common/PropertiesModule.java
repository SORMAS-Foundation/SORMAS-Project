/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package org.sormas.e2etests.common;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.emptyToNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This module allows bind system properties.
 *
 * <p>Usage: @Named("name.of.system.properties") String value
 *
 * <p>Use CONFIGURATION_PROPERTIES_COMMON_PROPERTIES_PATH value map to define common properties
 * which will be overwritten in if there are same values on the other property files
 *
 * <p>Use DEFAULT_ENVIRONMENT_PROPERTIES value map to define default properties from one of the
 * configuration/properties/environment files
 *
 * <p>the system.properties can be override by env variable because we are putting them after system
 * properties
 *
 * <p>Unified naming convention to access values is SNAKE_CASE.
 */
public class PropertiesModule extends AbstractModule {

  public static final String CONFIGURATION_PROPERTIES_COMMON_PROPERTIES_PATH =
      "configuration/properties/common.properties";
  public static final String ENVIRONMENT =
      firstNonNull(emptyToNull(System.getenv("ENVIRONMENT")), "test-performance");

  private static final Map<String, String> DEFAULT_ENVIRONMENT_PROPERTIES =
      MoreResources.getConfig(
          String.format("configuration/properties/environment/%s.properties", ENVIRONMENT));

  private static final Map<String, String> DEFAULT_COMMON_ENVIRONMENT_PROPERTIES =
      MoreResources.getConfig(CONFIGURATION_PROPERTIES_COMMON_PROPERTIES_PATH);

  @Override
  public void configure() {
    Map<String, String> allProperties = new HashMap<>();
    allProperties.putAll(DEFAULT_COMMON_ENVIRONMENT_PROPERTIES);
    allProperties.putAll(DEFAULT_ENVIRONMENT_PROPERTIES);
    allProperties.putAll(getProperties(System.getProperties()));
    allProperties.putAll(System.getenv());
    Names.bindProperties(binder(), allProperties);
  }

  @VisibleForTesting
  protected static String toSnakeCase(String envVariable) {
    return envVariable.replaceAll("[^A-Za-z0-9]", "_").toUpperCase();
  }

  /**
   * We are converting from Hashtable<Object, Object> to Map<String, String>
   *
   * @param properties received from the system
   * @return map of system properties
   */
  private static Map<String, String> getProperties(Properties properties) {
    return properties.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> toSnakeCase(e.getKey().toString()), e -> e.getValue().toString()));
  }
}
