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

package de.symeda.sormas.backend.sormastosormas.entities.contact;

import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.infrastructure.district.DistrictFacadeEjb;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

@Stateless
@LocalBean
public class ProcessedContactDataPersister extends ProcessedDataPersister<ContactDto, SormasToSormasContactDto, Contact> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	@EJB
	private DistrictFacadeEjb.DistrictFacadeEjbLocal districtFacade;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	public void persistSharedData(SormasToSormasContactDto processedData, Contact existingContact) throws SormasToSormasValidationException {
		persistProcessedData(processedData, existingContact == null);
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(ContactDto entity, String organizationId) {
		return shareInfoService.getByContactAndOrganization(entity.getUuid(), organizationId);
	}

	private void persistProcessedData(SormasToSormasContactDto processedData, boolean isCreate) throws SormasToSormasValidationException {

		ContactDto contact = processedData.getEntity();
		ValidationErrorGroup contactValidationGroupName = buildContactValidationGroupName(contact);

		if (isCreate) {
			// save person first during creation
			handleValidationError(
				() -> personFacade.savePerson(processedData.getPerson(), false, false, false),
				Captions.Person,
				contactValidationGroupName);

			if (contact.getSormasToSormasOriginInfo().isOwnershipHandedOver()) {
				setResponsibleDistrict(contact);
			}

			handleValidationError(() -> contactFacade.save(contact, true, true, false, false), Captions.Contact, contactValidationGroupName);
		} else {
			//save contact first during update
			handleValidationError(() -> contactFacade.save(contact, true, true, false, false), Captions.Contact, contactValidationGroupName);
			handleValidationError(
				() -> personFacade.savePerson(processedData.getPerson(), false, false, false),
				Captions.Person,
				contactValidationGroupName);
		}
	}

	private void setResponsibleDistrict(ContactDto contact) {
		String districtExternalId = configFacade.getS2SConfig().getDistrictExternalId();
		if (districtExternalId != null) {
			List<DistrictDto> districts = districtFacade.getByExternalId(districtExternalId, false);
			if (!districts.isEmpty()) {
				DistrictDto district = districts.get(0);

				contact.setRegion(district.getRegion());
				contact.setDistrict(district.toReference());
				contact.setCommunity(null);
			}
		}
	}
}
