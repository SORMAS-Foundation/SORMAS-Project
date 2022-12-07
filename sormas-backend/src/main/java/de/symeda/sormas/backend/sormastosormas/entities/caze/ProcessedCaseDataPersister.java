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

package de.symeda.sormas.backend.sormastosormas.entities.caze;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.handleValidationError;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.JoinType;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.caze.CaseSimilarityCriteria;
import de.symeda.sormas.api.contact.ContactSimilarityCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonSimilarityCriteria;
import de.symeda.sormas.api.person.SimilarPersonDto;
import de.symeda.sormas.api.sormastosormas.entities.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.uuid.AbstractUuidDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.contact.ContactFacadeEjb.ContactFacadeEjbLocal;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntitiesHelper;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedCaseDataPersister extends ProcessedDataPersister<CaseDataDto, SormasToSormasCaseDto, Case> {

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private CaseFacadeEjb.CaseFacadeEjbLocal caseFacade;
	@EJB
	private CaseService caseService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;
	@EJB
	private ContactFacadeEjbLocal contactFacade;
	@EJB
	private SormasToSormasEntitiesHelper sormasToSormasEntitiesHelper;

	@Override
	protected SormasToSormasShareInfoService getShareInfoService() {
		return shareInfoService;
	}

	@Override
	protected SormasToSormasOriginInfoFacadeEjb getOriginInfoFacade() {
		return originInfoFacade;
	}

	@Override
	protected void persistSharedData(SormasToSormasCaseDto processedData, Case existingEntity, boolean isSync)
		throws SormasToSormasValidationException {
		persistProcessedData(processedData, existingEntity == null, isSync);
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(CaseDataDto entity, String organizationId) {
		return shareInfoService.getByCaseAndOrganization(entity.getUuid(), organizationId);
	}

	@Override
	public DuplicateResult checkForSimilarEntities(SormasToSormasCaseDto processedData) {
		List<SimilarPersonDto> similarPersons = personFacade.getSimilarPersonDtos(PersonSimilarityCriteria.forPerson(processedData.getPerson()));
		if (similarPersons.isEmpty()) {
			return DuplicateResult.NONE;
		}

		List<CaseSelectionDto> similarCases = caseService.getSimilarCases(
			CaseSimilarityCriteria
				.forCase(processedData.getEntity(), similarPersons.stream().map(AbstractUuidDto::getUuid).collect(Collectors.toList())));

		if (!similarCases.isEmpty()) {
			boolean foundCasesConverted = caseService.exists(
				((cb, root, cq) -> cb.and(
					root.get(Case.UUID).in(similarCases.stream().map(AbstractUuidDto::getUuid).collect(Collectors.toList())),
					cb.isNotNull(root.join(Case.CONVERTED_FROM_CONTACT, JoinType.LEFT)))));

			return foundCasesConverted ? DuplicateResult.CASE_CONVERTED : DuplicateResult.CASE;
		}

		DistrictReferenceDto district = sormasToSormasEntitiesHelper.getS2SDistrictReference()
			.map(DistrictDto::toReference)
			.orElse(processedData.getEntity().getResponsibleDistrict());

		boolean foundSimilarContacts = contactFacade.hasSimilarContacts(
			new ContactSimilarityCriteria().withPersons(similarPersons.stream().map(SimilarPersonDto::toReference).collect(Collectors.toList()))
				.withNoResultingCase(true)
				.withDistrict(district)
				.withDisease(processedData.getEntity().getDisease()));

		return foundSimilarContacts ? DuplicateResult.CONTACT_TO_CASE : DuplicateResult.PERSON_ONLY;
	}

	private void persistProcessedData(SormasToSormasCaseDto caseData, boolean isCreate, boolean isSync) throws SormasToSormasValidationException {
		CaseDataDto caze = caseData.getEntity();

		final PersonDto person = caseData.getPerson();
		if (isCreate) {
			// save person first during creation
			handleValidationError(() -> personFacade.save(person, false, false, false), Captions.Person, buildCaseValidationGroupName(caze), person);

			handleValidationError(
				() -> caseFacade.save(caze, true, false, false, false),
				Captions.CaseData,
				buildCaseValidationGroupName(caze),
				caze);
		} else {
			//save case first during update

			handleValidationError(
				() -> caseFacade.save(caze, true, false, false, false),
				Captions.CaseData,
				buildCaseValidationGroupName(caze),
				caze);

			// #10544 only persons not owned should be updated
			if (!(isSync && personFacade.isEditAllowed(person.getUuid()))) {
				handleValidationError(
					() -> personFacade.save(person, false, false, false),
					Captions.Person,
					buildCaseValidationGroupName(caze),
					person);
			}
		}
	}
}
