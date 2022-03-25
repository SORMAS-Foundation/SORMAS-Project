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

import de.symeda.sormas.api.externalsurveillancetool.ExternalSurveillanceToolException;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.sormastosormas.SormasServerDescriptor;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEncryptedDataDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasEntityInterface;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasFacade;
import de.symeda.sormas.api.sormastosormas.sharerequest.RequestResponseDataDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.common.ConfigFacadeEjb;
import de.symeda.sormas.backend.contact.Contact;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
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
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareRequestInfoService;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;
import de.symeda.sormas.backend.sormastosormas.share.sharerequest.SormasToSormasShareRequestFacadeEJB.SormasToSormasShareRequestFacadeEJBLocal;
import de.symeda.sormas.backend.user.UserService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants.RESOURCE_PATH;

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
	@EJB
	private SormasToSormasDiscoveryService sormasToSormasDiscoveryService;
	@Inject
	private SormasToSormasRestClient sormasToSormasRestClient;
	@EJB
	private SormasToSormasShareRequestFacadeEJBLocal shareRequestFacade;
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

	@Override
	public String getOrganizationId() {
		return configFacadeEjb.getS2SConfig().getId();
	}

	@Override
	public List<SormasServerDescriptor> getAllAvailableServers() {
		return sormasToSormasDiscoveryService.getAllAvailableServers();
	}

	@Override
	public SormasServerDescriptor getSormasServerDescriptorById(String id) {
		return sormasToSormasDiscoveryService.getSormasServerDescriptorById(id);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void rejectRequest(ShareRequestDataType dataType, String uuid, String comment) throws SormasToSormasException {
		SormasToSormasShareRequestDto shareRequest = shareRequestFacade.getShareRequestByUuid(uuid);

		if (shareRequest.getStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRejectNotPending);
		}

		String organizationId = shareRequest.getOriginInfo().getOrganizationId();
		sormasToSormasRestClient.post(organizationId, REJECT_REQUEST_ENDPOINT, new RequestResponseDataDto(uuid, comment), null);

		shareRequest.setChangeDate(new Date());
		shareRequest.setRejected(comment);

		shareRequestFacade.saveShareRequest(shareRequest);
	}

	@Override
	@Transactional(rollbackOn = {
		Exception.class })
	public void requestRejected(SormasToSormasEncryptedDataDto encryptedRejectData) throws SormasToSormasException {
		RequestResponseDataDto rejectData = sormasToSormasEncryptionEjb.decryptAndVerify(encryptedRejectData, RequestResponseDataDto.class);
		ShareRequestInfo requestInfo = shareRequestInfoService.getByUuid(rejectData.getRequestUuid());

		if (requestInfo == null || requestInfo.getRequestStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRejectNotPending);
		}

		requestInfo.setRequestStatus(ShareRequestStatus.REJECTED);
		requestInfo.setResponseComment(rejectData.getComment());
		requestInfo.getShares().forEach(s -> s.setOwnershipHandedOver(false));

		shareRequestInfoService.ensurePersisted(requestInfo);
	}

	@Override
	public void acceptShareRequest(ShareRequestDataType dataType, String uuid) throws SormasToSormasException, SormasToSormasValidationException {
		getEntityInterface(dataType).acceptShareRequest(uuid);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public void revokeShare(String shareInfoUuid) throws SormasToSormasException {
		SormasToSormasShareInfo shareInfo = shareInfoService.getByUuid(shareInfoUuid);

		List<ShareRequestInfo> pendingRequests =
			shareInfo.getRequests().stream().filter(r -> r.getRequestStatus() == ShareRequestStatus.PENDING).collect(Collectors.toList());

		if (pendingRequests.isEmpty()) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRevokeNotPending);
		}

		for (ShareRequestInfo request : pendingRequests) {
			sormasToSormasRestClient.post(shareInfo.getOrganizationId(), REVOKE_REQUEST_ENDPOINT, Collections.singletonList(request.getUuid()), null);

			request.setRequestStatus(ShareRequestStatus.REVOKED);
			shareInfoService.ensurePersisted(shareInfo);
		}
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
	public void requestsRevoked(SormasToSormasEncryptedDataDto encryptedRequestUuids) throws SormasToSormasException {
		String[] requestUuids = encryptionService.decryptAndVerify(encryptedRequestUuids, String[].class);
		List<SormasToSormasShareRequestDto> shareRequests = shareRequestFacade.getShareRequestsByUuids(Arrays.asList(requestUuids));

		if (shareRequests.stream().anyMatch(r -> r.getStatus() != ShareRequestStatus.PENDING)) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasRevokeNotPending);
		}

		shareRequests.forEach(shareRequest -> {
			shareRequest.setChangeDate(new Date());
			shareRequest.setRevoked();
			shareRequestFacade.saveShareRequest(shareRequest);
		});
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
	public void requestAccepted(SormasToSormasEncryptedDataDto encryptedAcceptData) throws SormasToSormasException {
		ShareRequestAcceptData acceptData = encryptionService.decryptAndVerify(encryptedAcceptData, ShareRequestAcceptData.class);

		ShareRequestInfo requestInfo = shareRequestInfoService.getByUuid(acceptData.getRequestUuid());

		if (requestInfo.getRequestStatus() != ShareRequestStatus.PENDING) {
			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasAcceptNotPending);
		}

		requestInfo.setRequestStatus(ShareRequestStatus.ACCEPTED);
		requestInfo.getShares().forEach(s -> {
			updateCaseOnShareAccepted(s.getCaze(), s, acceptData.getDistrictExternalId());
			updateContactOnShareAccepted(s.getContact(), s, acceptData.getDistrictExternalId());
			updateOriginInfoOnShareAccepted(s.getEvent(), s);
			updateOriginInfoOnShareAccepted(s.getEventParticipant(), s);
			updateOriginInfoOnShareAccepted(s.getSample(), s);
			updateOriginInfoOnShareAccepted(s.getImmunization(), s);
		});
		shareRequestInfoService.ensurePersisted(requestInfo);

		try {
			shareInfoService.handleOwnershipChangeInExternalSurvTool(requestInfo);
		} catch (ExternalSurveillanceToolException e) {
			LOGGER.error("Failed to delete shared entities in external surveillance tool", e);

			throw SormasToSormasException.fromStringProperty(Strings.errorSormasToSormasAccept);
		}
	}

	private void updateCaseOnShareAccepted(Case caze, SormasToSormasShareInfo shareInfo, String districtExternalId) {
		if(caze != null) {
			updateOriginInfoOnShareAccepted(caze, shareInfo);
			sormasToSormasEntitiesHelper.updateCaseResponsibleDistrict(caze, districtExternalId);
		}
	}

	private void updateContactOnShareAccepted(Contact contact, SormasToSormasShareInfo shareInfo, String districtExternalId) {
		if(contact != null) {
			updateOriginInfoOnShareAccepted(contact, shareInfo);
			sormasToSormasEntitiesHelper.updateContactResponsibleDistrict(contact, districtExternalId);
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
	public boolean isFeatureEnabledForUser() {
		return userService.hasRight(UserRight.SORMAS_TO_SORMAS_SHARE) && isFeatureConfigured();
	}

	@Override
	public boolean isFeatureConfigured() {
		return !StringUtils.isEmpty(configFacadeEjb.getS2SConfig().getPath());
	}

	@Override
	public boolean isSharingCasesContactsAndSamplesEnabledForUser() {
		return isFeatureEnabledForUser()
			&& featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_CASES_WITH_CONTACTS_AND_SAMPLES);
	}

	@Override
	public boolean isSharingEventsEnabledForUser() {
		return isFeatureEnabledForUser() && featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_EVENTS);
	}

	@Override
	public boolean isSharingLabMessagesEnabledForUser() {
		return isFeatureEnabledForUser() && featureConfigurationFacade.isFeatureEnabled(FeatureType.SORMAS_TO_SORMAS_SHARE_LAB_MESSAGES);
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
