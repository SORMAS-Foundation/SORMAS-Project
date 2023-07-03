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

package de.symeda.sormas.backend.sormastosormas;

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.symeda.sormas.api.audit.AuditIgnore;
import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.DuplicateResult;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.entities.SormasToSormasEntityInterface;
import de.symeda.sormas.api.sormastosormas.share.incoming.RequestResponseDataDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.share.incoming.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DataHelper.Pair;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.surveillancereport.SurveillanceReport;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.externalmessage.ExternalMessageService;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.sample.Sample;
import de.symeda.sormas.backend.sormastosormas.access.SormasToSormasDiscoveryService;
import de.symeda.sormas.backend.sormastosormas.crypto.SormasToSormasEncryptionFacadeEjb.SormasToSormasEncryptionFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasEntitiesHelper;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.sormastosormas.entities.caze.SormasToSormasCaseFacadeEjb.SormasToSormasCaseFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.contact.SormasToSormasContactFacadeEjb.SormasToSormasContactFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.entities.event.SormasToSormasEventFacadeEjb.SormasToSormasEventFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfo;
import de.symeda.sormas.backend.sormastosormas.rest.SormasToSormasRestClient;
import de.symeda.sormas.backend.sormastosormas.share.ShareRequestAcceptData;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestFacadeEJB.SormasToSormasShareRequestFacadeEJBLocal;
import de.symeda.sormas.backend.sormastosormas.share.incoming.SormasToSormasShareRequestService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.ShareRequestInfoService;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.outgoing.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.user.UserService;
import de.symeda.sormas.backend.util.RightsAllowed;

@Stateless(name = "SormasToSormasFacade")
public class SormasToSormasFacadeEjb implements SormasToSormasFacade {

	private static final Logger LOGGER = LoggerFactory.getLogger(SormasToSormasFacadeEjb.class);

	private static final String REJECT_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.REJECT_REQUESTS_ENDPOINT;
	private static final String REVOKE_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.REVOKE_REQUESTS_ENDPOINT;

	@EJB
	private ShareRequestInfoService shareRequestInfoService;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;
	@EJB
	private UserService userService;
	@Inject
	private SormasToSormasDiscoveryService sormasToSormasDiscoveryService;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private SormasToSormasShareRequestFacadeEJBLocal shareRequestFacade;
	@EJB
	private SormasToSormasShareRequestService shareRequestService;
	@EJB
	private SormasToSormasCaseFacadeEjbLocal sormasToSormasCaseFacade;
	@EJB
	private SormasToSormasContactFacadeEjbLocal sormasToSormasContactFacade;
	@EJB
	private SormasToSormasEventFacadeEjbLocal sormasToSormasEventFacade;
	@EJB
	private SormasToSormasEncryptionFacadeEjbLocal encryptionService;
	@Inject
	private ConfigFacadeEjb.ConfigFacadeEjbLocal configFacadeEjb;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private SormasToSormasEncryptionFacadeEjbLocal sormasToSormasEncryptionEjb;
	@EJB
	private SormasToSormasEntitiesHelper sormasToSormasEntitiesHelper;
	@EJB
	private ExternalMessageService externalMessageService;

	@Override
	@PermitAll
	public String getOrganizationId() {
		return configFacadeEjb.getS2SConfig().getId();
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_SHARE })
	public List<SormasServerDescriptor> getAllAvailableServers() {
		return sormasToSormasDiscoveryService.getAllAvailableServers();
	}

	@Override
	@PermitAll
	public SormasServerDescriptor getSormasServerDescriptorById(String id) {
		return sormasToSormasDiscoveryService.getSormasServerDescriptorById(id);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public void rejectRequest(ShareRequestDataType dataType, String uuid, String comment) throws SormasToSormasException {
		SormasToSormasShareRequestDto shareRequest = shareRequestFacade.getShareRequestByUuid(uuid);

		if (shareRequest == null || shareRequest.getStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		String organizationId = shareRequest.getOriginInfo().getOrganizationId();
		sormasToSormasRestClient.post(organizationId, REJECT_REQUEST_ENDPOINT, new RequestResponseDataDto(uuid, comment), null);

		shareRequestService.deletePermanentByUuids(Collections.singletonList(shareRequest.getUuid()));
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public void requestRejected(SormasToSormasEncryptedDataDto encryptedRejectData) throws SormasToSormasException {
		RequestResponseDataDto rejectData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedRejectData, RequestResponseDataDto.class);
		ShareRequestInfo requestInfo = shareRequestInfoService.getByUuid(rejectData.getRequestUuid());

		if (requestInfo == null || requestInfo.getRequestStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		requestInfo.setRequestStatus(ShareRequestStatus.REJECTED);
		requestInfo.setResponseComment(rejectData.getComment());
		requestInfo.getShares().forEach(s -> s.setOwnershipHandedOver(false));

		shareRequestInfoService.ensurePersisted(requestInfo);
	}

	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public DuplicateResult acceptShareRequest(ShareRequestDataType dataType, String uuid, boolean checkDuplicates)
		throws SormasToSormasException, SormasToSormasValidationException {
		return getEntityInterface(dataType).acceptShareRequest(uuid, checkDuplicates);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_SHARE,
		UserRight._SORMAS_TO_SORMAS_PROCESS })
	public void revokeShare(String shareInfoUuid) throws SormasToSormasException {
		SormasToSormasShareInfo shareInfo = shareInfoService.getByUuid(shareInfoUuid);

		List<ShareRequestInfo> pendingRequests =
			shareInfo.getRequests().stream().filter(r -> r.getRequestStatus() == ShareRequestStatus.PENDING).collect(Collectors.toList());

		if (pendingRequests.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		for (ShareRequestInfo pendingRequest : pendingRequests) {
			revokeShareRequest(pendingRequest, shareInfo.getOrganizationId());
		}
	}

	private void revokeShareRequest(ShareRequestInfo request, String targetOrganizationId) throws SormasToSormasException {
		if (request.getRequestStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		sormasToSormasRestClient.post(targetOrganizationId, REVOKE_REQUEST_ENDPOINT, Collections.singletonList(request.getUuid()), null);

		request.setRequestStatus(ShareRequestStatus.REVOKED);
		shareRequestInfoService.ensurePersisted(request);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_SHARE })
	public void revokeShareRequest(String requestUuid) throws SormasToSormasException {
		ShareRequestInfo request = shareRequestInfoService.getByUuid(requestUuid);

		revokeShareRequest(request, request.getShares().get(0).getOrganizationId());
	}

	/**
	 * Called from the REST api when the sender revokes share requests
	 *
	 * @param encryptedRequestUuids
	 *            the uuids of requests
	 * @throws SormasToSormasException
	 *             in case of failure
	 */
	@Override
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public void requestsRevoked(SormasToSormasEncryptedDataDto encryptedRequestUuids) throws SormasToSormasException {
		String[] requestUuids = encryptionService.decryptAndVerify(encryptedRequestUuids, String[].class);
		List<SormasToSormasShareRequestDto> shareRequests = shareRequestFacade.getShareRequestsByUuids(Arrays.asList(requestUuids));

		if (shareRequests.stream().anyMatch(r -> r.getStatus() != ShareRequestStatus.PENDING)) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		shareRequestService.deletePermanentByUuids(shareRequests.stream().map(SormasToSormasShareRequestDto::getUuid).collect(Collectors.toList()));
	}

	/**
	 * Called from REST api when the receiver accepts a share request
	 *
	 * @param encryptedAcceptData
	 *            the uuid of the request
	 * @throws SormasToSormasException
	 *             in case of failure
	 */
	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	@RightsAllowed({
		UserRight._SORMAS_TO_SORMAS_CLIENT })
	public void requestAccepted(SormasToSormasEncryptedDataDto encryptedAcceptData) throws SormasToSormasException {
		ShareRequestAcceptData acceptData = encryptionService.decryptAndVerify(encryptedAcceptData, ShareRequestAcceptData.class);

		ShareRequestInfo requestInfo = shareRequestInfoService.getByUuid(acceptData.getRequestUuid());

		if (requestInfo.getRequestStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRequestProcessed);
		}

		requestInfo.setRequestStatus(ShareRequestStatus.ACCEPTED);
		requestInfo.getShares().forEach(s -> {
			updateCaseOnShareAccepted(s.getCaze(), s, acceptData.getDistrictExternalId());
			updateContactOnShareAccepted(s.getContact(), s, acceptData.getDistrictExternalId());
			updateOriginInfoOnShareAccepted(s.getEvent(), s);
			updateOriginInfoOnShareAccepted(s.getEventParticipant(), s);
			updateSampleOnShareAccepted(s);
			updateSurveillanceReportOnShareAccepted(s);
			updateOriginInfoOnShareAccepted(s.getImmunization(), s);
		});
		shareRequestInfoService.ensurePersisted(requestInfo);

		try {
			shareInfoService.handleOwnershipChangeInExternalSurvTool(requestInfo);
		} catch (ExternalSurveillanceToolException e) {
			LOGGER.error("Failed to delete shared entities in external surveillance tool", e);

			throw SormasToSormasException.fromStringProperty(
				Strings.unexpectedErrorSormasToSormasAccept,
				I18nProperties.getString(Strings.errorNotifyingExternalSurveillanceTool) + ": " + e.getMessage());
		}
	}

	private void updateCaseOnShareAccepted(Case caze, SormasToSormasShareInfo shareInfo, String districtExternalId) {
		if (caze != null) {
			updateOriginInfoOnShareAccepted(caze, shareInfo);
			sormasToSormasEntitiesHelper.updateSentCaseResponsibleDistrict(caze, districtExternalId);
		}
	}

	private void updateContactOnShareAccepted(Contact contact, SormasToSormasShareInfo shareInfo, String districtExternalId) {
		if (contact != null) {
			updateOriginInfoOnShareAccepted(contact, shareInfo);
			sormasToSormasEntitiesHelper.updateSentContactResponsibleDistrict(contact, districtExternalId);
		}
	}

	private void updateSampleOnShareAccepted(SormasToSormasShareInfo s) {
		Sample sample = s.getSample();

		if (sample != null) {
			updateOriginInfoOnShareAccepted(sample, s);
			sormasToSormasEntitiesHelper.updateSampleOnShare(s.getSample(), s);
		}
	}

	private void updateSurveillanceReportOnShareAccepted(SormasToSormasShareInfo s) {
		SurveillanceReport report = s.getSurveillanceReport();

		if (report != null) {
			updateOriginInfoOnShareAccepted(report, s);
			sormasToSormasEntitiesHelper.updateSurveillanceReportOnShare(s.getSurveillanceReport(), s);
		}
	}

	private void updateOriginInfoOnShareAccepted(SormasToSormasShareable entity, SormasToSormasShareInfo shareInfo) {
		if (entity != null) {
			SormasToSormasOriginInfo originInfo = entity.getSormasToSormasOriginInfo();
			if (originInfo != null) {
				originInfo.setOwnershipHandedOver(!shareInfo.isOwnershipHandedOver());
			}
		}
	}

	@Override
	@PermitAll
	public boolean isShareEnabledForUser() {
		return userService.hasRight(UserRight.SORMAS_TO_SORMAS_SHARE) && isFeatureConfigured();
	}

	@Override
	@PermitAll
	public boolean isProcessingShareEnabledForUser() {
		return userService.hasRight(UserRight.SORMAS_TO_SORMAS_PROCESS) && isFeatureConfigured();
	}

	@Override
	@PermitAll
	public boolean isFeatureConfigured() {
		return configFacadeEjb.isS2SConfigured();
	}

	@Override
	@PermitAll
	@AuditIgnore
	public boolean isAnyFeatureConfigured(FeatureType... sormasToSormasFeatures) {
		return configFacadeEjb.isS2SConfigured() && featureConfigurationFacade.isAnyFeatureEnabled(sormasToSormasFeatures);
	}

	@Override
	@PermitAll
	public boolean isSharingCasesEnabledForUser() {
		return isShareEnabledForUser() && featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_CASES);
	}

	@Override
	@PermitAll
	public boolean isSharingContactsEnabledForUser() {
		return isShareEnabledForUser() && featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_CONTACTS);
	}

	@Override
	@PermitAll
	public boolean isSharingEventsEnabledForUser() {
		return isShareEnabledForUser() && featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS);
	}

	@Override
	@PermitAll
	public boolean isSharingExternalMessagesEnabledForUser() {
		return isShareEnabledForUser() && featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_EXTERNAL_MESSAGES);
	}

	/**
	 *
	 * @param sormasToSormasShares
	 *            - tha shares to be checked
	 * @param doDelete
	 *            - whether to delete the revoked request or not - for automatic deletion it will be deleted separately
	 * @throws SormasToSormasException
	 *             when something goes wring during reject on the target system
	 */
	@RightsAllowed({
		UserRight._CASE_DELETE,
		UserRight._CASE_MERGE,
		UserRight._CONTACT_DELETE,
		UserRight._CONTACT_MERGE,
		UserRight._EVENT_DELETE,
		UserRight._SYSTEM })
	public void revokePendingShareRequests(List<SormasToSormasShareInfo> sormasToSormasShares, boolean doDelete) throws SormasToSormasException {
		List<Pair<ShareRequestInfo, String>> pendingRequests = sormasToSormasShares.stream()
			.map(
				s -> s.getRequests()
					.stream()
					.filter(r -> r.getRequestStatus() == ShareRequestStatus.PENDING)
					.map(r -> Pair.createPair(r, s.getOrganizationId()))
					.collect(Collectors.toList()))
			.flatMap(Collection::stream)
			.collect(Collectors.toList());

		for (Pair<ShareRequestInfo, String> requestAndOrganization : pendingRequests) {
			ShareRequestInfo request = requestAndOrganization.getElement0();
			String organizationId = requestAndOrganization.getElement1();

			revokeShareRequest(request, organizationId);

			if (doDelete) {
				shareRequestInfoService.deletePermanent(request);
			}
		}
	}

	private SormasToSormasEntityInterface getEntityInterface(ShareRequestDataType dataType) {
		switch (dataType) {
		case CASE:
			return sormasToSormasCaseFacade;
		case CONTACT:
			return sormasToSormasContactFacade;
		case EVENT:
			return sormasToSormasEventFacade;
		default:
			throw new RuntimeException("Unknown request [" + dataType + "]");
		}
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasFacadeEjbLocal extends SormasToSormasFacadeEjb {

	}
}
