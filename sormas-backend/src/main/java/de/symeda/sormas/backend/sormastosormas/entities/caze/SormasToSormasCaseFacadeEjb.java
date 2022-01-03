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

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CASE_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CASE_SYNC_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildCaseValidationGroupName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactCriteria;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseFacade;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.user.User;

@Stateless(name = "SormasToSormasCaseFacade")
public class SormasToSormasCaseFacadeEjb extends AbstractSormasToSormasInterface<Case, CaseDataDto, SormasToSormasCaseDto>
	implements SormasToSormasCaseFacade {

	public static final String CASE_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_REQUEST_ENDPOINT;
	public static final String CASE_REQUEST_GET_DATA_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_REQUEST_GET_DATA_ENDPOINT;
	public static final String SAVE_SHARED_CASE_ENDPOINT = RESOURCE_PATH + CASE_ENDPOINT;
	public static final String SYNC_CASE_ENDPOINT = RESOURCE_PATH + CASE_SYNC_ENDPOINT;
	public static final String CASE_SHARES_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_SHARES_ENDPOINT;

	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	public SormasToSormasCaseFacadeEjb() {
		super(
			CASE_REQUEST_ENDPOINT,
			CASE_REQUEST_GET_DATA_ENDPOINT,
			SAVE_SHARED_CASE_ENDPOINT,
			SYNC_CASE_ENDPOINT,
			CASE_SHARES_ENDPOINT,
			Captions.CaseData,
			ShareRequestDataType.CASE);
	}

	@Override
	protected BaseAdoService<Case> getEntityService() {
		return caseService;
	}

	@Override
	protected Class<SormasToSormasCaseDto[]> getShareDataClass() {
		return SormasToSormasCaseDto[].class;
	}

	@Override
	protected void validateEntitiesBeforeShare(List<Case> entities, boolean handOverOwnership) throws SormasToSormasException {
		List<ValidationErrors> validationErrors = new ArrayList<>();
		for (Case caze : entities) {
			if (!caseService.isCaseEditAllowed(caze)) {
				validationErrors.add(
					new ValidationErrors(
						buildCaseValidationGroupName(caze),
						ValidationErrors
							.create(new ValidationErrorGroup(Captions.CaseData), new ValidationErrorMessage(Validations.sormasToSormasNotEditable))));
			}
			if (handOverOwnership && caze.getPerson().isEnrolledInExternalJournal()) {
				validationErrors.add(
					new ValidationErrors(
						buildCaseValidationGroupName(caze),
						ValidationErrors.create(
							new ValidationErrorGroup(Captions.CaseData),
							new ValidationErrorMessage(Validations.sormasToSormasPersonEnrolled))));
			}
		}

		if (!validationErrors.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}
	}

	@Override
	protected void validateEntitiesBeforeShare(List<SormasToSormasShareInfo> shares) throws SormasToSormasException {
		validateEntitiesBeforeShare(
			shares.stream().map(SormasToSormasShareInfo::getCaze).filter(Objects::nonNull).collect(Collectors.toList()),
			shares.get(0).isOwnershipHandedOver());
	}

	@Override
	protected List<SormasToSormasShareInfo> getOrCreateShareInfos(Case caze, SormasToSormasOptionsDto options, User user, boolean forSync) {
		String organizationId = options.getOrganization().getId();
		SormasToSormasShareInfo cazeShareInfo = caze.getSormasToSormasShares()
			.stream()
			.filter(s -> s.getOrganizationId().equals(organizationId))
			.findFirst()
			.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, caze, SormasToSormasShareInfo::setCaze, options));

		Stream<SormasToSormasShareInfo> contactShareInfos = Stream.empty();
		List<Contact> contacts = Collections.emptyList();
		if (options.isWithAssociatedContacts()) {
			contacts = contactService.findBy(new ContactCriteria().caze(caze.toReference()), user);

			contactShareInfos = contacts.stream()
				.map(c -> c.getSormasToSormasShares().stream().filter(s -> s.getOrganizationId().equals(organizationId)).findFirst().orElseGet(() -> {
					if (forSync) {
						// do not share new contacts on sync
						return c.getSormasToSormasOriginInfo() != null && c.getSormasToSormasOriginInfo().getOrganizationId().equals(organizationId)
							? ShareInfoHelper.createShareInfo(organizationId, c, SormasToSormasShareInfo::setContact, options)
							: null;
					} else {
						return ShareInfoHelper.createShareInfo(organizationId, c, SormasToSormasShareInfo::setContact, options);
					}
				}))
				.filter(Objects::nonNull);
		}

		Stream<SormasToSormasShareInfo> sampleShareInfos = Stream.empty();
		if (options.isWithSamples()) {
			sampleShareInfos = getAssociatedSamples(caze.toReference(), contacts, user).stream()
				.map(
					s -> s.getSormasToSormasShares()
						.stream()
						.filter(share -> share.getOrganizationId().equals(organizationId))
						.findFirst()
						.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, s, SormasToSormasShareInfo::setSample, options)));
		}

		Stream<SormasToSormasShareInfo> immunizationShareInfos = Stream.empty();
		if (options.isWithImmunizations()) {
			immunizationShareInfos = getAssociatedImmunizations(caze, contacts).stream()
				.map(
					i -> i.getSormasToSormasShares()
						.stream()
						.filter(share -> share.getOrganizationId().equals(organizationId))
						.findFirst()
						.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, i, SormasToSormasShareInfo::setImmunization, options)));
		}

		return Stream.of(Stream.of(cazeShareInfo), contactShareInfos, sampleShareInfos, immunizationShareInfos)
			.flatMap(Function.identity())
			.collect(Collectors.toList());
	}

	@Override
	protected List<String> getUuidsWithPendingOwnershipHandedOver(List<Case> entities) {
		return shareInfoService.getCaseUuidsWithPendingOwnershipHandOver(entities);
	}

	private List<Sample> getAssociatedSamples(CaseReferenceDto caseReferenceDto, List<Contact> associatedContacts, User user) {
		List<Sample> samples;
		final List<Sample> caseSamples = sampleService.findBy(new SampleCriteria().caze(caseReferenceDto), user);
		samples = new ArrayList<>(caseSamples);

		for (Contact associatedContact : associatedContacts) {
			List<Sample> contactSamples = sampleService.findBy(new SampleCriteria().contact(associatedContact.toReference()), user)
				.stream()
				.filter(contactSample -> caseSamples.stream().noneMatch(caseSample -> DataHelper.isSame(caseSample, contactSample)))
				.collect(Collectors.toList());

			samples.addAll(contactSamples);
		}

		return samples;
	}

	private List<Immunization> getAssociatedImmunizations(Case caze, List<Contact> associatedContacts) {
		final List<Immunization> caseImmunizations = immunizationService.getByPersonIds(Collections.singletonList(caze.getPerson().getId()));
		List<Immunization> immunizations = new ArrayList<>(caseImmunizations);

		if (!associatedContacts.isEmpty()) {
			List<Long> contactPersonIds = associatedContacts.stream().map(Contact::getId).collect(Collectors.toList());
			List<Immunization> contactImmunizations = immunizationService.getByPersonIds(contactPersonIds)
				.stream()
				.filter(
					contactImmunization -> caseImmunizations.stream()
						.noneMatch(caseImmunization -> DataHelper.isSame(caseImmunization, contactImmunization)))
				.collect(Collectors.toList());

			immunizations.addAll(contactImmunizations);
		}

		return immunizations;
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasCaseFacadeEjbLocal extends SormasToSormasCaseFacadeEjb {

	}
}
