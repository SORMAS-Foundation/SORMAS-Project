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

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;
import static de.symeda.sormas.backend.sormastosormas.ValidationHelper.buildValidationGroupName;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityInterface;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareTree;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.SormasToSormasEntityDto;
import de.symeda.sormas.backend.common.AbstractDomainObject;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedData;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessorHelper;
import de.symeda.sormas.backend.sormastosormas.entities.AssociatedEntityWrapper;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntity;
import de.symeda.sormas.backend.sormastosormas.entities.SyncDataDto;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;
import de.symeda.sormas.backend.sormastosormas.share.ShareData;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestData;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.sormastosormas.share.sharerequest.SormasToSormasShareRequest;
import de.symeda.sormas.backend.sormastosormas.share.sharerequest.SormasToSormasShareRequestFacadeEJB.SormasToSormasShareRequestFacadeEJBLocal;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;

public abstract class AbstractSormasToSormasInterface<ADO extends AbstractDomainObject & SormasToSormasEntity, DTO extends EntityDto & SormasToSormasEntityDto, S extends SormasToSormasDto<DTO>, PREVIEW extends HasUuid, PROCESSED extends ProcessedData<DTO>>
	implements SormasToSormasEntityInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSormasToSormasInterface.class);

	private static final String REQUEST_ACCEPTED_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.REQUEST_ACCEPTED_ENDPOINT;

	@EJB
	private UserService userService;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private SormasToSormasShareRequestFacadeEJBLocal shareRequestFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;
	@EJB
	private ReceivedDataProcessorHelper dataProcessorHelper;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb;
	@EJB
	private SormasToSormasShareInfoFacadeEjbLocal shareInfoFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;

	private final String requestEndpoint;
	private final String requestRejectEndpoint;
	private final String requestGetDataEndpoint;
	private final String saveEndpoint;
	private final String syncEndpoint;
	private final String sharesEndpoint;

	private final String entityCaptionTag;

	private final ShareRequestDataType shareRequestDataType;
	private final Class<? extends ShareRequestData<PREVIEW>> previewType;
	private final Class<? extends SyncDataDto<S>> syncDataType;

	public AbstractSormasToSormasInterface() {
		throw new RuntimeException("AbstractSormasToSormasInterface should not be instantiated");
	}

	public AbstractSormasToSormasInterface(
		String requestEndpoint,
		String requestRejectEndpoint,
		String requestGetDataEndpoint,
		String saveEndpoint,
		String syncEndpoint,
		String sharesEndpoint,
		String entityCaptionTag,
		ShareRequestDataType shareRequestDataType,
		Class<? extends ShareRequestData<PREVIEW>> previewType,
		Class<? extends SyncDataDto<S>> syncDataType) {
		this.requestEndpoint = requestEndpoint;
		this.requestRejectEndpoint = requestRejectEndpoint;
		this.requestGetDataEndpoint = requestGetDataEndpoint;
		this.saveEndpoint = saveEndpoint;
		this.syncEndpoint = syncEndpoint;
		this.sharesEndpoint = sharesEndpoint;
		this.entityCaptionTag = entityCaptionTag;
		this.shareRequestDataType = shareRequestDataType;
		this.previewType = previewType;
		this.syncDataType = syncDataType;
	}

	@Override
	public void share(List<String> entityUuids, @Valid SormasToSormasOptionsDto options) throws SormasToSormasException {
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT)) {
			sendShareRequest(entityUuids, options);
		} else {
			shareEntities(entityUuids, options);
		}
	}

	public void sendShareRequest(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<ADO> entities = getEntityService().getByUuids(entityUuids);

		if (options.isHandOverOwnership()) {
			validateOwnership(entities);
			ensureConsistentOptions(options);
		}

		validateEntitiesBeforeShare(entities, options.isHandOverOwnership());

		List<PREVIEW> previewsToSend = new ArrayList<>();
		List<AssociatedEntityWrapper<?>> associatedEntities = new ArrayList<>();
		for (ADO entity : entities) {
			ShareData<ADO, PREVIEW> shareData = getShareDataBuilder().buildShareDataPreview(entity, currentUser, options);

			previewsToSend.add(shareData.getDto());

			associatedEntities.addAll(shareData.getAssociatedEntities());
		}

		SormasToSormasOriginInfoDto originInfo = dataBuilderHelper.createSormasToSormasOriginInfo(currentUser, options);
		String requestUuid = DataHelper.createUuid();

		sormasToSormasRestClient
			.post(options.getOrganization().getId(), requestEndpoint, new ShareRequestData<>(requestUuid, previewsToSend, originInfo), null);

		saveNewShareInfo(currentUser.toReference(), options, requestUuid, ShareRequestStatus.PENDING, entities, associatedEntities);
	}

	protected void ensureConsistentOptions(SormasToSormasOptionsDto options) {
		if (options.isHandOverOwnership()) {
			options.setPseudonymizePersonalData(false);
			options.setPseudonymizeSensitiveData(false);

			if (SormasToSormasCaseDto[].class.isAssignableFrom(getShareDataClass())) {
				options.setWithSamples(true);
			}
		}
	}

	@Override
	public void saveShareRequest(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		ShareRequestData<PREVIEW> shareData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, previewType);

		List<ValidationErrors> validationErrors = new ArrayList<>();
		List<PREVIEW> previews = shareData.getPreviews();
		List<PREVIEW> previewsToSave = new ArrayList<>(previews.size());

		SormasToSormasOriginInfoDto originInfo = shareData.getOriginInfo();
		ValidationErrors originInfoErrors = dataProcessorHelper.processOriginInfo(originInfo, entityCaptionTag);
		if (originInfoErrors.hasError()) {
			validationErrors.add(new ValidationErrors(new ValidationErrorGroup(entityCaptionTag), originInfoErrors));
		}

		for (PREVIEW preview : previews) {
			try {
				ValidationErrors caseErrors = validateSharedPreview(preview);

				if (caseErrors.hasError()) {
					validationErrors.add(new ValidationErrors(buildEntityValidationGroupName(preview), caseErrors));
				} else {
					PREVIEW processedData = getReceivedDataProcessor().processReceivedPreview(preview);
					previewsToSave.add(processedData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.addAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		shareRequestFacade.saveShareRequest(createNewShareRequest(shareData.getRequestUuid(), originInfo, previewsToSave));
	}

	@Override
	public void sendRejectShareRequest(String uuid) throws SormasToSormasException {
		SormasToSormasShareRequestDto shareRequest = shareRequestFacade.getShareRequestByUuid(uuid);

		if (shareRequest.getStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRejectNotPending);
		}

		String organizationId = shareRequest.getOriginInfo().getOrganizationId();
		sormasToSormasRestClient.post(organizationId, requestRejectEndpoint, uuid, null);

		shareRequest.setChangeDate(new Date());
		shareRequest.setRejected();

		shareRequestFacade.saveShareRequest(shareRequest);
	}

	@Override
	@Transactional
	public void rejectShareRequest(SormasToSormasEncryptedDataDto encryptedRequestUuid) throws SormasToSormasException {
		String requestUuid = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedRequestUuid, String.class);
		SormasToSormasShareInfo shareInfo = shareInfoService.getByRequestUuid(requestUuid);

		if (shareInfo.getRequestStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRejectNotPending);
		}

		shareInfo.setRequestStatus(ShareRequestStatus.REJECTED);
		shareInfo.setOwnershipHandedOver(false);
		shareInfoService.ensurePersisted(shareInfo);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void acceptShareRequest(String uuid) throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasShareRequestDto shareRequest = shareRequestFacade.getShareRequestByUuid(uuid);

		if (shareRequest.getStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasAcceptNotPending);
		}

		String organizationId = shareRequest.getOriginInfo().getOrganizationId();

		SormasToSormasEncryptedDataDto encryptedData =
			sormasToSormasRestClient.post(organizationId, requestGetDataEndpoint, uuid, SormasToSormasEncryptedDataDto.class);

		saveSharedEntities(encryptedData, shareRequest.getOriginInfo());

		// notify the sender that the request has been accepted
		sormasToSormasRestClient.post(organizationId, REQUEST_ACCEPTED_ENDPOINT, uuid, null);

		shareRequest.setChangeDate(new Date());
		shareRequest.setStatus(ShareRequestStatus.ACCEPTED);
		shareRequestFacade.saveShareRequest(shareRequest);
	}

	@Override
	public SormasToSormasEncryptedDataDto getDataForShareRequest(SormasToSormasEncryptedDataDto encryptedRequestUuid) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		String requestUuid = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedRequestUuid, String.class);
		SormasToSormasShareInfo shareInfo = shareInfoService.getByRequestUuid(requestUuid);

		List<ShareData<ADO, S>> shareData = getShareDataBuilder().buildShareData(currentUser, shareInfo);

		List<S> entitiesToSend = shareData.stream().map(ShareData::getDto).collect(Collectors.toList());
		validateEntitiesBeforeShare(shareData.stream().map(ShareData::getEntity).collect(Collectors.toList()), shareInfo.isOwnershipHandedOver());

		return sormasToSormasEncryptionEjb.signAndEncrypt(entitiesToSend, shareInfo.getOrganizationId());
	}

	@Override
	public void shareEntities(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<ADO> entities = getEntityService().getByUuids(entityUuids);

		validateEntitiesBeforeShare(entities, options.isHandOverOwnership());
		ensureConsistentOptions(options);

		List<S> entitiesToSend = new ArrayList<>();
		List<AssociatedEntityWrapper<?>> associatedEntities = new ArrayList<>();
		for (ADO entity : entities) {
			ShareData<ADO, S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

			entitiesToSend.add(shareData.getDto());

			associatedEntities.addAll(shareData.getAssociatedEntities());
		}

		sormasToSormasRestClient.post(options.getOrganization().getId(), saveEndpoint, entitiesToSend, null);
		SormasToSormasShareInfo shareInfo =
			saveNewShareInfo(currentUser.toReference(), options, DataHelper.createUuid(), ShareRequestStatus.ACCEPTED, entities, associatedEntities);

		try {
			shareInfoService.handleOwnershipChangeInExternalSurvTool(shareInfo);
		} catch (ExternalSurveillanceToolException e) {
			LOGGER.error("Failed to delete shared entities in external surveillance tool", e);

			throw SormasToSormasException.fromStringPropertyWithWarning(Strings.errorSormasToSormasDeleteFromExternalSurveillanceTool);
		}
	}

	@Override
	public void saveSharedEntities(SormasToSormasEncryptedDataDto encryptedData, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasException, SormasToSormasValidationException {
		decryptAndPersist(encryptedData, data -> getProcessedDataPersister().persistSharedData(data), originInfo);
	}

	@Override
	public void returnEntity(String entityUuid, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();

		ADO entity = getEntityService().getByUuid(entityUuid);

		SormasToSormasOriginInfo originInfo = entity.getSormasToSormasOriginInfo();
		options.setHandOverOwnership(true);
		if (originInfo.getContacts().size() > 0) {
			options.setWithAssociatedContacts(true);
		}
		if (originInfo.getSamples().size() > 0) {
			options.setWithSamples(true);
		}
		if (originInfo.getEventParticipants().size() > 0) {
			options.setWithEventParticipants(true);
		}

		validateEntitiesBeforeShare(Collections.singletonList(entity), options.isHandOverOwnership());

		ShareData<ADO, S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

		sormasToSormasRestClient.put(options.getOrganization().getId(), saveEndpoint, Collections.singletonList(shareData.getDto()), null);

		originInfo.setOwnershipHandedOver(false);
		originInfoService.persist(originInfo);

		SormasToSormasShareInfo shareToOrigin = getShareInfoByEntityAndOrganization(shareData.getEntity().getUuid(), originInfo.getOrganizationId());
		if (shareToOrigin != null) {
			shareToOrigin.setOwnershipHandedOver(true);
			shareInfoService.persist(shareToOrigin);
		}

		try {
			shareInfoService.handleOwnershipChangeInExternalSurvTool(originInfo);
		} catch (ExternalSurveillanceToolException e) {
			LOGGER.error("Failed to delete shared entities in external surveillance tool", e);

			throw SormasToSormasException.fromStringPropertyWithWarning(Strings.errorSormasToSormasDeleteFromExternalSurveillanceTool);
		}
	}

	@Override
	public void saveReturnedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		decryptAndPersist(encryptedData, data -> getProcessedDataPersister().persistReturnedData(data, data.getOriginInfo()), null);
	}

	@Override
	public void syncShares(ShareTreeCriteria criteria) {
		walkShareTree(
			criteria,
			(entity, originInfo, parentCriteria) -> {
				// prevent stopping the iteration through the shares because of a failed sync operation
				// sync with as much servers as possible
				try {
					SormasToSormasOptionsDto options = dataBuilderHelper.createOptionsFromOriginInfoDto(originInfo);
					options.setHandOverOwnership(false);

					syncEntity(entity, originInfo.getOrganizationId(), options, parentCriteria);
				} catch (Exception e) {
					LOGGER.error("Failed to sync to [{}]", originInfo.getOrganizationId(), e);
				}
			},
			((entity, shareInfo, reShareCriteria, noForward) -> {
				if (!noForward) {
					// prevent stopping the iteration through the shares because of a failed sync operation
					// sync with as much servers as possible
					try {
						SormasToSormasOptionsDto options = dataBuilderHelper.createOptionsFormShareInfo(shareInfo);
						options.setHandOverOwnership(false);

						syncEntity(entity, shareInfo.getOrganizationId(), options, reShareCriteria);
					} catch (Exception e) {
						LOGGER.error("Failed to sync to [{}]", shareInfo.getOrganizationId(), e);
					}
				}
			}));
	}

	@Override
	public void saveSyncedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		SyncDataDto<S> syncData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, syncDataType);

		perisist(Collections.singletonList(syncData.getShareData()), data -> {
			getProcessedDataPersister().persistSyncData(data, syncData.getCriteria());
		}, null);
	}

	@Override
	public List<SormasToSormasShareTree> getAllShares(String uuid) throws SormasToSormasException {
		return getShareTrees(new ShareTreeCriteria(uuid));
	}

	@Override
	public SormasToSormasEncryptedDataDto getShareTrees(SormasToSormasEncryptedDataDto encryptedCriteria) throws SormasToSormasException {
		ShareTreeCriteria criteria = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedCriteria, ShareTreeCriteria.class);

		List<SormasToSormasShareTree> shares = getShareTrees(criteria);

		return sormasToSormasEncryptionEjb.signAndEncrypt(shares, encryptedCriteria.getSenderId());
	}

	private interface Persister<PROCESSED> {

		void call(PROCESSED data) throws SormasToSormasValidationException;
	}

	private void decryptAndPersist(
		SormasToSormasEncryptedDataDto encryptedData,
		Persister<PROCESSED> persister,
		SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasException, SormasToSormasValidationException {

		S[] receivedS2SEntities = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, getShareDataClass());

		perisist(Arrays.asList(receivedS2SEntities), persister, originInfo);
	}

	private void perisist(@Valid List<S> receivedS2SEntities, Persister<PROCESSED> persister, SormasToSormasOriginInfoDto originInfo)
		throws SormasToSormasValidationException {
		List<ValidationErrors> validationErrors = new ArrayList<>();
		List<PROCESSED> entitiesToPersist = new ArrayList<>(receivedS2SEntities.size());
		List<DTO> existingEntities =
			loadExistingEntities(receivedS2SEntities.stream().map(e -> e.getEntity().getUuid()).collect(Collectors.toList()));
		Map<String, DTO> existingEntitiesMap = existingEntities.stream().collect(Collectors.toMap(EntityDto::getUuid, Function.identity()));

		for (S receivedS2SEntity : receivedS2SEntities) {
			try {

				DTO receivedEntity = receivedS2SEntity.getEntity();
				ValidationErrors validationError;

				if (existingEntities.isEmpty()) {
					// check that the newly shared entity does not exist
					validationError = validateSharedEntity(receivedEntity);
				} else {
					// check that the received entity already exists
					validationError = validateExistingEntity(receivedEntity, existingEntities);
				}

				if (validationError.hasError()) {
					validationErrors.add(new ValidationErrors(buildEntityValidationGroupName(receivedEntity), validationError));
				} else {
					DTO existingEntity = existingEntitiesMap.get(receivedEntity.getUuid());
					PROCESSED processedData = getReceivedDataProcessor().processReceivedData(receivedS2SEntity, existingEntity);

					if (existingEntities.isEmpty()) {
						// update origin info if we receive a new entity
						processedData.getEntity().setSormasToSormasOriginInfo(originInfo != null ? originInfo : processedData.getOriginInfo());
					}

					entitiesToPersist.add(processedData);
				}
			} catch (SormasToSormasValidationException validationException) {
				validationErrors.addAll(validationException.getErrors());
			}
		}

		if (validationErrors.size() > 0) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		for (PROCESSED data : entitiesToPersist) {
			persister.call(data);
		}
	}

	private ValidationErrorGroup buildEntityValidationGroupName(HasUuid entity) {
		return buildEntityValidationGroupName(entity.getUuid());
	}

	private ValidationErrorGroup buildEntityValidationGroupName(String uuid) {
		return buildValidationGroupName(entityCaptionTag, uuid);
	}

	protected abstract BaseAdoService<ADO> getEntityService();

	protected abstract ShareDataBuilder<ADO, S, PREVIEW> getShareDataBuilder();

	protected abstract ReceivedDataProcessor<DTO, S, PROCESSED, PREVIEW> getReceivedDataProcessor();

	protected abstract ProcessedDataPersister<PROCESSED> getProcessedDataPersister();

	protected abstract Class<S[]> getShareDataClass();

	protected abstract void validateEntitiesBeforeShare(List<ADO> entities, boolean handOverOwnership) throws SormasToSormasException;

	protected abstract ValidationErrors validateSharedEntity(DTO entity);

	protected abstract ValidationErrors validateSharedPreview(PREVIEW preview);

	protected abstract void addEntityToShareInfo(SormasToSormasShareInfo sormasToSormasShareInfo, List<ADO> entities);

	protected abstract SormasToSormasShareInfo getShareInfoByEntityAndOrganization(String entityUuid, String receiverId);

	protected abstract List<DTO> loadExistingEntities(List<String> uuids);

	protected abstract List<SormasToSormasShareInfo> getEntityShares(ADO entity);

	protected ValidationErrors validateExistingEntity(DTO entity, List<DTO> existingEntities) {
		ValidationErrors errors = new ValidationErrors();

		if (existingEntities.stream().noneMatch(e -> e.getUuid().equals(entity.getUuid()))) {
			errors.add(new ValidationErrorGroup(entityCaptionTag), new ValidationErrorMessage(Validations.sormasToSormasReturnEntityNotExists));

		}

		return errors;
	}

	private SormasToSormasShareInfo saveNewShareInfo(
		UserReferenceDto sender,
		SormasToSormasOptionsDto options,
		String requestUuid,
		ShareRequestStatus requestStatus,
		List<ADO> entities,
		List<AssociatedEntityWrapper<?>> associatedEntities) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();

		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setCreationDate(new Timestamp(new Date().getTime()));
		shareInfo.setOrganizationId(options.getOrganization().getId());
		shareInfo.setSender(userService.getByReferenceDto(sender));
		shareInfo.setRequestUuid(requestUuid);
		shareInfo.setRequestStatus(requestStatus);

		addOptionsToShareInfo(options, shareInfo);

		addEntityToShareInfo(shareInfo, entities);
		associatedEntities.forEach(e -> e.addEntityToShareInfo(shareInfo));

		shareInfoService.ensurePersisted(shareInfo);

		return shareInfo;
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

	private void syncEntity(ADO entity, String targetOrganizationId, SormasToSormasOptionsDto options, ShareTreeCriteria criteria)
		throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();

		ShareData<ADO, S> shareData = getShareDataBuilder().buildShareData(entity, currentUser, options);

		sormasToSormasRestClient.post(targetOrganizationId, syncEndpoint, new SyncDataDto<>(shareData.getDto(), criteria), null);

		SormasToSormasShareInfo shareInfo = getShareInfoByEntityAndOrganization(entity.getUuid(), options.getOrganization().getId());
		if (shareInfo != null) {
			List<AssociatedEntityWrapper<?>> additionalAssociatedObjects = shareData.getAssociatedEntities()
				.stream()
				.filter(entityWrapper -> !entityWrapper.isAddedToShareInfo(shareInfo))
				.collect(Collectors.toList());

			updateShareInfoOptions(shareInfo, additionalAssociatedObjects, options);

			try {
				shareInfoService.handleOwnershipChangeInExternalSurvTool(shareInfo);
			} catch (ExternalSurveillanceToolException e) {
				LOGGER.error("Failed to delete shared entities in external surveillance tool", e);

				throw SormasToSormasException.fromStringPropertyWithWarning(Strings.errorSormasToSormasDeleteFromExternalSurveillanceTool);
			}
		} else {
			SormasToSormasOriginInfo originInfo = entity.getSormasToSormasOriginInfo();

			List<AssociatedEntityWrapper<?>> additionalAssociatedObjects = shareData.getAssociatedEntities()
				.stream()
				.filter(entityWrapper -> !entityWrapper.isAddedToOriginInfo(originInfo))
				.collect(Collectors.toList());

			SormasToSormasShareInfo shareToOrigin = getShareInfoByEntityAndOrganization(entity.getUuid(), originInfo.getOrganizationId());
			if (shareToOrigin != null) {
				updateShareInfoOptions(shareToOrigin, additionalAssociatedObjects, options);
			} else {
				SormasToSormasShareRequest shareRequest = originInfo.getRequest();
				String requestUuid = shareRequest != null ? shareRequest.getUuid() : null;
				ShareRequestStatus requestStatus = shareRequest == null ? shareRequest.getStatus() : ShareRequestStatus.ACCEPTED;

				saveNewShareInfo(
					currentUser.toReference(),
					options,
					requestUuid,
					requestStatus,
					Collections.singletonList(entity),
					additionalAssociatedObjects);
			}
		}
	}

	private void updateShareInfoOptions(
		SormasToSormasShareInfo shareInfo,
		List<AssociatedEntityWrapper<?>> associatedObjects,
		SormasToSormasOptionsDto options) {
		addOptionsToShareInfo(options, shareInfo);
		associatedObjects.forEach(a -> a.addEntityToShareInfo(shareInfo));

		shareInfoService.ensurePersisted(shareInfo);
	}

	private SormasToSormasShareRequestDto createNewShareRequest(String requestUuid, SormasToSormasOriginInfoDto originInfo, List<PREVIEW> previews) {
		SormasToSormasShareRequestDto request = SormasToSormasShareRequestDto.build();
		request.setUuid(requestUuid);
		request.setDataType(shareRequestDataType);
		request.setStatus(ShareRequestStatus.PENDING);

		request.setOriginInfo(originInfo);

		setShareRequestPreviewData(request, previews);

		return request;
	}

	protected void validateOwnership(List<ADO> entities) throws SormasToSormasException {
		List<String> notOwnedUuids = getUuidsWithPendingOwnershipHandedOver(entities);
		if (notOwnedUuids.size() > 0) {

			List<ValidationErrors> errors = notOwnedUuids.stream()
				.map(
					uuid -> new ValidationErrors(
						buildEntityValidationGroupName(uuid),
						ValidationErrors.create(
							new ValidationErrorGroup(entityCaptionTag),
							new ValidationErrorMessage(Strings.errorSormasToSormasOwnershipAlreadyHandedOver))))
				.collect(Collectors.toList());

			throw SormasToSormasException.fromStringProperty(errors, Strings.errorSormasToSormasShare);
		}
	}

	protected abstract List<String> getUuidsWithPendingOwnershipHandedOver(List<ADO> entities);

	protected abstract void setShareRequestPreviewData(SormasToSormasShareRequestDto request, List<PREVIEW> previews);

	private List<SormasToSormasShareTree> getShareTrees(ShareTreeCriteria criteria) {
		String ownOrganizationId = configFacadeEjb.getS2SConfig().getId();
		List<SormasToSormasShareTree> shares = new ArrayList<>();
		List<SormasToSormasShareTree> reShareTrees = new ArrayList<>();

		LOGGER.info(
			"Get shares for {} from {} by {}",
			criteria.getEntityUuid(),
			ownOrganizationId,
			criteria.getOriginInfo() != null ? criteria.getOriginInfo().getOrganizationId() : ownOrganizationId);

		walkShareTree(criteria, (entity, originInfo, parentCriteria) -> {
			try {
				SormasToSormasEncryptedDataDto encryptedShares = sormasToSormasRestClient
					.post(originInfo.getOrganizationId(), sharesEndpoint, parentCriteria, SormasToSormasEncryptedDataDto.class);

				shares.addAll(Arrays.asList(sormasToSormasEncryptionEjb.decryptAndVerify(encryptedShares, SormasToSormasShareTree[].class)));
			} catch (SormasToSormasException e) {
				// stop iteration and fail in case of error
				throw new RuntimeException("Failed to get all shares form server [" + originInfo.getOrganizationId() + "]", e);
			}
		}, (entity, shareInfo, reShareCriteria, noForward) -> {
			try {
				List<SormasToSormasShareTree> reShares = Collections.emptyList();
				if (!noForward) {
					SormasToSormasEncryptedDataDto encryptedShares = sormasToSormasRestClient.post(
						shareInfo.getOrganizationId(),
						sharesEndpoint,
						new ShareTreeCriteria(reShareCriteria.getEntityUuid(), reShareCriteria.getExceptedOrganizationId(), true),
						SormasToSormasEncryptedDataDto.class);

					reShares = Arrays.asList(sormasToSormasEncryptionEjb.decryptAndVerify(encryptedShares, SormasToSormasShareTree[].class));
				}

				reShareTrees.add(
					new SormasToSormasShareTree(
						SormasToSormasOriginInfoFacadeEjb.toDto(entity.getSormasToSormasOriginInfo()),
						shareInfoFacade.toDto(shareInfo),
						reShares));
			} catch (SormasToSormasException e) {
				// stop iteration and fail in case of error
				throw new RuntimeException("Failed to get all shares form server [" + shareInfo.getOrganizationId() + "]", e);
			}
		});

		SormasToSormasShareTree parentShare = findParentShare(shares, ownOrganizationId);
		if (parentShare != null) {
			parentShare.setReShares(reShareTrees);
		} else {
			shares.addAll(reShareTrees);
		}

		return shares;
	}

	private void walkShareTree(ShareTreeCriteria criteria, WalkParent<ADO> walkParent, WalkReShare<ADO> walkReShare) {
		ADO entity = getEntityService().getByUuid(criteria.getEntityUuid());

		if (entity == null) {
			return;
		}

		SormasToSormasOriginInfo originInfo = entity.getSormasToSormasOriginInfo();
		List<SormasToSormasShareInfo> entityShares = getEntityShares(entity);

		if (!criteria.isForwardOnly() && originInfo != null && !originInfo.getOrganizationId().equals(criteria.getExceptedOrganizationId())) {
			SormasToSormasShareRequest shareRequest = originInfo.getRequest();
			String ownOrganizationId = configFacadeEjb.getS2SConfig().getId();

			if (shareRequest == null || shareRequest.getStatus() == ShareRequestStatus.ACCEPTED) {
				walkParent.walk(entity, originInfo, new ShareTreeCriteria(criteria.getEntityUuid(), ownOrganizationId, false));
			}
		}

		for (SormasToSormasShareInfo s : entityShares) {
			boolean noForward = (s.getRequestStatus() != null && s.getRequestStatus() != ShareRequestStatus.ACCEPTED)
				|| s.getOrganizationId().equals(criteria.getExceptedOrganizationId());
			if (originInfo != null) {
				noForward = noForward || s.getOrganizationId().equals(originInfo.getOrganizationId());
			}

			walkReShare.walk(entity, s, new ShareTreeCriteria(criteria.getEntityUuid(), criteria.getExceptedOrganizationId(), true), noForward);
		}
	}

	private interface WalkParent<ADO> {

		void walk(ADO entity, SormasToSormasOriginInfo originInfo, ShareTreeCriteria criteria);
	}

	private interface WalkReShare<ADO> {

		void walk(ADO entity, SormasToSormasShareInfo shareInfo, ShareTreeCriteria criteria, boolean noForward);
	}

	private SormasToSormasShareTree findParentShare(List<SormasToSormasShareTree> shares, String ownOrganizationId) {
		for (SormasToSormasShareTree shareTree : shares) {
			if (shareTree.getShare().getTargetDescriptor().getId().equals(ownOrganizationId)) {
				return shareTree;
			} else if (shareTree.getReShares().size() > 0) {
				SormasToSormasShareTree subTree = findParentShare(shareTree.getReShares(), ownOrganizationId);

				if (subTree != null) {
					return subTree;
				}
			}
		}

		return null;
	}
}
