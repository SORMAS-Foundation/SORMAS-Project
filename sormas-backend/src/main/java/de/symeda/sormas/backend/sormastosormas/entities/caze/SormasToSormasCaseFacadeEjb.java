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
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.sormastosormas.SormasToSormasApiConstants;
import de.symeda.sormas.api.sormastosormas.SormasToSormasException;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorGroup;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrorMessage;
import de.symeda.sormas.api.sormastosormas.validation.ValidationErrors;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseDto;
import de.symeda.sormas.api.sormastosormas.caze.SormasToSormasCaseFacade;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestDataType;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.sharerequest.SormasToSormasShareRequestDto;
import de.symeda.sormas.backend.caze.Case;
import de.symeda.sormas.backend.caze.CaseFacadeEjb.CaseFacadeEjbLocal;
import de.symeda.sormas.backend.caze.CaseService;
import de.symeda.sormas.backend.common.BaseAdoService;
import de.symeda.sormas.backend.sormastosormas.AbstractSormasToSormasInterface;
import de.symeda.sormas.backend.sormastosormas.data.processed.ProcessedDataPersister;
import de.symeda.sormas.backend.sormastosormas.data.received.ReceivedDataProcessor;
import de.symeda.sormas.backend.sormastosormas.share.ShareDataBuilder;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.ShareInfoCase;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless(name = "SormasToSormasCaseFacade")
public class SormasToSormasCaseFacadeEjb
	extends AbstractSormasToSormasInterface<Case, CaseDataDto, SormasToSormasCaseDto, SormasToSormasCasePreview, ProcessedCaseData>
	implements SormasToSormasCaseFacade {

	public static final String CASE_REQUEST_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_REQUEST_ENDPOINT;
	public static final String CASE_REQUEST_REJECT_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_REQUEST_REJECT_ENDPOINT;
	public static final String CASE_REQUEST_GET_DATA_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_REQUEST_GET_DATA_ENDPOINT;
	public static final String SAVE_SHARED_CASE_ENDPOINT = RESOURCE_PATH + CASE_ENDPOINT;
	public static final String SYNC_CASE_ENDPOINT = RESOURCE_PATH + CASE_SYNC_ENDPOINT;
	public static final String CASE_SHARES_ENDPOINT = RESOURCE_PATH + SormasToSormasApiConstants.CASE_SHARES_ENDPOINT;

	@EJB
	private CaseService caseService;
	@EJB
	private CaseShareDataBuilder caseShareDataBuilder;
	@EJB
	private ReceivedCaseProcessor receivedCaseProcessor;
	@EJB
	private ProcessedCaseDataPersister processedCaseDataPersister;
	@EJB
	private CaseFacadeEjbLocal caseFacade;
	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	public SormasToSormasCaseFacadeEjb() {
		super(
			CASE_REQUEST_ENDPOINT,
			CASE_REQUEST_REJECT_ENDPOINT,
			CASE_REQUEST_GET_DATA_ENDPOINT,
			SAVE_SHARED_CASE_ENDPOINT,
			SYNC_CASE_ENDPOINT,
			CASE_SHARES_ENDPOINT,
			Captions.CaseData,
			ShareRequestDataType.CASE,
			CaseShareRequestData.class,
			CaseSyncData.class);
	}

	@Override
	protected BaseAdoService<Case> getEntityService() {
		return caseService;
	}

	@Override
	protected ShareDataBuilder<Case, SormasToSormasCaseDto, SormasToSormasCasePreview> getShareDataBuilder() {
		return caseShareDataBuilder;
	}

	protected ReceivedDataProcessor<CaseDataDto, SormasToSormasCaseDto, ProcessedCaseData, SormasToSormasCasePreview> getReceivedDataProcessor() {
		return receivedCaseProcessor;
	}

	@Override
	protected ProcessedDataPersister<ProcessedCaseData> getProcessedDataPersister() {
		return processedCaseDataPersister;
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
				validationErrors.add(new ValidationErrors(
					buildCaseValidationGroupName(caze),
					ValidationErrors
						.create(new ValidationErrorGroup(Captions.CaseData), new ValidationErrorMessage(Validations.sormasToSormasNotEditable))));
			}
			if (handOverOwnership && caze.getPerson().isEnrolledInExternalJournal()) {
				validationErrors.add(new ValidationErrors(
					buildCaseValidationGroupName(caze),
					ValidationErrors
						.create(new ValidationErrorGroup(Captions.CaseData), new ValidationErrorMessage(Validations.sormasToSormasPersonEnrolled))));
			}
		}

		if (validationErrors.size() > 0) {
			throw SormasToSormasException.fromStringProperty(validationErrors, Strings.errorSormasToSormasShare);
		}
	}

	@Override
	protected ValidationErrors validateSharedEntity(CaseDataDto entity) {
		return validateSharedUuid(entity.getUuid());
	}

	@Override
	protected ValidationErrors validateSharedPreview(SormasToSormasCasePreview preview) {
		return validateSharedUuid(preview.getUuid());
	}

	@Override
	protected void addEntityToShareInfo(SormasToSormasShareInfo shareInfo, List<Case> cases) {
		shareInfo.getCases().addAll(cases.stream().map(c -> new ShareInfoCase(shareInfo, c)).collect(Collectors.toList()));
	}

	@Override
	protected SormasToSormasShareInfo getShareInfoByEntityAndOrganization(String entityUuid, String receiverId) {
		return shareInfoService.getByCaseAndOrganization(entityUuid, receiverId);
	}

	@Override
	protected List<CaseDataDto> loadExistingEntities(List<String> uuids) {
		return caseFacade.getByUuids(uuids);
	}

	@Override
	protected void setShareRequestPreviewData(SormasToSormasShareRequestDto request, List<SormasToSormasCasePreview> previews) {
		request.setCases(previews);
	}

	@Override
	protected List<SormasToSormasShareInfo> getEntityShares(Case caze) {
		return caze.getShareInfoCases().stream().map(ShareInfoCase::getShareInfo).collect(Collectors.toList());
	}

	private ValidationErrors validateSharedUuid(String uuid) {
		ValidationErrors errors = new ValidationErrors();
		if (caseFacade.exists(uuid)) {
			errors.add(new ValidationErrorGroup(Captions.CaseData), new ValidationErrorMessage(Validations.sormasToSormasCaseExists));
		}

		return errors;
	}

	@Override
	protected List<String> getUuidsWithPendingOwnershipHandedOver(List<Case> entities) {
		return shareInfoService.getCaseUuidsWithPendingOwnershipHandOver(entities);
	}

	@LocalBean
	@Stateless
	public static class SormasToSormasCaseFacadeEjbLocal extends SormasToSormasCaseFacadeEjb {

	}
}
