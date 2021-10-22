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

package de.symeda.sormas.backend.sormastosormas.share.shareinfo;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import de.symeda.sormas.api.sormastosormas.SormasToSormasOptionsDto;
import de.symeda.sormas.api.sormastosormas.sharerequest.ShareRequestStatus;
import de.symeda.sormas.api.utils.DataHelper;

public class ShareInfoHelper {

	private ShareInfoHelper() {
	}

	public static boolean isOwnerShipHandedOver(SormasToSormasShareInfo shareInfo) {
		// ownership handed over and the latest request was accepted
		return shareInfo.isOwnershipHandedOver()
			&& shareInfo.getRequests()
				.stream()
				.max(Comparator.comparing(ShareRequestInfo::getCreationDate))
				.filter(r -> r.getRequestStatus() == ShareRequestStatus.ACCEPTED)
				.isPresent();
	}

	public static <T> SormasToSormasShareInfo createShareInfo(
		String organizationId,
		T entity,
		BiConsumer<SormasToSormasShareInfo, T> setEntity,
		SormasToSormasOptionsDto options) {
		SormasToSormasShareInfo shareInfo = new SormasToSormasShareInfo();
		shareInfo.setUuid(DataHelper.createUuid());
		shareInfo.setOrganizationId(organizationId);

		shareInfo.setOwnershipHandedOver(options.isHandOverOwnership());

		setEntity.accept(shareInfo, entity);

		return shareInfo;
	}

	public static Optional<ShareRequestInfo> getLatestRequest(Stream<ShareRequestInfo> requests) {
		return requests.max(Comparator.comparing(ShareRequestInfo::getCreationDate));
	}

	public static Optional<ShareRequestInfo> getLatestAcceptedRequest(Stream<ShareRequestInfo> requests) {
		return getLatestRequest(requests.filter(r -> r.getRequestStatus() == ShareRequestStatus.ACCEPTED));
	}
}
