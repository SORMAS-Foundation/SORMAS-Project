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

import static org.sormas.e2etests.steps.BaseSteps.locale;

import com.google.inject.Inject;
import java.util.Date;
import java.util.UUID;
import org.sormas.e2etests.entities.pojo.api.AssociatedCase;
import org.sormas.e2etests.entities.pojo.api.Case;
import org.sormas.e2etests.entities.pojo.api.Lab;
import org.sormas.e2etests.entities.pojo.api.ReportingUser;
import org.sormas.e2etests.entities.pojo.api.Sample;
import org.sormas.e2etests.enums.RegionsValues;
import org.sormas.e2etests.enums.UserRoles;
import org.sormas.e2etests.envconfig.manager.RunningConfiguration;
import org.sormas.e2etests.helpers.RestAssuredClient;
import org.sormas.e2etests.helpers.environmentdata.manager.EnvironmentManager;

public class SampleApiService {
  RunningConfiguration runningConfiguration;

  private RestAssuredClient restAssuredClient;

  @Inject
  public SampleApiService(
      RunningConfiguration runningConfiguration, RestAssuredClient restAssuredClient) {
    this.restAssuredClient = restAssuredClient;
    this.runningConfiguration = runningConfiguration;
  }

  public Sample buildGeneratedSample(Case caze) {
    EnvironmentManager environmentManager = new EnvironmentManager(restAssuredClient);
    return Sample.builder()
        .uuid(UUID.randomUUID().toString())
        .reportingUser(
            ReportingUser.builder()
                .uuid(
                    runningConfiguration
                        .getUserByRole(locale, UserRoles.RestUser.getRole())
                        .getUuid())
                .build())
        .reportDateTime(new Date())
        .sampleDateTime(new Date())
        .associatedCase(AssociatedCase.builder().uuid(caze.getUuid()).build())
        .sampleMaterial("BLOOD")
        .samplePurpose("EXTERNAL")
        .specimenCondition("ADEQUATE")
        .pathogenTestResult("PENDING")
        .lab(
            Lab.builder()
                .caption("Voreingestelltes Labor")
                .uuid(
                    environmentManager.getLaboratoryUUID(
                        RegionsValues.VoreingestellteBundeslander.getName(),
                        "Voreingestelltes Labor"))
                .build())
        .labDetails("Dexter's laboratory")
        .build();
  }
}
