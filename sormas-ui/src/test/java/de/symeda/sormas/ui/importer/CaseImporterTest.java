package de.symeda.sormas.ui.importer;

import static org.mockito.Mockito.when;

import java.security.Principal;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.util.CurrentInstance;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.TestDataCreator.RDCF;

@RunWith(MockitoJUnitRunner.class)
public class CaseImporterTest extends AbstractBeanTest {

	@Mock
	private VaadinServletRequest request;

	@Before
	public void initUI() throws Exception {

		creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);

		when(request.getUserPrincipal()).thenReturn(new Principal() {
			@Override
			public String getName() {
				return "admin";
			}
		});

		CurrentInstance.setInheritable(VaadinRequest.class, request);

		// TODO init UI
	}

	private static final int EXPECTED_NUMBER_OF_LINES = 5;

	@Test
	public void testImportAllCases() {

		RDCF rdcf = creator.createRDCF("Abia", "Osisioma Ngwa", "Community", "Amavo Ukwu Health Post");
		DistrictDto district = creator.createDistrict("Bende", rdcf.region.toReference());
		creator.createFacility("Akoli Health Centre", rdcf.region.toReference(), district.toReference(), null);
		UserDto user = creator.createUser(null, null, null, "ad", "min", UserRole.ADMIN, UserRole.NATIONAL_USER);

		FacadeProvider.getCaseFacade().getAllActiveCasesAfter(null, user.getUuid());

//		InputStream inputStream = ImportFacadeEjbTest.class.getResourceAsStream("/sormas_import_test.csv");
//	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8.name());
//		
//		getImportFacade().importCasesFromCsvFile(new InputStreamReader(inputStream), osw, user.getUuid());
//		
//		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(baos.toByteArray()));
//		InputStreamReader reader = new InputStreamReader(bis);
//		CSVReader csvReader = CSVUtils.createCSVReader(reader, getConfigFacade().getCsvSeparator());
//		
//		int numberOfLines = 0;
//		while (csvReader.readNext() != null) {
//			numberOfLines++;
//		}
//		
//		// The .csv file should have six lines including the header, one of which should import correctly
//		assertEquals(EXPECTED_NUMBER_OF_LINES, numberOfLines);
	}

}
