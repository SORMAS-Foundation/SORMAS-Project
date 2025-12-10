/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2024 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.epipulse;

import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.DeletableFacade;
import de.symeda.sormas.api.common.Page;
import de.symeda.sormas.api.utils.SortProperty;

@Remote
public interface EpipulseExportFacade extends DeletableFacade {

	EpipulseExportDto saveEpipulseExport(EpipulseExportDto dto);

	Page<EpipulseExportIndexDto> getIndexPage(EpipulseExportCriteria criteria, Integer offset, Integer size, List<SortProperty> sortProperties);

	EpipulseExportDto getEpiPulseExportByUuid(String uuid);

	long count(EpipulseExportCriteria criteria);

	List<EpipulseExportIndexDto> getIndexList(EpipulseExportCriteria criteria, Integer first, Integer max, List<SortProperty> sortProperties);

	void cancelEpipulseExport(String uuid);

	void deleteEpipulseExport(String uuid);

	boolean isConfigured();
}
