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
package org.sormas.e2etests.pojo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import lombok.*;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Builder(toBuilder = true, builderClassName = "builder")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
  String disease;
  String eventTitle;
  EventLocation eventLocation;
  String eventStatus;
  ReportingUser reportingUser;
  String eventInvestigationStatus;
  Date reportDateTime;
  String riskLevel;
  String srcType;
  String typeOfPlace;
  Boolean ownershipHandedOver;
  String uuid;
  Date startDate;
  Boolean multiDayEvent;
}
