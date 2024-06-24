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

package de.symeda.sormas.api.selfreport;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;

import de.symeda.sormas.api.CoreFacade;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;

import java.util.List;

@Remote
public interface SelfReportFacade extends CoreFacade<SelfReportDto, SelfReportIndexDto, SelfReportReferenceDto, SelfReportCriteria> {

    List<SelfReportListEntryDto> getEntriesList(SelfReportCriteria selfReportCriteria, Integer first, Integer max);

	void markProcessed(SelfReportReferenceDto selfReportRef, CaseReferenceDto caze);

	void markProcessed(SelfReportReferenceDto selfReportRef, ContactReferenceDto contactRef);

	boolean isProcessed(SelfReportReferenceDto reference);

	boolean existsUnlinkedCOntactWithCaseReferenceNumber(String caseReferenceNumber);

	void linkContactsToCaseByReferenceNumber(CaseReferenceDto caze);

	boolean existsReferencedCaseReport(String caseReference);

	List<SelfReportExportDto> getExportList(SelfReportCriteria selfReportCriteria, Collection<String> selectedRows, int first, int max);

}
