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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityInterface;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareStatus;
import de.symeda.sormas.api.sormastosormas.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SormasToSormasEntityDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.sormastosormas.sharerequest.SormasToSormasShareRequestFacadeEJB.SormasToSormasShareRequestFacadeEJBLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

public abstract class AbstractSormasToSormasInterface<ADO extends AbstractDomainObject & SormasToSormasEntity, DTO extends EntityDto & SormasToSormasEntityDto, S extends SormasToSormasDto<DTO>, PREVIEW extends HasUuid, PROCESSED extends ProcessedData<DTO>>
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
	@EJB
	private SormasToSormasShareRequestFacadeEJBLocal shareRequestFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;
	@EJB
	private SharedDataProcessorHelper dataProcessorHelper;

	private final String requestEndpoint;
	private final String saveEndpoint;
	private final String syncEndpoint;

	private final String entityCaptionTag;

	private final ShareRequestDataType shareRequestDataType;

	public AbstractSormasToSormasInterface() {
		throw new RuntimeException("AbstractSormasToSormasInterface should not be instantiated");
	}

	public AbstractSormasToSormasInterface(
		String requestEndpoint,
		String saveEndpoint,
		String syncEndpoint,
		String entityCaptionTag,
		ShareRequestDataType shareRequestDataType) {
		this.requestEndpoint = requestEndpoint;
		this.saveEndpoint = saveEndpoint;
		this.syncEndpoint = syncEndpoint;
		this.entityCaptionTag = entityCaptionTag;
		this.shareRequestDataType = shareRequestDataType;
	}

	public void sendShareRequest(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<ADO> entities = getEntityService().getByUuids(entityUuids);

		validateEntitiesBeforeSend(entities);

		List<PREVIEW> previewsToSend = new ArrayList<>();
		List<AssociatedEntityWrapper<?>> associatedEntities = new ArrayList<>();
		for (ADO entity : entities) {
			ShareData<PREVIEW> shareData = getShareDataBuilder().buildShareDataPreview(entity, currentUser, options);

			previewsToSend.add(shareData.getDto());

			associatedEntities.addAll(shareData.getAssociatedEntities());
		}

		SormasToSormasOriginInfoDto originInfo = dataBuilderHelper.createSormasToSormasOriginInfo(currentUser, options);

		sormasToSormasFacadeHelper.sendEntitiesToSormas(
			new ShareDataPreview<>(previewsToSend, originInfo),
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, requestEndpoint, authToken, encryptedData));

		entities.forEach(
			entity -> saveNewShareInfo(
				currentUser.toReference(),
				options,
				SormasToSormasShareStatus.PENDING,
				entity,
				this::setEntityShareInfoAssociatedObject));
		associatedEntities.forEach(wrapper -> {
			saveNewShareInfo(currentUser.toReference(), options, SormasToSormasShareStatus.PENDING, wrapper.getEntity(), (s, e) -> {
				wrapper.setShareInfoAssociatedObject(s);
			});
		});
	}

	@Override
	public void saveShareRequest(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		ShareDataPreview<PREVIEW> shareData = sormasToSormasFacadeHelper.decryptSharedData(encryptedData, ShareDataPreview.class);

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<PREVIEW> previews = shareData.getPreviews();
		List<PREVIEW> previewsToSave = new ArrayList<>(previews.size());

		SormasToSormasOriginInfoDto originInfo = shareData.getOriginInfo();
		ValidationErrors originInfoErrors = dataProcessorHelper.processOriginInfo(originInfo, entityCaptionTag);
		if (originInfoErrors.hasError()) {
			validationErrors.put(I18nProperties.getCaption(entityCaptionTag), originInfoErrors);
		}

		for (PREVIEW preview : previews) {
			try {
				ValidationErrors caseErrors = validateSharedPreview(preview);

				if (caseErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(preview), caseErrors);
				} else {
					PREVIEW processedData = getSharedDataProcessor().processSharedPreview(preview);
					previewsToSave.add(processedData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		shareRequestFacade.saveShareRequest(createNewShareRequest(ShareRequestStatus.PENDING, originInfo, previewsToSave));
	}

	@Override
	public void shareEntities(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<ADO> entities = getEntityService().getByUuids(entityUuids);

		validateEntitiesBeforeSend(entities);

		List<S> entitiesToSend = new ArrayList<>();
		List<AssociatedEntityWrapper<?>> associatedEntities = new ArrayList<>();
		for (ADO entity : entities) {
			ShareData<S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

			entitiesToSend.add(shareData.getDto());

			associatedEntities.addAll(shareData.getAssociatedEntities());
		}

		sormasToSormasFacadeHelper.sendEntitiesToSormas(
			entitiesToSend,
			options,
			(host, authToken, encryptedData) -> sormasToSormasRestClient.post(host, saveEndpoint, authToken, encryptedData));

		entities.forEach(
			entity -> saveNewShareInfo(
				currentUser.toReference(),
				options,
				SormasToSormasShareStatus.ACCEPTED,
				entity,
				this::setEntityShareInfoAssociatedObject));
		associatedEntities.forEach(wrapper -> {
			saveNewShareInfo(currentUser.toReference(), options, SormasToSormasShareStatus.ACCEPTED, wrapper.getEntity(), (s, e) -> {
				wrapper.setShareInfoAssociatedObject(s);
			});
		});
	}

	@Override
	public void saveSharedEntities(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		S[] sharedEntities = sormasToSormasFacadeHelper.decryptSharedData(encryptedData, getShareDataClass());

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<PROCESSED> dataToSave = new ArrayList<>(sharedEntities.length);

		for (S data : sharedEntities) {
			try {
				DTO entity = data.getEntity();

				ValidationErrors caseErrors = validateSharedEntity(entity);

				if (caseErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(entity), caseErrors);
				} else {
					PROCESSED processedData = getSharedDataProcessor().processSharedData(data, null);

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

		for (PROCESSED processedData : dataToSave) {
			getProcessedDataPersister().persistSharedData(processedData);
		}
	}

	@Override
	public void returnEntity(String entityUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		options.setHandOverOwnership(true);
		User currentUser = userService.getCurrentUser();

		ADO entity = getEntityService().getByUuid(entityUuid);
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
				saveNewShareInfo(currentUser.toReference(), options, SormasToSormasShareStatus.ACCEPTED, wrapper.getEntity(), (s, e) -> {
					wrapper.setShareInfoAssociatedObject(s);
				});
			}
		});
	}

	@Override
	public void saveReturnedEntity(SormasToSormasEncryptedDataDto sharedData) throws SormasToSormasException, SormasToSormasValidationException {
		S[] sharedEntities = sormasToSormasFacadeHelper.decryptSharedData(sharedData, getShareDataClass());

		Map<String, ValidationErrors> validationErrors = new HashMap<>();
		List<PROCESSED> entitiesToSave = new ArrayList<>(sharedEntities.length);
		List<DTO> existingEntities =
			loadExistingEntities(Arrays.stream(sharedEntities).map(e -> e.getEntity().getUuid()).collect(Collectors.toList()));
		Map<String, DTO> existingEntitiesMap = existingEntities.stream().collect(Collectors.toMap(EntityDto::getUuid, Function.identity()));

		for (S sharedEntity : sharedEntities) {
			try {
				DTO entity = sharedEntity.getEntity();
				ValidationErrors entityErrors = validateExistingEntity(entity, existingEntities);
				if (entityErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(entity), entityErrors);
				} else {
					DTO exsitingEntity = existingEntitiesMap.get(sharedEntity.getEntity().getUuid());
					PROCESSED processedEntity = getSharedDataProcessor().processSharedData(sharedEntity, exsitingEntity);
					entitiesToSave.add(processedEntity);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (PROCESSED contactData : entitiesToSave) {
			getProcessedDataPersister().persistReturnedData(contactData, contactData.getOriginInfo());
		}
	}

	@Override
	public void syncEntity(String entityUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		ADO entity = getEntityService().getByUuid(entityUuid);

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
					SormasToSormasShareStatus.ACCEPTED,
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
		List<PROCESSED> entitiesToSave = new ArrayList<>(sharedEntities.length);
		List<DTO> existingEntities =
			loadExistingEntities(Arrays.stream(sharedEntities).map(e -> e.getEntity().getUuid()).collect(Collectors.toList()));
		Map<String, DTO> existingEntitiesMap = existingEntities.stream().collect(Collectors.toMap(EntityDto::getUuid, Function.identity()));

		for (S sharedEntity : sharedEntities) {
			try {
				DTO entity = sharedEntity.getEntity();

				ValidationErrors contactErrors = validateExistingEntity(entity, existingEntities);
				if (contactErrors.hasError()) {
					validationErrors.put(buildEntityValidationGroupName(entity), contactErrors);
				} else {
					DTO existingEntity = existingEntitiesMap.get(sharedEntity.getEntity().getUuid());
					PROCESSED processedContactData = getSharedDataProcessor().processSharedData(sharedEntity, existingEntity);
					entitiesToSave.add(processedContactData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.putAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (PROCESSED contactData : entitiesToSave) {
			getProcessedDataPersister().persistSyncData(contactData);
		}
	}

	private String buildEntityValidationGroupName(HasUuid entity) {
		return buildValidationGroupName(entityCaptionTag, entity);
	}

	protected abstract BaseAdoService<ADO> getEntityService();

	protected abstract ShareDataBuilder<ADO, S, PREVIEW> getShareDataBuilder();

	protected abstract SharedDataProcessor<DTO, S, PROCESSED, PREVIEW> getSharedDataProcessor();

	protected abstract ProcessedDataPersister<PROCESSED> getProcessedDataPersister();

	protected abstract Class<S[]> getShareDataClass();

	protected abstract void validateEntitiesBeforeSend(List<ADO> entities) throws SormasToSormasException;

	protected abstract ValidationErrors validateSharedEntity(DTO entity);

	protected abstract ValidationErrors validateSharedPreview(PREVIEW preview);

	protected abstract void setEntityShareInfoAssociatedObject(SormasToSormasShareInfo sormasToSormasShareInfo, ADO entity);

	protected abstract SormasToSormasShareInfo getShareInfoByEntityAndOrganization(String entityUuid, String organizationId);

	protected abstract List<DTO> loadExistingEntities(List<String> uuids);

	protected ValidationErrors validateExistingEntity(DTO entity, List<DTO> existingEntities) {
		ValidationErrors errors = new ValidationErrors();

		if (existingEntities.stream().noneMatch(e -> e.getUuid().equals(entity.getUuid()))) {
			errors
				.add(I18nProperties.getCaption(entityCaptionTag), I18nProperties.getValidationError(Validations.sormasToSormasReturnEntityNotExists));

		}

		return errors;
	}

	private <T> void saveNewShareInfo(
		UserReferenceDto sender,
		SormasToSormasOptionsDto options,
		SormasToSormasShareStatus status,
		T associatedObject,
		BiConsumer<SormasToSormasShareInfo, T> setAssociatedObject) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));
		shareInfo.setOrganizationId(options.getOrganization().getUuid());
		shareInfo.setSender(userService.getByReferenceDto(sender));
		shareInfo.setStatus(status);

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

	private SormasToSormasShareRequestDto createNewShareRequest(
		ShareRequestStatus status,
		SormasToSormasOriginInfoDto originInfo,
		List<PREVIEW> previews) {
		SormasToSormasShareRequestDto request = SormasToSormasShareRequestDto.build();
		request.setDataType(shareRequestDataType);
		request.setStatus(status);
		request.setOriginInfo(originInfo);

		setShareRequestPreviewData(request, previews);

		return request;
	}

	protected abstract void setShareRequestPreviewData(SormasToSormasShareRequestDto request, List<PREVIEW> previews);
}
