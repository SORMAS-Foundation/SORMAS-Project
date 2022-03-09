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
package org.sormas.e2etests.constants.api;

public interface Endpoints {

  /** Main paths */
  String PERSONS_PATH = "persons/";

  String IMMUNIZATIONS_PATH = "immunizations/";
  String CASES_PATH = "cases/";
  String COMMUNITIES_PATH = "communities/";
  String COUNTRIES_PATH = "countries/";
  String FACILITIES_PATH = "facilities/";
  String CONTACTS_PATH = "contacts/";
  String EVENTS_PATH = "events/";
  String SAMPLES_PATH = "samples/";
  String TASKS_PATH = "tasks/";

  /** End points actions */
  String POST_PATH = "push";

  String DELETE_PATH = "deleted/";
  String UUIDS_PATH = "uuids";
}
