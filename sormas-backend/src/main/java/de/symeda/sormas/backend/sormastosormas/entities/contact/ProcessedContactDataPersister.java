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

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.contact.SimilarContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.sormastosormas.entities.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.uuid.AbstractUuidDto;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntitiesHelper;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedContactDataPersister extends ProcessedDataPersister<ContactDto, SormasToSormasContactDto, Contact> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ContactService contactService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private CaseService caseService;
	@EJB
	private SormasToSormasEntitiesHelper sormasToSormasEntitiesHelper;

	@EJB
	private SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected SormasToSormasOriginInfoFacadeEjb getOriginInfoFacade() {
		return originInfoFacade;
	}

	@Override
	public void persistSharedData(SormasToSormasContactDto processedData, Contact existingContact, boolean isSync)
		throws SormasToSormasValidationException {
		persistProcessedData(processedData, existingContact == null, isSync);
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(ContactDto entity, String organizationId) {
		return shareInfoService.getByContactAndOrganization(entity.getUuid(), organizationId);
	}

	@Override
	public DuplicateResult checkForSimilarEntities(SormasToSormasContactDto processedData) {
		List<SimilarPersonDto> similarPersons = personFacade.getSimilarPersonDtos(PersonSimilarityCriteria.forPerson(processedData.getPerson()));
		if (similarPersons.isEmpty()) {
			return DuplicateResult.NONE;
		}

		List<SimilarContactDto> similarContacts = contactFacade.getMatchingContacts(
			new ContactSimilarityCriteria().withPersons(similarPersons.stream().map(SimilarPersonDto::toReference).collect(Collectors.toList()))
				.withCaze(processedData.getEntity().getCaze())
				.withDisease(processedData.getEntity().getDisease()));

		if (!similarContacts.isEmpty()) {
			boolean foundSimilarContactsConverted = contactService.exists(
				((cb, root, cq) -> cb.and(
					root.get(Contact.UUID).in(similarContacts.stream().map(AbstractUuidDto::getUuid).collect(Collectors.toList())),
					cb.isNotNull(root.get(Contact.RESULTING_CASE)))));

			return foundSimilarContactsConverted ? DuplicateResult.CONTACT_CONVERTED : DuplicateResult.CONTACT;
		}

		DistrictReferenceDto district =
			sormasToSormasEntitiesHelper.getS2SDistrictReference().map(DistrictDto::toReference).orElse(processedData.getEntity().getDistrict());
		if (district != null) {
			boolean foundSimilarCases = caseService.hasSimilarCases(
				new CaseSimilarityCriteria().caseCriteria(new CaseCriteria().disease(processedData.getEntity().getDisease()).district(district))
					.personUuids(similarPersons.stream().map(SimilarPersonDto::getUuid).collect(Collectors.toList())));

			return foundSimilarCases ? DuplicateResult.CASE_TO_CONTACT : DuplicateResult.PERSON_ONLY;
		}

		return DuplicateResult.PERSON_ONLY;
	}

	private void persistProcessedData(SormasToSormasContactDto processedData, boolean isCreate, boolean isSync)
		throws SormasToSormasValidationException {
		ContactDto contact = processedData.getEntity();
		ValidationErrorGroup contactValidationGroupName = buildContactValidationGroupName(contact);

		final PersonDto person = processedData.getPerson();
		if (isCreate) {
			// save person first during creation
			handleValidationError(() -> personFacade.save(person, false, false, false), Captions.Person, contactValidationGroupName, person);

			handleValidationError(() -> contactFacade.save(contact, true, true, false, false), Captions.Contact, contactValidationGroupName, contact);
		} else {
			//save contact first during update
			handleValidationError(() -> contactFacade.save(contact, true, true, false, false), Captions.Contact, contactValidationGroupName, contact);

			// #10544 only persons not owned should be updated
			if (!(isSync && personFacade.isEditAllowed(person.getUuid()))) {
				handleValidationError(() -> personFacade.save(person, false, false, false), Captions.Person, contactValidationGroupName, contact);
			}
		}
	}
}
