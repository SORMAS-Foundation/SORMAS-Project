/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

	/**
	 * The EpiPulse subject code the disease configuration provides for the given code, used to prefill the export form
	 * (#14472). Only reads the configuration, never changes it.
	 * <p>
	 * Example: disease configuration of Syphilis has {@code "SYPH"} → {@code SYPH} returns {@code "SYPH"}, {@code CONSYPH}
	 * returns {@code "CONSYPH"}; nothing configured for Diphtheria → {@code DIPH} returns {@code null}.
	 *
	 * @param subjectCode
	 *            the code selected in the disease dropdown
	 * @return the configured subject code, or {@code null} when none is configured
	 */
	String getConfiguredSubjectCode(EpipulseSubjectCode subjectCode);

	/**
	 * The EpiPulse data source the configuration provides for the disease of the given code: its own one in the disease
	 * configuration, else the system configuration value {@code EPIPULSE_DATASOURCE}. Used to prefill the export form
	 * (#14472); only reads the configuration, never changes it.
	 * <p>
	 * Example: Measles with own data source {@code "LU-SURV"} → {@code "LU-SURV"}; without one and
	 * {@code EPIPULSE_DATASOURCE = "LU-SYSTEM1"} → {@code "LU-SYSTEM1"}; neither → {@code null}.
	 *
	 * @param subjectCode
	 *            the code selected in the disease dropdown
	 * @return the configured data source, or {@code null} when none is configured
	 */
	String getConfiguredDatasource(EpipulseSubjectCode subjectCode);
}
