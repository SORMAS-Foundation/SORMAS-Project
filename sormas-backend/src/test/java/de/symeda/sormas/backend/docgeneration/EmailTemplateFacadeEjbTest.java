/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.docgeneration;

import static de.symeda.sormas.backend.docgeneration.TemplateTestUtil.cleanLineSeparators;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.DocumentTemplateException;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.backend.TestDataCreator;

public class EmailTemplateFacadeEjbTest extends AbstractDocGenerationTest {
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private TestDataCreator.RDCF rdcf;
	private UserDto userDto;

    private PersonDto personDto;

	@Override
	public void init() {
		super.init();

		rdcf = creator.createRDCF("Region", "District", "Community", "Facility", "PointOfEntry");

		userDto = creator.createUser(
			rdcf.region.getUuid(),
			rdcf.district.getUuid(),
			rdcf.facility.getUuid(),
			"Surv",
			"Sup",
			creator.getUserRoleReference(DefaultUserRole.SURVEILLANCE_SUPERVISOR));
		loginWith(userDto);

        LocationDto locationDto = LocationDto.build();
        locationDto.setStreet("Nauwieserstraße");
        locationDto.setHouseNumber("7");
        locationDto.setCity("Saarbrücken");
        locationDto.setPostalCode("66111");

        personDto = PersonDto.build();
        personDto.setFirstName("Guy");
        personDto.setLastName("Debord");
        personDto.setSex(Sex.UNKNOWN);
        personDto.setBirthdateYYYY(1931);
        personDto.setBirthdateMM(12);
        personDto.setBirthdateDD(28);
        personDto.setAddress(locationDto);
        personDto.setPhone("+49 681 1234");

        getPersonFacade().save(personDto);
    }

	@BeforeEach
	public void setup() throws URISyntaxException {
		reset();
	}

	@Test
    public void testGenerateCaseEmailContent() throws DocumentTemplateException, IOException {

        CaseDataDto caze = creator.createCase(userDto.toReference(), rdcf, (c) -> {
            c.setDisease(Disease.CORONAVIRUS);
            c.setPerson(personDto.toReference());
            c.setQuarantineFrom(DateHelper.parseDate("10/09/2020", DATE_FORMAT));
            c.setQuarantineTo(DateHelper.parseDate("24/09/2020", DATE_FORMAT));
            c.setQuarantineOrderedOfficialDocumentDate(DateHelper.parseDate("09/09/2020", DATE_FORMAT));
        });

        Properties properties = new Properties();
        properties.setProperty("extraremark1", "the first remark");
        properties.setProperty("extra_remark_no3", "the third remark");
        properties.setProperty("extraComment", "some Comment");


        String content = getEmailTemplateFacade().generateCaseEmailContent("CaseEmail.txt", caze.toReference(), properties);

        StringWriter writer = new StringWriter();
        IOUtils.copy(getClass().getResourceAsStream("/docgeneration/emailTemplates/cases/CaseEmail.cmp"), writer, "UTF-8");

        String expected = cleanLineSeparators(writer.toString());
        assertEquals(expected, content);
    }
}
