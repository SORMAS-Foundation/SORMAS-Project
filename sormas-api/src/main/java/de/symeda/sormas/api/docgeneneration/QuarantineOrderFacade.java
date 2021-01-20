/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.docgeneneration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.ejb.Remote;

import de.symeda.sormas.api.ReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestReferenceDto;
import de.symeda.sormas.api.sample.SampleReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;

@Remote
public interface QuarantineOrderFacade {

	byte[] getGeneratedDocument(
		String templateName,
		ReferenceDto rootEntityReference,
		UserReferenceDto userReference,
		SampleReferenceDto sampleReference,
		PathogenTestReferenceDto pathogenTest,
		Properties extraProperties)
		throws IOException;

	List<String> getAvailableTemplates(ReferenceDto referenceDto);

	DocumentVariables getDocumentVariables(ReferenceDto referenceDto, String templateName) throws IOException;
}
