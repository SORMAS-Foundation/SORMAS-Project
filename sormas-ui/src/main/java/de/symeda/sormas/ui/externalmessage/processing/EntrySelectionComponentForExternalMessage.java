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

package de.symeda.sormas.ui.externalmessage.processing;

import java.util.List;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.labmessage.SampleReportDto;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.processing.EntrySelectionComponent;
import de.symeda.sormas.ui.utils.processing.EntrySelectionField;

public class EntrySelectionComponentForExternalMessage extends EntrySelectionComponent {

	private static final long serialVersionUID = 5315286409460459687L;

	private final ExternalMessageDto externalMessageDto;

	public EntrySelectionComponentForExternalMessage(ExternalMessageDto externalMessageDto, EntrySelectionField.Options selectableOptions) {
		super(selectableOptions, Strings.infoSelectOrCreateEntry, Strings.infoCreateEntry, ExternalMessageDto.I18N_PREFIX);
		this.externalMessageDto = externalMessageDto;

		initContent();
	}

	@Override
	protected void createAndAddSearchFieldComponents() {
		List<SampleReportDto> sampleReports = externalMessageDto.getSampleReports();
		SampleReportDto sampleReport = sampleReports != null ? sampleReports.get(0) : null;

		createAndAddSearchDetailLabel(externalMessageDto.getMessageDateTime(), ExternalMessageDto.MESSAGE_DATE_TIME);
		if (sampleReport != null) {
			createAndAddSearchDetailLabel(sampleReport.getSampleDateTime(), SampleReportDto.SAMPLE_DATE_TIME);
		}
		createAndAddSearchDetailLabel(externalMessageDto.getPersonFirstName(), ExternalMessageDto.PERSON_FIRST_NAME);
		createAndAddSearchDetailLabel(externalMessageDto.getPersonLastName(), ExternalMessageDto.PERSON_LAST_NAME);
		createAndAddSearchDetailLabel(externalMessageDto.getPersonBirthDateDD(), ExternalMessageDto.PERSON_BIRTH_DATE_DD);
		createAndAddSearchDetailLabel(externalMessageDto.getPersonBirthDateMM(), ExternalMessageDto.PERSON_BIRTH_DATE_MM);
		createAndAddSearchDetailLabel(externalMessageDto.getPersonBirthDateYYYY(), ExternalMessageDto.PERSON_BIRTH_DATE_YYYY);
		createAndAddSearchDetailLabel(externalMessageDto.getPersonSex(), ExternalMessageDto.PERSON_SEX);
	}
}
