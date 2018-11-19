/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 *******************************************************************************/
package de.symeda.sormas.backend.importexport;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

import org.junit.Test;

import com.opencsv.CSVReader;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.backend.region.District;

public class ImportFacadeEjbTest extends AbstractBeanTest {

	private static final int EXPECTED_NUMBER_OF_LINES = 5;
	
	@Test
	public void testCaseImport() throws IOException, InvalidColumnException {
		when(MockProducer.getSessionContext().getCallerPrincipal()).thenReturn(new Principal() {
            @Override
            public String getName() {
                return "admin";
            }
        });
		
		RDCF rdcf = creator.createRDCF("Abia", "Osisioma Ngwa", "Community", "Amavo Ukwu Health Post");
		District district = creator.createDistrict("Bende", rdcf.region);
		creator.createFacility("Akoli Health Centre", rdcf.region, district, null);
		UserDto user = creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);
		
		InputStream inputStream = ImportFacadeEjbTest.class.getResourceAsStream("/sormas_import_test.csv");
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8.name());
		
		getImportFacade().importCasesFromCsvFile(new InputStreamReader(inputStream), osw, user.getUuid());
		
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
		InputStreamReader reader = new InputStreamReader(bis);
		CSVReader csvReader = CSVUtils.createCSVReader(reader, getConfigFacade().getCsvSeparator());
		
		int numberOfLines = 0;
		while (csvReader.readNext() != null) {
			numberOfLines++;
		}
		
		// The .csv file should have six lines including the header, one of which should import correctly
		assertEquals(EXPECTED_NUMBER_OF_LINES, numberOfLines);
	}
}
