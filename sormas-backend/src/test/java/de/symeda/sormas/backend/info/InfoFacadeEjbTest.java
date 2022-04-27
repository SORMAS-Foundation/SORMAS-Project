/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.info;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Test;

import de.symeda.sormas.api.user.DefaultUserRole;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;

public class InfoFacadeEjbTest extends AbstractBeanTest {

	private String originalCustomFilesPath;
	private String originalServerCountry;

	@Override
	public void init() {
		super.init();

		originalCustomFilesPath = MockProducer.getProperties().getProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH);
		if (originalCustomFilesPath == null) {
			originalCustomFilesPath = "";
		}

		originalServerCountry = MockProducer.getProperties().getProperty(ConfigFacadeEjb.COUNTRY_LOCALE);
		if (originalServerCountry == null) {
			originalServerCountry = "";
		}

		try {
			MockProducer.getProperties()
				.setProperty(
					ConfigFacadeEjb.CUSTOM_FILES_PATH,
					Paths.get(getClass().getResource("/").toURI()).toAbsolutePath().toString() + "/dataDictionaryTestCustom");
		} catch (URISyntaxException e) {
			throw new RuntimeException("Could not set custom files path", e);
		}

		UserDto admin = creator.createUser(creator.createRDCF(), creator.getUserRoleReferenceDtoMap().get(DefaultUserRole.ADMIN));
		loginWith(admin);
	}

	@After
	public void destroy() {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, originalCustomFilesPath);
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, originalServerCountry);
	}

	@Test
	public void testDataDictionaryAllowed() {
		assertThat(getInfoFacade().isGenerateDataProtectionDictionaryAllowed(), is(true));
	}

	@Test
	public void testFieldsAddedBasedOnServerCountryForDataProtectionDictionary() throws IOException, InvalidFormatException {
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "en");
		XSSFWorkbook workbook = new XSSFWorkbook(new File(getInfoFacade().generateDataProtectionDictionary()));

		assertThat(isFieldAdded(workbook, "Person", "Person.nickname"), is(true));
		assertThat(isFieldAdded(workbook, "CaseData", "CaseData.epidNumber"), is(true));
		assertThat(isFieldAdded(workbook, "CaseData", "Entity"), is(false));
		assertThat(isFieldAdded(workbook, "All fields", "Entity"), is(true));
		assertThat(isFieldAdded(workbook, "All fields", "Person"), is(true));

		MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "de");
		workbook = new XSSFWorkbook(new File(getInfoFacade().generateDataProtectionDictionary()));
		assertThat(isFieldAdded(workbook, "Person", "Person.nickname"), is(false));
		assertThat(isFieldAdded(workbook, "CaseData", "CaseData.epidNumber"), is(false));
		assertThat(isFieldAdded(workbook, "All fields", "Entity"), is(true));
		assertThat(isFieldAdded(workbook, "All fields", "Person"), is(true));
	}

	private boolean isFieldAdded(XSSFWorkbook dataDictionaryWb, String sheetName, String fieldId) throws IOException, InvalidFormatException {
		XSSFSheet sheet = dataDictionaryWb.getSheet(sheetName);

		Iterator<Row> rowIterator = sheet.rowIterator();

		boolean found = false;
		while (!found && rowIterator.hasNext()) {
			Row row = rowIterator.next();

			String stringCellValue = row.getCell(0).getStringCellValue();

			found = fieldId.equals(stringCellValue);
		}

		return found;
	}
}
