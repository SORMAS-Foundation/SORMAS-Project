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

package de.symeda.sormas.backend.sormastosormas.data.processed;

import javax.transaction.Transactional;

import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.validation.SormasToSormasValidationException;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.backend.sormastosormas.entities.SormasToSormasShareable;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

public abstract class ProcessedDataPersister<T extends SormasToSormasShareableDto, S extends de.symeda.sormas.api.sormastosormas.SormasToSormasEntityDto<T>, E extends SormasToSormasShareable> {

	protected abstract SormasToSormasShareInfoService getShareInfoService();

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSharedData(S processedData, SormasToSormasOriginInfoDto originInfo, E existingEntity)
		throws SormasToSormasValidationException {
		// create or update origin info
		if (existingEntity == null || existingEntity.getSormasToSormasOriginInfo() != null) {
			processedData.getEntity().setSormasToSormasOriginInfo(originInfo);
		}

		// update existing shares
		if (originInfo.isOwnershipHandedOver() && existingEntity != null && !existingEntity.getSormasToSormasShares().isEmpty()) {
			existingEntity.getSormasToSormasShares().forEach(s -> {
				s.setOwnershipHandedOver(false);
				getShareInfoService().ensurePersisted(s);
			});
		}

		persistSharedData(processedData, existingEntity);
	}

	protected abstract void persistSharedData(S processedData, E existingEntity) throws SormasToSormasValidationException;

	@Transactional(rollbackOn = {
		Exception.class })
	public void persistSyncData(S processedData, SormasToSormasOriginInfoDto originInfo, E existingEntity) throws SormasToSormasValidationException {
		T entity = processedData.getEntity();
		SormasToSormasShareInfo shareInfo = getShareInfoByEntityAndOrganization(entity, originInfo.getOrganizationId());

		if (shareInfo == null) {
			entity.setSormasToSormasOriginInfo(originInfo);
		}

		persistSharedData(processedData, existingEntity);
	}

	protected abstract SormasToSormasShareInfo getShareInfoByEntityAndOrganization(T entity, String organizationId);
}
