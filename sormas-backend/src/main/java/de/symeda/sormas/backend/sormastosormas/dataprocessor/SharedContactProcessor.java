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

package de.symeda.sormas.backend.sormastosormas.dataprocessor;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.backend.sormastosormas.ProcessedContactData;

@Stateless
@LocalBean
public class SharedContactProcessor
//		implements SharedDataProcessor<SormasToSormasContactDto, ProcessedContactData>
{

	@EJB
	private SharedDataProcessorHelper dataProcessorHelper;

	public ProcessedContactData processSharedData(SormasToSormasContactDto sharedContact) throws SormasToSormasValidationException {
		Map<String, ValidationErrors> validationErrors = new HashMap<>();

		PersonDto person = sharedContact.getPerson();
		ContactDto contact = sharedContact.getContact();
		List<SormasToSormasSampleDto> samples = sharedContact.getSamples();
		SormasToSormasOriginInfoDto originInfo = sharedContact.getOriginInfo();

		ValidationErrors contactValidationErrors = new ValidationErrors();

		ValidationErrors originInfoErrors = dataProcessorHelper.processOriginInfo(originInfo, Captions.Contact);
		contactValidationErrors.addAll(originInfoErrors);

		ValidationErrors contactDataErrors = dataProcessorHelper.processContactData(contact, person);
		contactValidationErrors.addAll(contactDataErrors);

		if (contactValidationErrors.hasError()) {
			validationErrors.put(buildContactValidationGroupName(contact), contactValidationErrors);
		}

		if (samples != null) {
			Map<String, ValidationErrors> sampleErrors = dataProcessorHelper.processSamples(samples);
			validationErrors.putAll(sampleErrors);
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return new ProcessedContactData(person, contact, samples, originInfo);
	}
}
