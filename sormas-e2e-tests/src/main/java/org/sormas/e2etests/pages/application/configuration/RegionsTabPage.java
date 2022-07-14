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

public class RegionsTabPage {
    public static final By NUMBER_OF_REGIONS =
            By.xpath(
                    "//div[@class='v-label v-widget bold v-label-bold vspace-top-none v-label-vspace-top-none align-right v-label-align-right v-label-undef-w']");
    public static final By REGIONS_TABLE_DATA = By.tagName("td");
    public static final By REGIONS_TABLE_ROW = By.cssSelector("div.v-grid-tablewrapper tbody tr");
    public static final By REGIONS_COLUMN_HEADERS =
            By.cssSelector("thead" + " .v-grid-column-default-header-content");
    public static final By COUNTRY_REGION_FILTER_COMBOBOX =
            By.cssSelector("[id='country'] [class='v-filterselect-button']");
}