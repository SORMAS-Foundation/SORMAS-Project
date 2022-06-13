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

package org.sormas.e2etests.webdriver;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.net.URI;
import lombok.Builder;
import lombok.Value;
import org.sormas.e2etests.enums.OperatingSystems;

@Builder(toBuilder = true, builderClassName = "Builder")
@JsonDeserialize(builder = DriverMetaData.Builder.class)
@Value
public class DriverMetaData {

  URI gridUrl;
  OperatingSystems operatingSystem;
  String systemArchitecture;
  boolean isRemoteWebDriverEnabled;
  boolean isProxyEnabled;
  String proxyHostname;
  int proxyPort;
  String proxyDetail;
  String userDirProperty;
  String browser;
  String environmentUrl;
}
