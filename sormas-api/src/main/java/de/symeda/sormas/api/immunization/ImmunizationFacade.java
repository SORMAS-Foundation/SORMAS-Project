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

package de.symeda.sormas.api.immunization;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface ImmunizationFacade extends CoreFacade<ImmunizationDto, ImmunizationIndexDto, ImmunizationReferenceDto, ImmunizationCriteria> {

	void archiveAllArchivableImmunizations(int daysAfterImmunizationsGetsArchived);

	List<String> getDeletedUuidsSince(Date since);

	ImmunizationDto getImmunizationByUuid(String uuid);

	List<ImmunizationDto> getSimilarImmunizations(ImmunizationSimilarityCriteria criteria);

	void updateImmunizationStatuses();

	List<ImmunizationListEntryDto> getEntriesList(ImmunizationListCriteria criteria, Integer first, Integer max);

	Page<ImmunizationIndexDto> getIndexPage(
		ImmunizationCriteria immunizationCriteria,
		Integer offset,
		Integer size,
		List<SortProperty> sortProperties);

	boolean linkRecoveryImmunizationToSearchedCase(String specificCaseSearchValue, ImmunizationDto immunization);

	List<ImmunizationDto> getByPersonUuids(List<String> uuids);
}
