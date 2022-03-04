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

package org.sormas.e2etests.entities.services;

import com.github.javafaker.Faker;
import com.google.inject.Inject;
import org.sormas.e2etests.entities.pojo.web.QuarantineOrder;

public class ContactDocumentService {
  private final Faker faker;

  private final String emailDomain = "@CONTACT-DOCUMENT.com";

  @Inject
  public ContactDocumentService(Faker faker) {
    this.faker = faker;
  }

  public QuarantineOrder buildQuarantineOrder() {
    return QuarantineOrder.builder()
        .documentTemplate("ExampleDocumentTemplateContacts.docx")
        .sample("")
        .pathogenTest("")
        .extraComment("This is a test comment")
        .build();
  }
}
