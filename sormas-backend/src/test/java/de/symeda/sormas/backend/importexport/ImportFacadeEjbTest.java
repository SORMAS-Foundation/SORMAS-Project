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
