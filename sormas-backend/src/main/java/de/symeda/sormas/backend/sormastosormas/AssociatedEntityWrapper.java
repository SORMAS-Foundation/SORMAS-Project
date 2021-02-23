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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import de.symeda.sormas.backend.event.EventParticipant;

public class AssociatedEntityWrapper<T extends SormasToSormasEntity> {

	private final T entity;
	private final BiConsumer<SormasToSormasShareInfo, T> shareInfoAssociatedObjectFn;
	private final BiFunction<SormasToSormasShareInfoService, String, SormasToSormasShareInfo> shareInfoFindFn;

	public static List<AssociatedEntityWrapper<?>> forEventParticipants(List<EventParticipant> eventParticipants) {
		return eventParticipants.stream()
			.map(
				ep -> new AssociatedEntityWrapper<>(
					ep,
					SormasToSormasShareInfo::setEventParticipant,
					(shareInfoService, organizationId) -> shareInfoService.getByEventParticipantAndOrganization(ep.getUuid(), organizationId)))
			.collect(Collectors.toList());
	}

	private AssociatedEntityWrapper(
		T entity,
		BiConsumer<SormasToSormasShareInfo, T> shareInfoAssociatedObjectFn,
		BiFunction<SormasToSormasShareInfoService, String, SormasToSormasShareInfo> shareInfoFindFn) {
		this.entity = entity;
		this.shareInfoAssociatedObjectFn = shareInfoAssociatedObjectFn;
		this.shareInfoFindFn = shareInfoFindFn;
	}

	public T getEntity() {
		return entity;
	}

	public void setShareInfoAssociatedObject(SormasToSormasShareInfo shareInfo) {
		shareInfoAssociatedObjectFn.accept(shareInfo, entity);
	}

	public SormasToSormasShareInfo getExistingShareInfo(SormasToSormasShareInfoService shareInfoService, String organizationId) {
		return shareInfoFindFn.apply(shareInfoService, organizationId);
	}
}
