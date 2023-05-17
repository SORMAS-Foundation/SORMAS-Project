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

package org.sormas.e2etests.entities.services.api;

import static org.sormas.e2etests.entities.pojo.helpers.ShortUUIDGenerator.generateShortUUID;
import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.SneakyThrows;
import org.sormas.e2etests.entities.pojo.api.Event;
import org.sormas.e2etests.entities.pojo.api.EventParticipant;
import org.sormas.e2etests.entities.pojo.api.Person;
import org.sormas.e2etests.entities.pojo.api.ReportingUser;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;

public class EventParticipantApiService {

  private static RunningConfiguration runningConfiguration;
  private RestAssuredClient restAssuredClient;

  @Inject
  public EventParticipantApiService(
      Faker faker, RunningConfiguration runningConfiguration, RestAssuredClient restAssuredClient) {

    this.restAssuredClient = restAssuredClient;
    this.runningConfiguration = runningConfiguration;
  }

  @SneakyThrows
  public EventParticipant buildGeneratedEventParticipantWithCreationDate(
      String eventUUID, Person person, Integer days) {
    return EventParticipant.builder()
        .creationDate(
            LocalDateTime.parse(LocalDateTime.now().minusDays(days).toString())
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli())
        .uuid(generateShortUUID())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .person(
            Person.builder()
                .uuid(person.getUuid())
                .sex(person.getSex())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .build())
        .event(Event.builder().uuid(eventUUID).build())
        .build();
  }
}
