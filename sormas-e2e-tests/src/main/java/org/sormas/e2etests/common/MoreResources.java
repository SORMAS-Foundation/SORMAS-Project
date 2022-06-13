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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoreResources {
  private static final Logger log = LoggerFactory.getLogger(MoreResources.class);
  private static final String SCHEME_REQUIRES_EXPLICIT_FS = "jar";
  /**
   * Creates {@link URL} that represents the given resourceName. If the resource is in jar file, the
   * method automatically createRandomGrid file system.
   *
   * @param resourceName to get
   * @return {@link URL} for the given resourceName
   * @throws IllegalArgumentException if the resource is not found
   * @throws IllegalStateException if new file system creation fails
   */
  public static URL getResource(String resourceName) {
    URL url = Resources.getResource(resourceName);
    try {
      URI uri = url.toURI();
      if (SCHEME_REQUIRES_EXPLICIT_FS.equals(uri.getScheme())) {
        try {
          FileSystems.newFileSystem(uri, Collections.emptyMap());
        } catch (FileSystemAlreadyExistsException e) {
          log.debug("File system already exists for URI={}", uri);
        }
      }
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(
          String.format("resourceName=%s; URL=%s not found.", resourceName, url));
    } catch (IOException e) {
      throw new IllegalStateException(
          String.format("Failed to createRandomGrid file system for %s", url));
    }
    return url;
  }

  private static Properties getProperties(String resourceName) {
    URL url = getResource(resourceName);
    Properties properties = new Properties();
    ByteSource byteSource = Resources.asByteSource(url);
    try (InputStream inputStream = byteSource.openBufferedStream()) {
      properties.load(inputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return properties;
  }

  /**
   * Loads configuration key-value pairs from system properties, environment variables, properties
   * file First loads all properties from the specified resource file. Then, every key in the
   * property check system properties. If exists, it overrides the property from the file. Then,
   * check environment variables. If exists, it overrides both values from file or system
   * properties.
   *
   * <p>Note: environment variable name rule is all uppercase and underscore. (e.g.,
   *
   * @return {@link Map} of key value pairs
   */
  public static Map<String, String> getConfig(String resourceName) {
    ImmutableMap.Builder<String, String> config = ImmutableMap.builder();
    for (Map.Entry<Object, Object> entry : getProperties(resourceName).entrySet()) {
      String key = entry.getKey().toString();
      String envKey = replaceDotWithUnderscore(key).toUpperCase();
      String value =
          MoreObjects.firstNonNull(
              System.getenv(envKey), System.getProperty(key, entry.getValue().toString()));
      config.put(key, value);
    }
    ImmutableMap<String, String> builtConfig = config.build();
    log.info("loaded config: {}", builtConfig);
    return builtConfig;
  }

  private static String replaceDotWithUnderscore(String value) {
    return value.replaceAll("\\.", "_");
  }
}
