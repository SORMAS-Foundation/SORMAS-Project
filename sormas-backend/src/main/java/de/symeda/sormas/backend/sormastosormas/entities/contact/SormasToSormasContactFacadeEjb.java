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

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CONTACT_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.CONTACT_SYNC_ENDPOINT;
import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildContactValidationGroupName;

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

import de.symeda.sormas.api.EditPermissionType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.contact.SormasToSormasContactFacade;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.user.User;

@Stateless(name = "SormasToSormasContactFacade")
public class SormasToSormasContactFacadeEjb extends AbstractSormasToSormasInterface<Contact, ContactDto, SormasToSormasContactDto>
	implements SormasToSormasContactFacade {

	private static final String CONTACT_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CONTACT_REQUEST_ENDPOINT;
	private static final String CONTACT_REQUEST_GET_DATA_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CONTACT_REQUEST_GET_DATA_ENDPOINT;
	private static final String SAVE_SHARED_CONTACT_ENDPOINT = RESOURCE_PATH + CONTACT_ENDPOINT;
	public static final String SYNC_SHARED_CONTACT_ENDPOINT = RESOURCE_PATH + CONTACT_SYNC_ENDPOINT;
	public static final String CONTACT_SHARES_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CONTACT_SHARES_ENDPOINT;

	@EJB
	private ContactService contactService;
	@EJB
	private SampleService sampleService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private ImmunizationService immunizationService;

	public SormasToSormasContactFacadeEjb() {
		super(
			CONTACT_REQUEST_ENDPOINT,
			CONTACT_REQUEST_GET_DATA_ENDPOINT,
			SAVE_SHARED_CONTACT_ENDPOINT,
			SYNC_SHARED_CONTACT_ENDPOINT,
			CONTACT_SHARES_ENDPOINT,
			Captions.Contact,
			ShareRequestDataType.CONTACT);
	}

	@Override
	protected BaseAdoService<Contact> getEntityService() {
		return contactService;
	}

	@Override
	protected Class<SormasToSormasContactDto[]> getShareDataClass() {
		return SormasToSormasContactDto[].class;
	}

	@Override
	protected void validateEntitiesBeforeShare(List<Contact> entities, boolean handOverOwnership) throws SormasToSormasException {
		List<ValidationErrors> validationErrors = new ArrayList<>();
		for (Contact contact : entities) {
			if (!contactService.isContactEditAllowed(contact).equals(EditPermissionType.ALLOWED)) {
				validationErrors.add(
					new ValidationErrors(
						buildContactValidationGroupName(contact),
						ValidationErrors
							.create(new ValidationErrorGroup(Captions.Contact), new ValidationErrorMessage(Validations.sormasToSormasNotEditable))));
			}
			if (handOverOwnership && contact.getPerson().isEnrolledInExternalJournal()) {
				validationErrors.add(
					new ValidationErrors(
						buildContactValidationGroupName(contact),
						ValidationErrors.create(
							new ValidationErrorGroup(Captions.Contact),
							new ValidationErrorMessage(Validations.sormasToSormasPersonEnrolled))));
			}
		}

		if (validationErrors.size() > 0) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}
	}

	@Override
	protected void validateEntitiesBeforeShare(List<SormasToSormasShareInfo> shares) throws SormasToSormasException {
		validateEntitiesBeforeShare(
			shares.stream().map(SormasToSormasShareInfo::getContact).filter(Objects::nonNull).collect(Collectors.toList()),
			shares.get(0).isOwnershipHandedOver());
	}

	@Override
	protected List<SormasToSormasShareInfo> getOrCreateShareInfos(Contact contact, SormasToSormasOptionsDto options, User user, boolean forSync) {
		String organizationId = options.getOrganization().getId();
		SormasToSormasShareInfo eventShareInfo = contact.getSormasToSormasShares()
			.stream()
			.filter(s -> s.getOrganizationId().equals(organizationId))
			.findFirst()
			.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, contact, SormasToSormasShareInfo::setContact, options));

		Stream<SormasToSormasShareInfo> sampleShareInfos = Stream.empty();
		if (options.isWithSamples()) {
			sampleShareInfos = sampleService.findBy(new SampleCriteria().contact(contact.toReference()), user)
				.stream()
				.map(
					s -> s.getSormasToSormasShares()
						.stream()
						.filter(share -> share.getOrganizationId().equals(organizationId))
						.findFirst()
						.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, s, SormasToSormasShareInfo::setSample, options)));
		}

		Stream<SormasToSormasShareInfo> immunizationShareInfos = Stream.empty();
		if (options.isWithImmunizations()) {
			immunizationShareInfos = immunizationService.getByPersonIds(Collections.singletonList(contact.getPerson().getId()))
				.stream()
				.map(
					i -> i.getSormasToSormasShares()
						.stream()
						.filter(share -> share.getOrganizationId().equals(organizationId))
						.findFirst()
						.orElseGet(() -> ShareInfoHelper.createShareInfo(organizationId, i, SormasToSormasShareInfo::setImmunization, options)));
		}

		return Stream.of(Stream.of(eventShareInfo), sampleShareInfos, immunizationShareInfos)
			.flatMap(Function.identity())
			.collect(Collectors.toList());
	}

	@Override
	protected List<String> getUuidsWithPendingOwnershipHandedOver(List<Contact> entities) {
		return shareInfoService.getContactUuidsWithPendingOwnershipHandOver(entities);
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasContactFacadeEjbLocal extends SormasToSormasContactFacadeEjb {

	}
}
