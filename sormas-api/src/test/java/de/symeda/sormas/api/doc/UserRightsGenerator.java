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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.doc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

import de.symeda.sormas.api.user.UserRightsDocumentGenerator;

/**
 * Intentionally named *Generator because we don't want Maven to execute this class automatically.
 */
public class UserRightsGenerator {

	@Test
	public void generateUserRights() throws IOException {
		String filePath = "src/main/resources/doc/SORMAS_User_Rights.xlsx";
		try (OutputStream fileOut = new FileOutputStream(filePath)) {
			UserRightsDocumentGenerator.generateDefault(fileOut);
		}

//		Desktop.getDesktop().open(new File(filePath));
	}
}
