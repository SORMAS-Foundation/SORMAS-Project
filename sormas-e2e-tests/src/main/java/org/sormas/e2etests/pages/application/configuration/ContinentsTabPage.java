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
package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class ContinentsTabPage {
    public static final By NUMBER_OF_CONTINENTS =
            By.xpath(
                    "/html/body/div[1]/div/div[2]/div/div[2]/div/div/div/div[3]/div/div/div[2]/div/div/div[3]/div");
    public static final By CONTINENTS_TABLE_DATA = By.tagName("td");
    public static final By CONTINENTS_TABLE_ROW = By.cssSelector("div.v-grid-tablewrapper tbody tr");
    public static final By CONTINENTS_COLUMN_HEADERS =
            By.cssSelector("thead" + " .v-grid-column-default-header-content");
}