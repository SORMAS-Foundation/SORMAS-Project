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

package de.symeda.sormas.backend.sormastosormas.data.received;

import de.symeda.sormas.api.sormastosormas.S2SIgnoreProperty;
import de.symeda.sormas.api.sormastosormas.SormasToSormasConfig;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.AdoServiceWithUserFilter;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.data.validation.SormasToSormasDtoValidator;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public abstract class ReceivedDataProcessor<ADO extends AbstractDomainObject, DTO extends SormasToSormasShareableDto, SHARED extends SormasToSormasEntityDto<DTO>, PREVIEW extends PseudonymizableDto, ENTITY extends SormasToSormasShareable, SRV extends AdoServiceWithUserFilter<ADO>, VALIDATOR extends SormasToSormasDtoValidator<DTO, SHARED, PREVIEW>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected SRV service;
	protected UserService userService;
	protected ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade;
	protected VALIDATOR validator;

	protected ReceivedDataProcessor() {

	}

	protected ReceivedDataProcessor(SRV service, UserService userService, ConfigFacadeEjb.ConfigFacadeEjbLocal configFacade, VALIDATOR validator) {
		this.service = service;
		this.userService = userService;
		this.configFacade = configFacade;
		this.validator = validator;
	}

	public ValidationErrors processReceivedData(SHARED sharedData, ENTITY existingData, SormasToSormasOriginInfoDto originInfo) {
		ValidationErrors uuidError = existsNotShared(sharedData.getEntity().getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}

		handleReceivedData(sharedData, existingData, originInfo);
		return validator.validateIncoming(sharedData);
	}

	public abstract void handleReceivedData(SHARED sharedData, ENTITY existingData, SormasToSormasOriginInfoDto originInfo);

	public ValidationErrors processReceivedPreview(PREVIEW sharedPreview) {
		ValidationErrors uuidError = existsNotShared(sharedPreview.getUuid());
		if (uuidError.hasError()) {
			return uuidError;
		}
		return validator.validateIncomingPreview(sharedPreview);
	}

	public ValidationErrors existsNotShared(
		String uuid,
		String sormasToSormasOriginInfo,
		String sormasToSormasShares,
		String groupNameTag,
		String errorMessage) {
		ValidationErrors errors = new ValidationErrors();
		if (service.exists(
			(cb, eventRoot, cq) -> cb.and(
				cb.equal(eventRoot.get(AbstractDomainObject.UUID), uuid),
				cb.isNull(eventRoot.get(sormasToSormasOriginInfo)),
				cb.isEmpty(eventRoot.get(sormasToSormasShares))))) {
			errors.add(new ValidationErrorGroup(groupNameTag), new ValidationErrorMessage(errorMessage));
		}
		return errors;
	}

	public abstract ValidationErrors existsNotShared(String uuid);

	protected void updateReportingUser(SormasToSormasShareableDto entity, SormasToSormasShareable originalEntiy) {
		UserReferenceDto reportingUser =
			originalEntiy == null ? userService.getCurrentUser().toReference() : originalEntiy.getReportingUser().toReference();
		entity.setReportingUser(reportingUser);
	}

	protected <T> void handleIgnoredProperties(T receivedEntity, T originalEntity) {
		Class<?> dtoType = receivedEntity.getClass();
		SormasToSormasConfig s2SConfig = configFacade.getS2SConfig();
		for (Field field : dtoType.getDeclaredFields()) {
			if (field.isAnnotationPresent(S2SIgnoreProperty.class)) {
				String s2sConfigProperty = field.getAnnotation(S2SIgnoreProperty.class).configProperty();
				if (s2SConfig.getIgnoreProperties().get(s2sConfigProperty)) {
					field.setAccessible(true);
					try {
						Object originalValue = originalEntity != null ? field.get(originalEntity) : null;
						field.set(receivedEntity, originalValue);
					} catch (IllegalAccessException e) {
						logger.error("Could not set field {} for {}", field.getName(), dtoType.getSimpleName());
					}
					field.setAccessible(false);
				}
			}
		}
	}
}
