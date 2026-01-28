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

package de.symeda.sormas.backend.sormastosormas.share;

import java.lang.reflect.Field;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.entities.externalmessage.SormasToSormasExternalMessageDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasPersonPreview;
import de.symeda.sormas.api.utils.fieldaccess.checkers.PersonalDataFieldAccessChecker;
import de.symeda.sormas.api.utils.fieldaccess.checkers.SensitiveDataFieldAccessChecker;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.ContactFacadeEjb;
import de.symeda.sormas.backend.externalmessage.ExternalMessage;
import de.symeda.sormas.backend.externalmessage.ExternalMessageFacadeEjb;
import de.symeda.sormas.backend.location.LocationFacadeEjb;
import de.symeda.sormas.backend.person.Person;
import de.symeda.sormas.backend.person.PersonFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.util.Pseudonymizer;

@Stateless
@LocalBean
public class ShareDataBuilderHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShareDataBuilderHelper.class);

	@EJB
	private PersonFacadeEjb.PersonFacadeEjbLocal personFacade;
	@EJB
	private ContactFacadeEjb.ContactFacadeEjbLocal contactFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;
	@EJB
	private ExternalMessageFacadeEjb.ExternalMessageFacadeEjbLocal externalMessageFacade;

	public SormasToSormasPseudonymizer createPseudonymizer(ShareRequestInfo requestInfo) {
		Pseudonymizer<?> pseudonymizer = Pseudonymizer.getDefaultNoCheckers(false);

		if (requestInfo.isPseudonymizedPersonalData()) {
			pseudonymizer.addFieldAccessChecker(PersonalDataFieldAccessChecker.forcedNoAccess(), PersonalDataFieldAccessChecker.forcedNoAccess());
		}
		if (requestInfo.isPseudonymizedSensitiveData()) {
			pseudonymizer.addFieldAccessChecker(SensitiveDataFieldAccessChecker.forcedNoAccess(), SensitiveDataFieldAccessChecker.forcedNoAccess());
		}

		return new SormasToSormasPseudonymizer(pseudonymizer);
	}

	public PersonDto getPersonDto(Person person, SormasToSormasPseudonymizer pseudonymizer, ShareRequestInfo requestInfo) {
		PersonDto personDto = personFacade.toPseudonymizedDto(person, pseudonymizer.getPseudonymizer(), true);

		pseudonymizePerson(personDto, requestInfo);
		clearIgnoredProperties(personDto);

		return personDto;
	}

	public void pseudonymizePerson(PersonDto personDto, ShareRequestInfo requestInfo) {
		if (requestInfo.isPseudonymizedPersonalData() || requestInfo.isPseudonymizedSensitiveData()) {
			personDto.setFirstName(I18nProperties.getCaption(Captions.inaccessibleValue));
			personDto.setLastName(I18nProperties.getCaption(Captions.inaccessibleValue));
		}
	}

	public SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(User user, ShareRequestInfo requestInfo) {
		return createSormasToSormasOriginInfo(user, createOptionsFormShareRequestInfo(requestInfo));
	}

	public SormasToSormasOriginInfoDto createSormasToSormasOriginInfo(User user, @Valid SormasToSormasOptionsDto options) {
		SormasToSormasOriginInfoDto sormasToSormasOriginInfo = new SormasToSormasOriginInfoDto();
		sormasToSormasOriginInfo.setOrganizationId(configFacadeEjb.getS2SConfig().getId());
		sormasToSormasOriginInfo.setSenderName(String.format("%s %s", user.getFirstName(), user.getLastName()));
		sormasToSormasOriginInfo.setSenderEmail(user.getUserEmail());
		sormasToSormasOriginInfo.setSenderPhoneNumber(user.getPhone());
		sormasToSormasOriginInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		sormasToSormasOriginInfo.setWithAssociatedContacts(options.isWithAssociatedContacts());
		sormasToSormasOriginInfo.setWithSamples(options.isWithSamples());
		sormasToSormasOriginInfo.setWithEventParticipants(options.isWithEventParticipants());
		sormasToSormasOriginInfo.setWithImmunizations(options.isWithImmunizations());
		sormasToSormasOriginInfo.setWithSurveillanceReports(options.isWithSurveillanceReports());
		sormasToSormasOriginInfo.setComment(options.getComment());
		sormasToSormasOriginInfo.setPseudonymizedData(options.isPseudonymizeData());

		return sormasToSormasOriginInfo;
	}

	public SormasToSormasPersonPreview getPersonPreview(Person person) {
		SormasToSormasPersonPreview personPreview = new SormasToSormasPersonPreview();

		personPreview.setFirstName(person.getFirstName());
		personPreview.setLastName(person.getLastName());
		personPreview.setBirthdateDD(person.getBirthdateDD());
		personPreview.setBirthdateMM(person.getBirthdateMM());
		personPreview.setBirthdateYYYY(person.getBirthdateYYYY());
		personPreview.setSex(person.getSex());
		personPreview.setAddress(LocationFacadeEjb.toDto(person.getAddress()));

		return personPreview;
	}

	public SormasToSormasOptionsDto createOptionsFormShareRequestInfo(ShareRequestInfo requestInfo) {
		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();

		options.setOrganization(new SormasServerDescriptor(requestInfo.getShares().get(0).getOrganizationId()));
		options.setHandOverOwnership(requestInfo.isOwnershipHandedOver());
		options.setWithAssociatedContacts(requestInfo.isWithAssociatedContacts());
		options.setWithSamples(requestInfo.isWithSamples());
		options.setWithEventParticipants(requestInfo.isWithEventParticipants());
		options.setWithImmunizations(requestInfo.isWithImmunizations());
		options.setWithSurveillanceReports(requestInfo.isWithSurveillanceReports());
		options.setComment(requestInfo.getComment());
		options.setPseudonymizeData(requestInfo.isPseudonymizedPersonalData() || requestInfo.isPseudonymizedSensitiveData());

		return options;

	}

	public SormasToSormasOptionsDto createOptionsFromOriginInfoDto(SormasToSormasOriginInfo originInfo) {
		SormasToSormasOptionsDto options = new SormasToSormasOptionsDto();

		options.setOrganization(new SormasServerDescriptor(originInfo.getOrganizationId()));
		options.setHandOverOwnership(originInfo.isOwnershipHandedOver());
		options.setWithAssociatedContacts(originInfo.isWithAssociatedContacts());
		options.setWithSamples(originInfo.isWithSamples());
		options.setWithEventParticipants(originInfo.isWithEventParticipants());
		options.setWithImmunizations(originInfo.isWithImmunizations());
		options.setWithSurveillanceReports(originInfo.isWithSurveillanceReports());
		options.setComment(originInfo.getComment());

		return options;

	}

	public <T> void clearIgnoredProperties(T dto) {
		SormasToSormasConfig s2SConfig = configFacadeEjb.getS2SConfig();
		Class<?> dtoType = dto.getClass();
		for (Field field : dtoType.getDeclaredFields()) {
			if (field.isAnnotationPresent(S2SIgnoreProperty.class)) {
				String s2sConfigProperty = field.getAnnotation(S2SIgnoreProperty.class).configProperty();
				if (s2SConfig.getIgnoreProperties().get(s2sConfigProperty)) {
					field.setAccessible(true);
					try {
						field.set(dto, null);
					} catch (IllegalAccessException e) {
						LOGGER.error("Could not clear field {} for {}", field.getName(), dtoType.getSimpleName());
					}
					field.setAccessible(false);
				}
			}
		}
	}

	public SormasToSormasExternalMessageDto getExternalMessageDto(ExternalMessage externalMessage, ShareRequestInfo requestInfo) {
		ExternalMessageDto externalMessageDto = externalMessageFacade.toDto(externalMessage);
		externalMessageDto.setAssignee(null);

		if (!requestInfo.isWithSurveillanceReports()) {
			externalMessageDto.setSurveillanceReport(null);
		}

		return new SormasToSormasExternalMessageDto(externalMessageDto);
	}
}
