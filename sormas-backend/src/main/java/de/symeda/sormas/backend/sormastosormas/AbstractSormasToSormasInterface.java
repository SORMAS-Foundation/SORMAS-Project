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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.sormastosormas.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.ShareTreeCriteria;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareTree;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.api.sormastosormas.entities.DuplicateResultType;
import de.symeda.sormas.api.sormastosormas.entities.SormasToSormasEntityDto;
import de.symeda.sormas.api.sormastosormas.entities.SormasToSormasEntityInterface;
import de.symeda.sormas.api.sormastosormas.entities.SyncDataDto;
import de.symeda.sormas.api.sormastosormas.entities.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.entities.contact.SormasToSormasContactDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventDto;
import de.symeda.sormas.api.sormastosormas.entities.event.SormasToSormasEventParticipantDto;
import de.symeda.sormas.api.sormastosormas.entities.sample.SormasToSormasSampleDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReportService;
import de.symeda.sormas.backend.common.AbstractCoreAdoService;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.common.CoreAdo;
import de.symeda.sormas.backend.common.QueryJoins;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.contact.ContactService;
import de.symeda.sormas.backend.event.Event;
import de.symeda.sormas.backend.event.EventParticipant;
import de.symeda.sormas.backend.event.EventParticipantService;
import de.symeda.sormas.backend.event.EventService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.immunization.ImmunizationService;
import de.symeda.sormas.backend.immunization.entity.Immunization;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sample.SampleService;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.ProcessedEntitiesPersister;
import de.symeda.sormas.backend.sormastosormas.entities.ReceivedEntitiesProcessor;
import de.symeda.sormas.backend.sormastosormas.entities.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.entities.ShareDataExistingEntities;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntitiesHelper;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoService;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilderHelper;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestAcceptData;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestData;
import de.symeda.sormas.backend.sormastosormas.share.incoming.ShareRequestPreviews;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequest;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestFacadeEJB.SormasToSormasShareRequestFacadeEJBLocal;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareInfoHelper;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoFacadeEjb.SormasToSormasShareInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.user.User;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

public abstract class AbstractSormasToSormasInterface<ADO extends CoreAdo & SormasToSormasShareable, DTO extends SormasToSormasShareableDto, S extends SormasToSormasEntityDto<DTO>>
	implements SormasToSormasEntityInterface {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSormasToSormasInterface.class);

	private static final String REQUEST_ACCEPTED_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.REQUEST_ACCEPTED_ENDPOINT;

	@EJB
	private UserService userService;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal originInfoFacade;
	@EJB
	private SormasToSormasOriginInfoService originInfoService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private ShareRequestInfoService shareRequestInfoService;
	@EJB
	private SormasToSormasShareRequestFacadeEJBLocal shareRequestFacade;
	@EJB
	private ShareDataBuilderHelper dataBuilderHelper;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb;
	@EJB
	private SormasToSormasShareInfoFacadeEjbLocal shareInfoFacade;
	@EJB
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;
	@EJB
	private ShareDataBuilder shareDataBuilder;
	@EJB
	private ReceivedEntitiesProcessor receivedEntitiesProcessor;
	@EJB
	private ProcessedEntitiesPersister processedEntitiesPersister;
	@EJB
	private CaseService caseService;
	@EJB
	private ContactService contactService;
	@EJB
	private EventService eventService;
	@EJB
	private EventParticipantService eventParticipantService;
	@EJB
	private SampleService sampleService;
	@EJB
	private ImmunizationService immunizationService;
	@EJB
	private SurveillanceReportService surveillanceReportService;
	@EJB
	private SormasToSormasEntitiesHelper sormasToSormasEntitiesHelper;

	private final String requestEndpoint;
	private final String requestGetDataEndpoint;
	private final String saveEndpoint;
	private final String syncEndpoint;
	private final String sharesEndpoint;

	private final String entityCaptionTag;

	private final ShareRequestDataType shareRequestDataType;

	protected AbstractSormasToSormasInterface() {
		throw new RuntimeException("AbstractSormasToSormasInterface should not be instantiated");
	}

	protected AbstractSormasToSormasInterface(
		String requestEndpoint,
		String requestGetDataEndpoint,
		String saveEndpoint,
		String syncEndpoint,
		String sharesEndpoint,
		String entityCaptionTag,
		ShareRequestDataType shareRequestDataType) {
		this.requestEndpoint = requestEndpoint;
		this.requestGetDataEndpoint = requestGetDataEndpoint;
		this.saveEndpoint = saveEndpoint;
		this.syncEndpoint = syncEndpoint;
		this.sharesEndpoint = sharesEndpoint;
		this.entityCaptionTag = entityCaptionTag;
		this.shareRequestDataType = shareRequestDataType;
	}

	@Override
	@Transactional(value = Transactional.TxType.REQUIRES_NEW,
		rollbackOn = {
			Exception.class })
	@DenyAll
	public void share(List<String> entityUuids, @Valid SormasToSormasOptionsDto options) throws SormasToSormasException {
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_ACCEPT_REJECT)) {
			sendShareRequest(entityUuids, options);
		} else {
			shareEntities(entityUuids, options);
		}
	}

	/**
	 * Send a S2S share request for the ADOs identified by the given UUIDs to a remote S2S instance specified by options.
	 * 
	 * @param entityUuids
	 *            The entities for which should be delivered to the remote as preview.
	 * @param options
	 *            S2S request options also including the receiver.
	 * @throws SormasToSormasException
	 *             Errors out in case validation fails or request cannot be sent.
	 */
	private void sendShareRequest(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {

		User currentUser = userService.getCurrentUser();
		List<ADO> entities = getEntityService().getByUuids(entityUuids);

		if (options.isHandOverOwnership()) {
			validateOwnership(entities);
			ensureConsistentOptions(options);
		}

		validateEntitiesBeforeShare(entities, options.isHandOverOwnership(), options.isWithSamples(), options.getOrganization().getId(), false);

		String requestUuid = DataHelper.createUuid();

		ShareRequestInfo shareRequestInfo =
			createShareRequestInfoForEntities(requestUuid, ShareRequestStatus.PENDING, options, entities, currentUser, false);

		ShareRequestPreviews previewsToSend = shareDataBuilder.buildShareDataPreview(shareRequestInfo);
		SormasToSormasOriginInfoDto originInfo = dataBuilderHelper.createSormasToSormasOriginInfo(currentUser, options);

		sormasToSormasRestClient
			.post(options.getOrganization().getId(), requestEndpoint, new ShareRequestData(requestUuid, previewsToSend, originInfo), null);

		shareRequestInfoService.ensurePersisted(shareRequestInfo);
	}

	protected void ensureConsistentOptions(SormasToSormasOptionsDto options) {
		if (options.isHandOverOwnership()) {
			options.setPseudonymizeData(false);

			if (SormasToSormasCaseDto[].class.isAssignableFrom(getShareDataClass())
				|| SormasToSormasContactDto[].class.isAssignableFrom(getShareDataClass())) {
				options.setWithSamples(true);
				options.setWithImmunizations(true);

				if (SormasToSormasContactDto[].class.isAssignableFrom(getShareDataClass())) {
					options.setWithSurveillanceReports(true);
				}
			}
		}
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_CLIENT)
	public void saveShareRequest(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		ShareRequestData shareData = processShareRequest(encryptedData);
		shareRequestFacade.saveShareRequest(createShareRequest(shareData));
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_PROCESS)
	public DuplicateResult acceptShareRequest(String requestUuid, boolean checkDuplicates)
		throws SormasToSormasException, SormasToSormasValidationException {
		SormasToSormasShareRequestDto shareRequest = shareRequestFacade.getShareRequestByUuid(requestUuid);

		if (shareRequest == null || shareRequest.getStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		String organizationId = shareRequest.getOriginInfo().getOrganizationId();

		validateShareRequestBeforeAccept(shareRequest);

		SormasToSormasEncryptedDataDto encryptedData =
			sormasToSormasRestClient.post(organizationId, requestGetDataEndpoint, requestUuid, SormasToSormasEncryptedDataDto.class);

		DuplicateResult duplicateCheckResult = decryptAndPersist(encryptedData, (data, existingData) -> {
			DuplicateResult duplicateResult = DuplicateResult.none();

			if (checkDuplicates && !data.getOriginInfo().isPseudonymizedData()) {
				duplicateResult = processedEntitiesPersister.checkForSimilarEntities(data, existingData);
			}

			if (duplicateResult.getType() == DuplicateResultType.NONE) {
				processedEntitiesPersister.persistSharedData(data, shareRequest.getOriginInfo(), existingData);
			}

			return duplicateResult;
		});

		if (duplicateCheckResult.getType() == DuplicateResultType.NONE) {
			// notify the sender that the request has been accepted
			sormasToSormasRestClient.post(
				organizationId,
				REQUEST_ACCEPTED_ENDPOINT,
				new ShareRequestAcceptData(
					requestUuid,
					shareRequest.getOriginInfo().isOwnershipHandedOver() ? configFacadeEjb.getS2SConfig().getDistrictExternalId() : null),
				null);

			shareRequest.setChangeDate(new Date());
			shareRequest.setStatus(ShareRequestStatus.ACCEPTED);
			shareRequestFacade.saveShareRequest(shareRequest);
		}

		return duplicateCheckResult;
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_CLIENT)
	public SormasToSormasEncryptedDataDto getDataForShareRequest(SormasToSormasEncryptedDataDto encryptedRequestUuid) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		String requestUuid = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedRequestUuid, String.class);
		ShareRequestInfo requestInfo = shareRequestInfoService.getByUuid(requestUuid);

		if (requestInfo == null) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasShare);
		}
		validateEntitiesBeforeSend(requestInfo);

		// update share request: add new samples, immunizations, etc.
		requestInfo.extractSharedMainEntities().forEach((e) -> updateShareRequestInfo(requestInfo, currentUser, (ADO) e));

		SormasToSormasDto shareData = shareDataBuilder.buildShareDataForRequest(requestInfo, currentUser);

		return sormasToSormasEncryptionEjb.signAndEncrypt(shareData, requestInfo.getShares().get(0).getOrganizationId());
	}

	private void shareEntities(List<String> entityUuids, SormasToSormasOptionsDto options) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();
		List<ADO> entities = getEntityService().getByUuids(entityUuids);

		validateEntitiesBeforeShare(entities, options.isHandOverOwnership(), options.isWithSamples(), options.getOrganization().getId(), false);
		ensureConsistentOptions(options);

		String requestUuid = DataHelper.createUuid();
		ShareRequestInfo requestInfo =
			createShareRequestInfoForEntities(requestUuid, ShareRequestStatus.ACCEPTED, options, entities, currentUser, false);
		SormasToSormasDto dataToSend = shareDataBuilder.buildShareDataForRequest(requestInfo, currentUser);

		SormasToSormasEncryptedDataDto encryptedResponse =
			sormasToSormasRestClient.post(options.getOrganization().getId(), saveEndpoint, dataToSend, SormasToSormasEncryptedDataDto.class);

		ShareRequestAcceptData responseData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedResponse, ShareRequestAcceptData.class);

		shareRequestInfoService.ensurePersisted(requestInfo);

		requestInfo.getShares().forEach(s -> {
			if (s.getCaze() != null) {
				sormasToSormasEntitiesHelper.updateSentCaseResponsibleDistrict(s.getCaze(), responseData.getDistrictExternalId());
			}
			if (s.getContact() != null) {
				sormasToSormasEntitiesHelper.updateSentContactResponsibleDistrict(s.getContact(), responseData.getDistrictExternalId());
			}

			if (s.getSample() != null) {
				sormasToSormasEntitiesHelper.updateSampleOnShare(s.getSample(), s);
			}

			if (s.getSurveillanceReport() != null) {
				sormasToSormasEntitiesHelper.updateSurveillanceReportOnShare(s.getSurveillanceReport(), s);
			}
		});
		entities.forEach(e -> {
			SormasToSormasOriginInfo entityOriginInfo = e.getSormasToSormasOriginInfo();
			if (entityOriginInfo != null) {
				entityOriginInfo.setOwnershipHandedOver(!options.isHandOverOwnership());
				originInfoService.ensurePersisted(entityOriginInfo);
			}
		});

		try {
			shareInfoService.handleOwnershipChangeInExternalSurvTool(requestInfo);
		} catch (ExternalSurveillanceToolException e) {
			LOGGER.error("Failed to delete shared entities in external surveillance tool", e);

			throw SormasToSormasException.fromStringPropertyWithWarning(Strings.errorSormasToSormasDeleteFromExternalSurveillanceTool);
		}
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_CLIENT)
	public SormasToSormasEncryptedDataDto saveSharedEntities(SormasToSormasEncryptedDataDto encryptedData)
		throws SormasToSormasException, SormasToSormasValidationException {
		MutableBoolean withOwnership = new MutableBoolean(false);
		decryptAndPersist(encryptedData, (data, existingData) -> {
			originInfoFacade.saveOriginInfo(data.getOriginInfo());
			processedEntitiesPersister.persistSharedData(data, data.getOriginInfo(), existingData);
			withOwnership.setValue(data.getOriginInfo().isOwnershipHandedOver());

			return null;
		});

		return sormasToSormasEncryptionEjb.signAndEncrypt(
			new ShareRequestAcceptData(null, withOwnership.isTrue() ? configFacadeEjb.getS2SConfig().getDistrictExternalId() : null),
			encryptedData.getSenderId());
	}

	@Override
	@PermitAll
	public void syncShares(ShareTreeCriteria criteria) {
		User currentUser = userService.getCurrentUser();

		walkShareTree(criteria, (entity, originInfo, parentCriteria) -> {
			// prevent stopping the iteration through the shares because of a failed sync operation
			// sync with as much servers as possible
			try {
				syncEntityToOrigin(entity, originInfo, parentCriteria);
			} catch (Exception e) {
				LOGGER.error("Failed to sync to [{}]", originInfo.getOrganizationId(), e);
			}
		}, ((entity, shareInfo, reShareCriteria, noForward) -> {
			// prevent stopping the iteration through the shares because of a failed sync operation
			// sync with as much servers as possible
			try {
				if (!noForward) {
					ShareRequestInfo latestRequestInfo = ShareInfoHelper.getLatestAcceptedRequest(shareInfo.getRequests().stream()).orElse(null);
					syncEntityToShares(entity, latestRequestInfo, reShareCriteria, currentUser);
				} else {
					ShareRequestInfo latestRequestInfo = ShareInfoHelper.getLatestRequest(shareInfo.getRequests().stream()).orElse(null);
					updateShareRequestInfo(latestRequestInfo, currentUser, entity);
				}
			} catch (Exception e) {
				LOGGER.error("Failed to sync to [{}]", shareInfo.getOrganizationId(), e);
			}
		}));
	}

	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_CLIENT)
	public void saveSyncedEntity(SormasToSormasEncryptedDataDto encryptedData) throws SormasToSormasException, SormasToSormasValidationException {
		SyncDataDto syncData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, SyncDataDto.class);

		ShareDataExistingEntities existingEntities = loadExistingEntities(syncData.getShareData());
		persist(syncData.getShareData(), (data, existinCase) -> {
			processedEntitiesPersister.persistSyncData(data, syncData.getShareData().getOriginInfo(), syncData.getCriteria(), existingEntities);

			return null;
		});
	}

	/**
	 * Called by frontend to get all the shares of an object
	 * 
	 * @param uuid
	 *            of the object
	 * @return list of share trees (shares and shares of shares)
	 */
	@Override
	@PermitAll
	public List<SormasToSormasShareTree> getAllShares(String uuid) {
		return getShareTrees(new ShareTreeCriteria(uuid), true);
	}

	/**
	 * Called only by the backend to collect all shares from different servers
	 * 
	 * @param encryptedCriteria
	 *            criteria containing uuid of shared object
	 * @return list of share trees (shares and shares of shares)
	 * @throws SormasToSormasException
	 *             if unexpected arror happens
	 */
	@Override
	@RightsAllowed(UserRight._SORMAS_TO_SORMAS_CLIENT)
	public SormasToSormasEncryptedDataDto getShareTrees(SormasToSormasEncryptedDataDto encryptedCriteria) throws SormasToSormasException {
		ShareTreeCriteria criteria = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedCriteria, ShareTreeCriteria.class);

		List<SormasToSormasShareTree> shares = getShareTrees(criteria, false);

		return sormasToSormasEncryptionEjb.signAndEncrypt(shares, encryptedCriteria.getSenderId());
	}

	@Override
	@PermitAll
	public boolean hasPendingRequest(List<String> entityUuids) {
		return shareInfoService.hasPendingRequest(getShareInfoAssociatedObjectField(), entityUuids);
	}

	protected abstract String getShareInfoAssociatedObjectField();

	private <T> T decryptAndPersist(SormasToSormasEncryptedDataDto encryptedData, Persister<T> persister)
		throws SormasToSormasException, SormasToSormasValidationException {

		SormasToSormasDto receivedData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, SormasToSormasDto.class);

		return persist(receivedData, persister);
	}

	private <T> T persist(@Valid SormasToSormasDto receivedData, Persister<T> persister) throws SormasToSormasValidationException {
		ShareDataExistingEntities existingEntities = loadExistingEntities(receivedData);
		List<ValidationErrors> validationErrors = receivedEntitiesProcessor.processReceivedData(receivedData, existingEntities);

		if (!validationErrors.isEmpty()) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return persister.call(receivedData, existingEntities);
	}

	protected interface Persister<T> {

		T call(SormasToSormasDto data, ShareDataExistingEntities existingEntities) throws SormasToSormasValidationException;
	}

	private ShareDataExistingEntities loadExistingEntities(SormasToSormasDto receivedData) {
		Map<String, Case> existingCases = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getCases())) {
			existingCases = caseService
				.getByUuids(
					receivedData.getCases().stream().map(SormasToSormasCaseDto::getEntity).map(CaseDataDto::getUuid).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(Case::getUuid, Function.identity()));
		}

		Map<String, Contact> existingContacts = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getContacts())) {
			existingContacts = contactService.getByUuids(
				receivedData.getContacts().stream().map(SormasToSormasContactDto::getEntity).map(ContactDto::getUuid).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(Contact::getUuid, Function.identity()));
		}

		Map<String, Event> existingEvents = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getEvents())) {
			existingEvents = eventService
				.getByUuids(
					receivedData.getEvents().stream().map(SormasToSormasEventDto::getEntity).map(EventDto::getUuid).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(Event::getUuid, Function.identity()));
		}

		Map<String, EventParticipant> existingEventParticipants = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getEventParticipants())) {
			existingEventParticipants =
				eventParticipantService
					.getByUuids(
						receivedData.getEventParticipants()
							.stream()
							.map(SormasToSormasEventParticipantDto::getEntity)
							.map(EventParticipantDto::getUuid)
							.collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(EventParticipant::getUuid, Function.identity()));
		}

		Map<String, Sample> existingSamples = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getSamples())) {
			existingSamples = sampleService
				.getByUuids(
					receivedData.getSamples().stream().map(SormasToSormasSampleDto::getEntity).map(SampleDto::getUuid).collect(Collectors.toList()))
				.stream()
				.collect(Collectors.toMap(Sample::getUuid, Function.identity()));
		}

		Map<String, Immunization> existingImmunizations = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getImmunizations())) {
			existingImmunizations =
				immunizationService
					.getByUuids(
						receivedData.getImmunizations()
							.stream()
							.map(SormasToSormasEntityDto::getEntity)
							.map(ImmunizationDto::getUuid)
							.collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(Immunization::getUuid, Function.identity()));
		}

		Map<String, SurveillanceReport> existingReports = Collections.emptyMap();
		if (CollectionUtils.isNotEmpty(receivedData.getSurveillanceReports())) {
			existingReports =
				surveillanceReportService
					.getByUuids(
						receivedData.getSurveillanceReports()
							.stream()
							.map(SormasToSormasEntityDto::getEntity)
							.map(SurveillanceReportDto::getUuid)
							.collect(Collectors.toList()))
					.stream()
					.collect(Collectors.toMap(SurveillanceReport::getUuid, Function.identity()));
		}

		return new ShareDataExistingEntities(
			existingCases,
			existingContacts,
			existingEvents,
			existingEventParticipants,
			existingSamples,
			existingImmunizations,
			existingReports);
	}

	private ValidationErrorGroup buildEntityValidationGroupName(String uuid) {
		return buildValidationGroupName(entityCaptionTag, uuid);
	}

	protected abstract AbstractCoreAdoService<ADO, ? extends QueryJoins> getEntityService();

	protected abstract Class<S[]> getShareDataClass();

	protected void validateEntitiesBeforeShare(
		List<ADO> entities,
		boolean handOverOwnership,
		boolean isWithSamples,
		String targetOrganizationId,
		boolean pendingRequestAllowed)
		throws SormasToSormasException {

		List<ValidationErrors> validationErrors = new ArrayList<>();
		for (ADO ado : entities) {
			if (!getEntityService().isEditAllowed(ado)) {
				validationErrors.add(
					new ValidationErrors(
						buildEntityValidationGroupNameForAdo(ado),
						ValidationErrors
							.create(new ValidationErrorGroup(entityCaptionTag), new ValidationErrorMessage(Validations.sormasToSormasNotEditable))));
			}

			if (!pendingRequestAllowed) {
				SormasToSormasShareInfo shareInfo = getByTypeAndOrganization(ado, targetOrganizationId);
				if (shareInfo != null) {
					ShareRequestInfo latestShare =
						ShareInfoHelper.getLatestRequest(shareInfo.getRequests().stream()).orElseGet(ShareRequestInfo::new);

					if (latestShare.getRequestStatus() == ShareRequestStatus.PENDING) {
						validationErrors.add(
							new ValidationErrors(
								buildEntityValidationGroupNameForAdo(ado),
								ValidationErrors.create(
									new ValidationErrorGroup(entityCaptionTag),
									new ValidationErrorMessage(Validations.sormasToSormasExistingPendingRequest))));
					}
				}
			}

			// run type specific S2S validation checks
			validateEntitiesBeforeShareInner(ado, handOverOwnership, isWithSamples, targetOrganizationId, validationErrors);
		}

		if (!validationErrors.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}

	}

	protected abstract void validateEntitiesBeforeShareInner(
		ADO ado,
		boolean handOverOwnership,
		boolean isWithSamples,
		String targetOrganizationId,
		List<ValidationErrors> validationErrors);

	protected abstract SormasToSormasShareInfo getByTypeAndOrganization(ADO ado, String targetOrganizationId);

	protected abstract ValidationErrorGroup buildEntityValidationGroupNameForAdo(ADO ado);

	protected void validateEntitiesBeforeSend(ShareRequestInfo shareRequestInfo) throws SormasToSormasException {
		List<SormasToSormasShareInfo> shares = shareRequestInfo.getShares();
		validateEntitiesBeforeShare(
			shares.stream().map(this::extractFromShareInfo).filter(Objects::nonNull).collect(Collectors.toList()),
			shares.get(0).isOwnershipHandedOver(),
			shareRequestInfo.isWithSamples(),
			shares.get(0).getOrganizationId(),
			true);
	}

	protected abstract ADO extractFromShareInfo(SormasToSormasShareInfo shareInfo);

	protected abstract void validateShareRequestBeforeAccept(SormasToSormasShareRequestDto shareRequest) throws SormasToSormasException;

	private void syncEntityToShares(ADO entity, ShareRequestInfo requestInfo, ShareTreeCriteria criteria, User currentUser)
		throws SormasToSormasException {
		SormasToSormasOptionsDto options = dataBuilderHelper.createOptionsFormShareRequestInfo(requestInfo);
		List<SormasToSormasShareInfo> shares = getOrCreateShareInfos(entity, options, currentUser, true);

		SormasToSormasDto shareData =
			shareDataBuilder.buildShareData(shares, dataBuilderHelper.createSormasToSormasOriginInfo(currentUser, requestInfo), requestInfo);

		sormasToSormasRestClient.post(options.getOrganization().getId(), syncEndpoint, new SyncDataDto(shareData, criteria), null);

		// add new shares to the original request
		requestInfo.getShares().addAll(shares.stream().filter(s -> s.getId() == null).collect(Collectors.toList()));
		shareRequestInfoService.ensurePersisted(requestInfo);
	}

	private void syncEntityToOrigin(ADO entity, SormasToSormasOriginInfo originInfo, ShareTreeCriteria criteria) throws SormasToSormasException {
		User currentUser = userService.getCurrentUser();

		SormasToSormasOptionsDto options = dataBuilderHelper.createOptionsFromOriginInfoDto(originInfo);
		options.setHandOverOwnership(false);

		ShareRequestInfo shareRequestInfo = createShareRequestInfoForEntities(
			DataHelper.createUuid(),
			ShareRequestStatus.ACCEPTED,
			options,
			Collections.singletonList(entity),
			currentUser,
			true);

		SormasToSormasDto shareData = shareDataBuilder
			.buildShareData(shareRequestInfo.getShares(), dataBuilderHelper.createSormasToSormasOriginInfo(currentUser, options), shareRequestInfo);

		sormasToSormasRestClient.post(originInfo.getOrganizationId(), syncEndpoint, new SyncDataDto(shareData, criteria), null);

		// remove existing shares from the request info
		shareRequestInfo.setShares(
			shareRequestInfo.getShares()
				.stream()
				.filter(s -> s.getSharedEntity().getSormasToSormasOriginInfo() == null && s.getId() == null)
				.collect(Collectors.toList()));
		if (!shareRequestInfo.getShares().isEmpty()) {
			shareRequestInfoService.ensurePersisted(shareRequestInfo);
		}
	}

	private void updateShareRequestInfo(ShareRequestInfo requestInfo, User currentUser, ADO entity) {

		SormasToSormasOptionsDto options = dataBuilderHelper.createOptionsFormShareRequestInfo(requestInfo);
		List<SormasToSormasShareInfo> shares = getOrCreateShareInfos(entity, options, currentUser, true);

		// add new shares to the original request
		requestInfo.getShares().addAll(shares.stream().filter(s -> s.getId() == null).collect(Collectors.toList()));
		shareRequestInfoService.ensurePersisted(requestInfo);
	}

	private SormasToSormasShareRequestDto createShareRequest(ShareRequestData shareData) {
		SormasToSormasShareRequestDto request = SormasToSormasShareRequestDto.build();
		request.setUuid(shareData.getRequestUuid());
		request.setDataType(shareRequestDataType);
		request.setStatus(ShareRequestStatus.PENDING);

		request.setOriginInfo(shareData.getOriginInfo());

		ShareRequestPreviews previews = shareData.getPreviews();
		request.setCases(previews.getCases());
		request.setContacts(previews.getContacts());

		request.setEvents(previews.getEvents());
		request.setEventParticipants(previews.getEventParticipants());

		return request;
	}

	protected void validateOwnership(List<ADO> entities) throws SormasToSormasException {
		List<String> notOwnedUuids = getUuidsWithPendingOwnershipHandedOver(entities);
		if (!notOwnedUuids.isEmpty()) {

			List<ValidationErrors> errors = notOwnedUuids.stream()
				.map(
					uuid -> new ValidationErrors(
						buildEntityValidationGroupName(uuid),
						ValidationErrors.create(
							new ValidationErrorGroup(entityCaptionTag),
							new ValidationErrorMessage(Validations.sormasToSormasOwnershipAlreadyHandedOver))))
				.collect(Collectors.toList());

			throw SormasToSormasException.fromStringProperty(errors, Strings.errorSormasToSormasShare);
		}
	}

	protected abstract List<String> getUuidsWithPendingOwnershipHandedOver(List<ADO> entities);

	private List<SormasToSormasShareTree> getShareTrees(ShareTreeCriteria criteria, boolean rootCall) {
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
						reShares,
						rootCall));
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
		List<SormasToSormasShareInfo> entityShares = entity.getSormasToSormasShares();

		if (!criteria.isForwardOnly() && originInfo != null && !originInfo.getOrganizationId().equals(criteria.getExceptedOrganizationId())) {
			SormasToSormasShareRequest shareRequest = originInfo.getRequest();
			String ownOrganizationId = configFacadeEjb.getS2SConfig().getId();

			if (shareRequest == null || shareRequest.getStatus() == ShareRequestStatus.ACCEPTED) {
				walkParent.walk(entity, originInfo, new ShareTreeCriteria(criteria.getEntityUuid(), ownOrganizationId, false));
			}
		}

		for (SormasToSormasShareInfo s : entityShares) {
			boolean notAccepted = s.getRequests().stream().noneMatch(r -> r.getRequestStatus() == ShareRequestStatus.ACCEPTED);
			boolean noForward = notAccepted || s.getOrganizationId().equals(criteria.getExceptedOrganizationId());
			if (originInfo != null) {
				noForward = noForward || s.getOrganizationId().equals(originInfo.getOrganizationId());
			}

			walkReShare.walk(entity, s, new ShareTreeCriteria(criteria.getEntityUuid(), criteria.getExceptedOrganizationId(), true), noForward);
		}
	}

	protected interface WalkParent<ADO> {

		void walk(ADO entity, SormasToSormasOriginInfo originInfo, ShareTreeCriteria criteria);
	}

	protected interface WalkReShare<ADO> {

		void walk(ADO entity, SormasToSormasShareInfo shareInfo, ShareTreeCriteria criteria, boolean noForward);
	}

	private SormasToSormasShareTree findParentShare(List<SormasToSormasShareTree> shares, String ownOrganizationId) {
		for (SormasToSormasShareTree shareTree : shares) {
			if (shareTree.getShare().getTargetDescriptor().getId().equals(ownOrganizationId)) {
				return shareTree;
			} else if (!shareTree.getReShares().isEmpty()) {
				SormasToSormasShareTree subTree = findParentShare(shareTree.getReShares(), ownOrganizationId);

				if (subTree != null) {
					return subTree;
				}
			}
		}

		return null;
	}

	private ShareRequestData processShareRequest(SormasToSormasEncryptedDataDto encryptedData)
		throws SormasToSormasException, SormasToSormasValidationException {
		ShareRequestData shareData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedData, ShareRequestData.class);

		List<ValidationErrors> validationErrors = receivedEntitiesProcessor.processReceivedRequest(shareData);

		if (!validationErrors.isEmpty()) {
			throw new SormasToSormasValidationException(validationErrors);
		}

		return shareData;
	}

	private ShareRequestInfo createShareRequestInfoForEntities(
		String requestUuid,
		ShareRequestStatus requestStatus,
		SormasToSormasOptionsDto options,
		List<ADO> entities,
		User currentUser,
		boolean forSync) {

		return createShareRequestInfoForShares(
			requestUuid,
			requestStatus,
			options,
			entities.stream()
				.map(e -> getOrCreateShareInfos(e, options, currentUser, forSync))
				.flatMap(Collection::stream)
				.collect(Collectors.toList()),
			currentUser);
	}

	private ShareRequestInfo createShareRequestInfoForShares(
		String requestUuid,
		ShareRequestStatus requestStatus,
		SormasToSormasOptionsDto options,
		List<SormasToSormasShareInfo> shares,
		User currentUser) {
		ShareRequestInfo requestInfo = new ShareRequestInfo();
		requestInfo.setUuid(requestUuid);
		requestInfo.setDataType(shareRequestDataType);
		requestInfo.setRequestStatus(requestStatus);
		requestInfo.setOwnershipHandedOver(options.isHandOverOwnership());

		addOptionsToShareRequestInfo(requestInfo, options, currentUser);
		shares.forEach(s -> s.setOwnershipHandedOver(options.isHandOverOwnership()));

		requestInfo.setShares(shares);

		return requestInfo;
	}

	protected abstract List<SormasToSormasShareInfo> getOrCreateShareInfos(ADO entity, SormasToSormasOptionsDto options, User user, boolean forSync);

	private void addOptionsToShareRequestInfo(ShareRequestInfo requestInfo, SormasToSormasOptionsDto options, User currentUser) {
		requestInfo.setSender(currentUser);
		requestInfo.setWithAssociatedContacts(options.isWithAssociatedContacts());
		requestInfo.setWithSamples(options.isWithSamples());
		requestInfo.setWithEventParticipants(options.isWithEventParticipants());
		requestInfo.setWithImmunizations(options.isWithImmunizations());
		requestInfo.setWithSurveillanceReports(options.isWithSurveillanceReports());
		requestInfo.setPseudonymizedPersonalData(options.isPseudonymizeData());
		requestInfo.setPseudonymizedSensitiveData(options.isPseudonymizeData());
		requestInfo.setComment(options.getComment());
	}
}
