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

package org.sormas.e2etests.pages.application.configuration;

import org.openqa.selenium.By;

public class ConfigurationTabsPage {
  public static final By CONFIGURATION_OUTBREAKS_TAB =
      By.cssSelector("div#tab-configuration-outbreaks");
  public static final By CONFIGURATION_CONTINENTS_TAB =
      By.cssSelector("div#tab-configuration-continents");
  public static final By CONFIGURATION_SUBCONTINENTS_TAB =
      By.cssSelector("div#tab-configuration-subcontinents");
  public static final By CONFIGURATION_COUNTRIES_TAB =
      By.cssSelector("div#tab-configuration-countries");
  public static final By CONFIGURATION_REGIONS_TAB =
      By.cssSelector("div#tab-configuration-regions");
  public static final By CONFIGURATION_DISTRICTS_TAB =
      By.cssSelector("div#tab-configuration-districts");
  public static final By CONFIGURATION_COMMUNITIES_TAB =
      By.cssSelector("div#tab-configuration-communities");
  public static final By CONFIGURATION_FACILITIES_TAB =
      By.cssSelector("div#tab-configuration-facilities");
  public static final By CONFIGURATION_POINTS_OF_ENTRY_TAB =
      By.cssSelector("div#tab-configuration-pointsofentry");
  public static final By CONFIGURATION_POPULATION_TAB =
      By.cssSelector(("div#tab-configuration-populationdata"));
  public static final By CONFIGURATION_LINE_LISTING_TAB =
      By.cssSelector("div#tab-configuration-linelisting");
  public static final By CONFIGURATION_DOCUMENT_TEMPLATES_TAB =
      By.cssSelector("div#tab-configuration-documentTemplates");
}
