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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;

import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.opencsv.exceptions.CsvValidationException;

import de.symeda.sormas.api.importexport.InvalidColumnException;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.ui.AbstractBeanTest;
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
		TravelEntryImporterExtension importer = new TravelEntryImporterExtension(csvFile, false, user);
		ImportResultStatus importResult = importer.runImport();

		assertEquals(ImportResultStatus.COMPLETED, importResult);
		assertEquals(1, getTravelEntryFacade().count(null));
	}

	public static class TravelEntryImporterExtension extends TravelEntryImporter {

		public StringBuilder stringBuilder = new StringBuilder("");
		public StringBuilderWriter writer = new StringBuilderWriter(stringBuilder);

		public TravelEntryImporterExtension(File inputFile, boolean hasEntityClassRow, UserDto currentUser) {
			super(inputFile, hasEntityClassRow, currentUser);
		}

		@Override
		protected Writer createErrorReportWriter() {
			return writer;
		}
	}

}
