/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package travelentry.importer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.importexport.ValueSeparator;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryType;
import de.symeda.sormas.api.travelentry.TravelEntryDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.ui.AbstractBeanTest;
import de.symeda.sormas.ui.MockProducer;
import de.symeda.sormas.ui.TestDataCreator;
import de.symeda.sormas.ui.importer.ImportResultStatus;
import de.symeda.sormas.ui.travelentry.importer.TravelEntryImporter;

@RunWith(MockitoJUnitRunner.class)
public class TravelEntryImporterTest extends AbstractBeanTest {

	@Test
	public void testImportAllTravelEntries()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		TestDataCreator tdc = new TestDataCreator();
		TestDataCreator.RDP rdp = tdc.createRDP();
		UserDto user = creator.createPointOfEntryUser(rdp.region.getUuid(), rdp.district.getUuid(), rdp.pointOfEntry.getUuid());

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_travelentry_import_test.csv").toURI());
		TravelEntryImporterExtension importer = new TravelEntryImporterExtension(csvFile, false, user, ValueSeparator.DEFAULT);
		ImportResultStatus importResult = importer.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, getTravelEntryFacade().count(null));
	}

	@Test
	public void testImportAllTravelEntriesSeparatedWithTab()
		throws IOException, InvalidColumnException, InterruptedException, CsvValidationException, URISyntaxException {

		TestDataCreator tdc = new TestDataCreator();
		TestDataCreator.RDP rdp = tdc.createRDP();
		UserDto user = creator.createPointOfEntryUser(rdp.region.getUuid(), rdp.district.getUuid(), rdp.pointOfEntry.getUuid());

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_travelentry_import_test_tab.csv").toURI());
		TravelEntryImporterExtension importer = new TravelEntryImporterExtension(csvFile, false, user, ValueSeparator.TAB);
		ImportResultStatus importResult = importer.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, getTravelEntryFacade().count(null));
	}

	@Test
	public void testUserJurisdictionSetIfMissing()
		throws InterruptedException, InvalidColumnException, CsvValidationException, IOException, URISyntaxException {
		TestDataCreator tdc = new TestDataCreator();
		TestDataCreator.RDP rdp = tdc.createRDP();

		UserDto user = creator.createPointOfEntryUser(rdp.region.getUuid(), rdp.district.getUuid(), rdp.pointOfEntry.getUuid());

		loginWith(user);

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_travelentry_import_no_jurisdiction.csv").toURI());

		TravelEntryImporterExtension importer = new TravelEntryImporterExtension(csvFile, false, user, ValueSeparator.DEFAULT);
		ImportResultStatus importResult = importer.runImport();

		assertThat(importResult, is(ImportResultStatus.COMPLETED));

		List<TravelEntryDto> entries = getTravelEntryFacade().getAllAfter(new Date(0));
		assertThat(entries, hasSize(1));

		assertThat(entries.get(0).getPointOfEntry(), is(rdp.pointOfEntry.toReference()));
	}

	@Test
	public void testSetOtherPOEForGermanLocale()
		throws InterruptedException, InvalidColumnException, CsvValidationException, IOException, URISyntaxException {
		TestDataCreator tdc = new TestDataCreator();
		TestDataCreator.RDP rdp = tdc.createRDP();

		PointOfEntryDto otherPoe = PointOfEntryDto.build();
		otherPoe.setUuid(PointOfEntryDto.OTHER_POE_UUID);
		otherPoe.setName("Test Other POE");
		otherPoe.setPointOfEntryType(PointOfEntryType.OTHER);
		otherPoe.setRegion(rdp.region.toReference());
		otherPoe.setDistrict(rdp.district.toReference());
		getPointOfEntryFacade().save(otherPoe);

		UserDto user = creator.createUser(rdp.region.getUuid(), rdp.district.getUuid(), null, "James", "Smith", UserRole.SURVEILLANCE_OFFICER);

		loginWith(user);

		ImportResultStatus importResult;

		String serverLocale = MockProducer.getProperties().getProperty(ConfigFacadeEjb.COUNTRY_LOCALE);
		try {
			MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, "de");

			File csvFile = new File(getClass().getClassLoader().getResource("sormas_travelentry_import_no_poe.csv").toURI());

			TravelEntryImporterExtension importer = new TravelEntryImporterExtension(csvFile, false, user, ValueSeparator.DEFAULT);
			importResult = importer.runImport();
		} finally {
			// make sure server locale is reset
			if (serverLocale == null) {
				MockProducer.getProperties().remove(ConfigFacadeEjb.COUNTRY_LOCALE);
			} else {
				MockProducer.getProperties().setProperty(ConfigFacadeEjb.COUNTRY_LOCALE, serverLocale);
			}
		}

		assertThat(importResult, is(ImportResultStatus.COMPLETED));

		List<TravelEntryDto> entries = getTravelEntryFacade().getAllAfter(new Date(0));
		assertThat(entries, hasSize(1));

		assertThat(entries.get(0).getPointOfEntry(), is(otherPoe.toReference()));
		assertThat(entries.get(0).getPointOfEntryDetails(), is("[System] Automatically filled point of entry"));
	}

	@Test
	public void testImportFailedWithoutPOE()
		throws InterruptedException, InvalidColumnException, CsvValidationException, IOException, URISyntaxException {
		TestDataCreator tdc = new TestDataCreator();
		TestDataCreator.RDP rdp = tdc.createRDP();

		UserDto user = creator.createUser(rdp.region.getUuid(), rdp.district.getUuid(), null, "James", "Smith", UserRole.SURVEILLANCE_OFFICER);

		loginWith(user);

		File csvFile = new File(getClass().getClassLoader().getResource("sormas_travelentry_import_no_poe.csv").toURI());

		TravelEntryImporterExtension importer = new TravelEntryImporterExtension(csvFile, false, user, ValueSeparator.DEFAULT);
		ImportResultStatus importResult = importer.runImport();

		assertThat(importResult, is(ImportResultStatus.COMPLETED_WITH_ERRORS));
	}

	public static class TravelEntryImporterExtension extends TravelEntryImporter {

		public StringBuilder stringBuilder = new StringBuilder("");
		public StringBuilderWriter writer = new StringBuilderWriter(stringBuilder);

		public TravelEntryImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser, ValueSeparator separator)
			throws IOException {
			super(inputFile, hasEntityClassRow, currentUser, separator);
		}

		@Override
		protected Writer createErrorReportWriter() {
			return writer;
		}

		@Override
		protected Path getErrorReportFolderPath() {
			return Paths.get(System.getProperty("java.io.tmpdir"));
		}
	}

}
