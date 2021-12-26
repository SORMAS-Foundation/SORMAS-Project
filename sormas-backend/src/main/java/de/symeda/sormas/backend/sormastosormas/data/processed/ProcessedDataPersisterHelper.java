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

package de.symeda.sormas.backend.sormastosormas.data.processed;

import java.util.function.BiFunction;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import de.symeda.sormas.api.HasUuid;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoDto;
import de.symeda.sormas.api.sormastosormas.SormasToSormasOriginInfoFacade;
import de.symeda.sormas.api.sormastosormas.SormasToSormasShareableDto;
import de.symeda.sormas.backend.sormastosormas.origin.SormasToSormasOriginInfoFacadeEjb.SormasToSormasOriginInfoFacadeEjbLocal;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfo;
import de.symeda.sormas.backend.sormastosormas.share.shareinfo.SormasToSormasShareInfoService;

@Stateless
@LocalBean
public class ProcessedDataPersisterHelper {

	@EJB
	private SormasToSormasOriginInfoFacadeEjbLocal oriInfoFacade;

	@EJB
	private SormasToSormasShareInfoService shareInfoService;

	public <T extends SormasToSormasShareableDto, U extends SormasToSormasShareableDto> void sharedAssociatedEntityCallback(U parent, T entity) {
		entity.setSormasToSormasOriginInfo(parent.getSormasToSormasOriginInfo());
	}

	public ReturnedAssociatedEntityCallback createReturnedAssociatedEntityCallback(SormasToSormasOriginInfoDto originInfo) {
		return new ReturnedAssociatedEntityCallback(originInfo, oriInfoFacade, shareInfoService);
	}

	public static class ReturnedAssociatedEntityCallback {

		private boolean originInfoSaved;

		private final SormasToSormasOriginInfoDto originInfo;

		private final SormasToSormasOriginInfoFacade oriInfoFacade;
		private final SormasToSormasShareInfoService shareInfoService;

		public ReturnedAssociatedEntityCallback(
			SormasToSormasOriginInfoDto originInfo,
			SormasToSormasOriginInfoFacade oriInfoFacade,
			SormasToSormasShareInfoService shareInfoService) {
			this.originInfo = originInfo;
			this.oriInfoFacade = oriInfoFacade;
			this.shareInfoService = shareInfoService;
		}

		public <T extends SormasToSormasShareableDto> void apply(T entity, BiFunction<String, String, SormasToSormasShareInfo> findShareInfo) {
			if (entity.getSormasToSormasOriginInfo() != null) {
				entity.getSormasToSormasOriginInfo().setOwnershipHandedOver(false);
			} else {
				SormasToSormasShareInfo shareInfo = findShareInfo.apply(entity.getUuid(), originInfo.getOrganizationId());
				if (shareInfo == null) {
					if (!originInfoSaved) {
						oriInfoFacade.saveOriginInfo(originInfo);
						originInfoSaved = true;
					}

					entity.setSormasToSormasOriginInfo(originInfo);
				} else {
					shareInfo.setOwnershipHandedOver(false);
					shareInfoService.persist(shareInfo);
				}
			}
		}
	}

	public static class SyncedAssociatedEntityCallback {

		private boolean originInfoSaved;

		private final SormasToSormasOriginInfoDto originInfo;

		private final SormasToSormasOriginInfoFacade oriInfoFacade;

		public SyncedAssociatedEntityCallback(SormasToSormasOriginInfoDto originInfo, SormasToSormasOriginInfoFacade originInfoFacade) {
			this.originInfo = originInfo;
			this.oriInfoFacade = originInfoFacade;
		}

		public <T extends SormasToSormasShareableDto> void apply(T entity, BiFunction<String, String, SormasToSormasShareInfo> findShareInfo) {
			SormasToSormasShareInfo shareInfo = findShareInfo.apply(entity.getUuid(), originInfo.getOrganizationId());

			if (shareInfo == null) {
				if (!originInfoSaved) {
					oriInfoFacade.saveOriginInfo(originInfo);
					originInfoSaved = true;
				}

				entity.setSormasToSormasOriginInfo(originInfo);
			}
		}
	}
}
