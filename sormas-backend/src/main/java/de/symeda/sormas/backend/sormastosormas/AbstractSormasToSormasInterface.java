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

package de.symeda.sormas.backend.sormastosormas;

import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildValidationGroupName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.ejb.EJB;
import javax.inject.Inject;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityInterface;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SormasToSormasEntityDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

public abstract class AbstractSormasToSormasInterface<T extends AbstractDomainObject & SormasToSormasEntity, U extends EntityDto & SormasToSormasEntityDto, S extends SormasToSormasDto<U>, P extends ProcessedData<U>>
	implements SormasToSormasEntityInterface {

	@EJB
	private UserService userService;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasFacadeHelper sormasToSormasFacadeHelper;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;

	private final String saveEndpoint;
	private final String syncEndpoint;

	private final String entityCaptionTag;

	public AbstractSormasToSormasInterface() {
		throw new RuntimeException("AbstractSormasToSormasInterface should not be instantiated");
	}

	public AbstractSormasToSormasInterface(String saveEndpoint, String syncEndpoint, String entityCaptionTag) {
		this.saveEndpoint = saveEndpoint;
		this.syncEndpoint = syncEndpoint;
		this.entityCaptionTag = entityCaptionTag;
	}

	@Override
	public void shareEntities(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<T> entities = getEntityService().getByUuids(entityUuids);

		validateEntitiesBeforeSend(entities);

		List<S> entitiesToSend = new ArrayList<>();
		List<AssociatedEntityWrapper<?>> associatedEntities = new ArrayList<>();
		for (T entity : entities) {
			ShareData<S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

			entitiesToSend.add(shareData.getDto());

			associatedEntities.addAll(shareData.getAssociatedEntities());
		}

		sormasToSormasFacadeHelper.sendEntitiesToSormas(
			entitiesToSend,
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, saveEndpoint, authToken, encryptedData));

		entities.forEach(entity -> saveNewShareInfo(currentUser.toReference(), options, entity, this::setEntityShareInfoAssociatedObject));
		associatedEntities.forEach(wrapper -> {
			saveNewShareInfo(currentUser.toReference(), options, wrapper.getEntity(), (s, e) -> {
				wrapper.setShareInfoAssociatedObject(s);
			});
		});
	}

	@Override
	public void saveSharedEntities(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		S[] sharedEntities = sormasToSormasFacadeHelper.decryptSharedData(encryptedData, getShareDataClass());

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<P> dataToSave = new ArrayList<>(sharedEntities.length);

		for (S data : sharedEntities) {
			try {
				U entity = data.getEntity();

				ValidationErrors caseErrors = validateSharedEntity(entity);

				if (caseErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(entity), caseErrors);
				} else {
					P processedData = getSharedDataProcessor().processSharedData(data);

					processedData.getEntity().setSormasToSormasOriginInfo(processedData.getOriginInfo());

					dataToSave.add(processedData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (P processedData : dataToSave) {
			getProcessedDataPersister().persistSharedData(processedData);
		}
	}

	@Override
	public void returnEntity(String entityUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		options.setHandOverOwnership(true);
		User currentUser = userService.getCurrentUser();

		T entity = getEntityService().getByUuid(entityUuid);
		validateEntitiesBeforeSend(Collections.singletonList(entity));

		ShareData<S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

		sormasToSormasFacadeHelper.sendEntitiesToSormas(
			Collections.singletonList(shareData.getDto()),
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.put(host, saveEndpoint, authToken, encryptedData));

		entity.getSormasToSormasOriginInfo().setOwnershipHandedOver(false);
		originInfoService.persist(entity.getSormasToSormasOriginInfo());

		shareData.getAssociatedEntities().forEach(wrapper -> {
			if (wrapper.getEntity().getSormasToSormasOriginInfo() == null) {
				saveNewShareInfo(currentUser.toReference(), options, wrapper.getEntity(), (s, e) -> {
					wrapper.setShareInfoAssociatedObject(s);
				});
			}
		});
	}

	@Override
	public void saveReturnedEntity(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		S[] sharedEntities = sormasToSormasFacadeHelper.decryptSharedData(sharedData, getShareDataClass());

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<P> entitiesToSave = new ArrayList<>(sharedEntities.length);

		for (S sharedEntity : sharedEntities) {
			try {
				U entity = sharedEntity.getEntity();
				ValidationErrors entityErrors = validateExistingEntity(entity);
				if (entityErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(entity), entityErrors);
				} else {
					P processedEntity = getSharedDataProcessor().processSharedData(sharedEntity);
					entitiesToSave.add(processedEntity);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (P contactData : entitiesToSave) {
			getProcessedDataPersister().persistReturnedData(contactData, contactData.getOriginInfo());
		}
	}

	@Override
	public void syncEntity(String entityUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		T entity = getEntityService().getByUuid(entityUuid);

		validateEntitiesBeforeSend(Collections.singletonList(entity));

		ShareData<S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

		sormasToSormasFacadeHelper.sendEntitiesToSormas(
			Collections.singletonList(shareData.getDto()),
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, syncEndpoint, authToken, encryptedData));

		SormasToSormasShareInfo shareInfo = getShareInfoByEntityAndOrganization(entity.getUuid(), options.getOrganization().getUuid());
		updateShareInfoOptions(shareInfo, options);

		shareData.getAssociatedEntities().forEach(entityWrapper -> {
			SormasToSormasShareInfo sampleShareInfo = entityWrapper.getExistingShareInfo(shareInfoService, options.getOrganization().getUuid());
			if (sampleShareInfo == null) {
				saveNewShareInfo(
					currentUser.toReference(),
					options,
					entityWrapper.getEntity(),
					(i, ae) -> entityWrapper.setShareInfoAssociatedObject(i));
			} else {
				updateShareInfoOptions(sampleShareInfo, options);
			}
		});
	}

	@Override
	public void saveSyncedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		S[] sharedEntities = sormasToSormasFacadeHelper.decryptSharedData(encryptedData, getShareDataClass());

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<P> entitiesToSave = new ArrayList<>(sharedEntities.length);

		for (S sharedEntity : sharedEntities) {
			try {
				U entity = sharedEntity.getEntity();

				ValidationErrors contactErrors = validateExistingEntity(entity);
				if (contactErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(entity), contactErrors);
				} else {
					P processedContactData = getSharedDataProcessor().processSharedData(sharedEntity);
					entitiesToSave.add(processedContactData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (P contactData : entitiesToSave) {
			getProcessedDataPersister().persistSyncData(contactData);
		}
	}

	private String buildEntityValidationGroupName(U entity) {
		return buildValidationGroupName(entityCaptionTag, entity);
	}

	protected abstract BaseAdoService<T> getEntityService();

	protected abstract ShareDataBuilder<T, S> getShareDataBuilder();

	protected abstract SharedDataProcessor<U, S, P> getSharedDataProcessor();

	protected abstract ProcessedDataPersister<P> getProcessedDataPersister();

	protected abstract Class<S[]> getShareDataClass();

	protected abstract void validateEntitiesBeforeSend(List<T> entities) throws SormasToSormasException;

	protected abstract ValidationErrors validateSharedEntity(U entity);

	protected abstract ValidationErrors validateExistingEntity(U entity);

	protected abstract void setEntityShareInfoAssociatedObject(SormasToSormasShareInfo sormasToSormasShareInfo, T entity);

	protected abstract SormasToSormasShareInfo getShareInfoByEntityAndOrganization(String entityUuid, String organizationId);

	private <T> void saveNewShareInfo(
		UserReferenceDto sender,
		SormasToSormasOptionsDto options,
		T associatedObject,
		BiConsumer<SormasToSormasShareInfo, T> setAssociatedObject) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));
		shareInfo.setOrganizationId(options.getOrganization().getUuid());
		shareInfo.setSender(userService.getByReferenceDto(sender));

		addOptionsToShareInfo(options, shareInfo);

		setAssociatedObject.accept(shareInfo, associatedObject);

		shareInfoService.ensurePersisted(shareInfo);
	}

	private void addOptionsToShareInfo(SormasToSormasOptionsDto options, SormasToSormasShareInfo shareInfo) {
		shareInfo.setOwnershipHandedOver(options.isHandOverOwnership());
		shareInfo.setWithAssociatedContacts(options.isWithAssociatedContacts());
		shareInfo.setWithSamples(options.isWithSamples());
		shareInfo.setWithEventParticipants(options.isWithEventParticipants());
		shareInfo.setPseudonymizedPersonalData(options.isPseudonymizePersonalData());
		shareInfo.setPseudonymizedSensitiveData(options.isPseudonymizeSensitiveData());
		shareInfo.setComment(options.getComment());
	}

	private void updateShareInfoOptions(SormasToSormasShareInfo shareInfo, SormasToSormasOptionsDto options) {
		addOptionsToShareInfo(options, shareInfo);

		shareInfoService.ensurePersisted(shareInfo);
	}
}
