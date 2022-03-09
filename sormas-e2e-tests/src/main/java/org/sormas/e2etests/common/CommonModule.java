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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.javafaker.Faker;
import com.google.inject.Exposed;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import javax.inject.Singleton;
import lombok.SneakyThrows;
import org.sormas.e2etests.envconfig.manager.EnvironmentManager;
import org.sormas.e2etests.webdriver.DriverManager;
import org.testng.asserts.SoftAssert;

public class CommonModule extends PrivateModule {

  @Override
  protected void configure() {
    bind(DriverManager.class).in(Singleton.class);
    expose(DriverManager.class);
  }

  @Provides
  @Singleton
  @Exposed
  Faker provideFaker() {
    return new Faker(Locale.GERMANY);
  }

  @Provides
  @Singleton
  @Exposed
  EnvironmentManager provideEnvManager() {
    return new EnvironmentManager();
  }

  @Provides
  @Singleton
  @Exposed
  SoftAssert provideSoftAssertions() {
    return new SoftAssert();
  }

  @SneakyThrows
  @Provides
  @Singleton
  @Exposed
  Properties provideProperties() {
    Properties prop = new Properties();
    InputStream inputStream;
    String propFileName = "enum.properties";
    inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
    if (inputStream != null) {
      prop.load(inputStream);
    } else {
      throw new FileNotFoundException(
          "property file '" + propFileName + "' not found in the classpath");
    }
    return prop;
  }

  @Provides
  @Exposed
  ObjectMapper provideObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.writer().withDefaultPrettyPrinter();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.registerModule(new Jdk8Module());
    return objectMapper;
  }
}
