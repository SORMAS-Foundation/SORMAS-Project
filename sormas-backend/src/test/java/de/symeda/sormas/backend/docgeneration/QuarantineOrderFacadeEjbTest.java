package de.symeda.sormas.backend.docgeneration;

import com.auth0.jwt.internal.org.apache.commons.io.IOUtils;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.docgeneneration.QuarantineOrderFacade;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.MockProducer;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class QuarantineOrderFacadeEjbTest extends AbstractBeanTest {

	private QuarantineOrderFacade quarantineOrderFacadeEjb;
	private CaseDataDto caseDataDto;
	private PersonDto personDto;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

	@Before
	public void setup() throws ParseException {
		quarantineOrderFacadeEjb = getQuarantineOrderFacade();
		MockProducer.getProperties().setProperty(ConfigFacadeEjb.CUSTOM_FILES_PATH, getClass().getResource("/").getPath());

		LocationDto locationDto = new LocationDto();
		locationDto.setStreet("Nauwieserstraße");
		locationDto.setHouseNumber("7");
		locationDto.setCity("Saarbrücken");
		locationDto.setPostalCode("66111");

		personDto = PersonDto.build();
		personDto.setFirstName("Guy");
		personDto.setLastName("Debord");
		personDto.setBirthdateYYYY(1931);
		personDto.setBirthdateMM(12);
		personDto.setBirthdateDD(28);
		personDto.setAddress(locationDto);
		personDto.setPhone("+49 681 1234");

		getPersonFacade().savePerson(personDto);

		caseDataDto = creator.createUnclassifiedCase(Disease.CORONAVIRUS);
		caseDataDto.setPerson(personDto.toReference());
		caseDataDto.setQuarantineFrom(dateFormat.parse("10/09/2020"));
		caseDataDto.setQuarantineTo(dateFormat.parse("24/09/2020"));
		caseDataDto.setQuarantineOrderedOfficialDocumentDate(dateFormat.parse("09/09/2020"));

		getCaseFacade().saveCase(caseDataDto);
	}

	@Test
	public void generateQuarantineOrder() throws IOException {
		ByteArrayInputStream generatedDocument =
			new ByteArrayInputStream(quarantineOrderFacadeEjb.getGeneratedDocument("Quarantine.docx", caseDataDto.getUuid(), new Properties()));

		XWPFDocument xwpfDocument = new XWPFDocument(generatedDocument);
		XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(xwpfDocument);
		String docxText = xwpfWordExtractor.getText();

		StringWriter writer = new StringWriter();
		IOUtils.copy(getClass().getResourceAsStream("/docgeneration/quarantine/Quarantine.txt"), writer, "UTF-8");

		String expected = writer.toString().replaceAll("\\r\\n?", "\n");
		assertEquals(expected, docxText);
		System.out.println("  document generated.");
	}
}
