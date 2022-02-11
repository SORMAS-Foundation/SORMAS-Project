/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
package org.sormas.e2etests.envconfig.configprovider;

import java.io.File;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ConfigFileReader {

    private static final String systemVariableName = "envConfig";

    @SneakyThrows
    public static File getConfigurationFile() {
        String jsonPath;
        try {
            log.info("Looking after environment environmentconfig file");
            jsonPath = System.getProperty(systemVariableName);
        } catch (NullPointerException e) {
            throw new Exception("Unable to find environment environmentconfig file: " + e.getMessage());
        }
        try {
            log.info("Returning environmentconfig file path");
            return new File(jsonPath);
        } catch (Exception any) {
            throw new Exception(
                    "Unable to convert provided environmentconfig file into File object: "
                            + any.getMessage());
        }
    }
}
