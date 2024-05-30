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

package de.symeda.sormas.api.selfreport.processing;

import java.util.List;

import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseFacade;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactFacade;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.feature.FeatureConfigurationFacade;
import de.symeda.sormas.api.infrastructure.community.CommunityFacade;
import de.symeda.sormas.api.infrastructure.district.DistrictFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityFacade;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.region.RegionFacade;
import de.symeda.sormas.api.selfreport.SelfReportDto;
import de.symeda.sormas.api.selfreport.SelfReportFacade;
import de.symeda.sormas.api.selfreport.SelfReportReferenceDto;
import de.symeda.sormas.api.utils.dataprocessing.AbstractProcessingFacade;

public abstract class SelfReportProcessingFacade extends AbstractProcessingFacade {

	private final FacilityFacade facilityFacade;
	private final SelfReportFacade selfReportFacade;

	public SelfReportProcessingFacade(
		FeatureConfigurationFacade featureConfigurationFacade,
		CaseFacade caseFacade,
		ContactFacade contactFacade,
		RegionFacade regionFacade,
		DistrictFacade districtFacade,
		CommunityFacade communityFacade,
		FacilityFacade facilityFacade,
		SelfReportFacade selfReportFacade) {
		super(featureConfigurationFacade, caseFacade, contactFacade, regionFacade, districtFacade, communityFacade);
		this.facilityFacade = facilityFacade;
		this.selfReportFacade = selfReportFacade;
	}

	public void markSelfReportAsProcessed(SelfReportReferenceDto selfReportRef, CaseReferenceDto caseRef) {
		selfReportFacade.markProcessed(selfReportRef, caseRef);
	}

	public void markSelfReportAsProcessed(SelfReportReferenceDto selfReportRef, ContactReferenceDto contactRef) {
		selfReportFacade.markProcessed(selfReportRef, contactRef);
	}

	public FacilityReferenceDto getNoneFacility() {
		return facilityFacade.getReferenceByUuid(FacilityDto.NONE_FACILITY_UUID);
	}

	public boolean existsContactToLinkToCase(String caseReferenceNumber) {
		return selfReportFacade.existsUnlinkedCOntactWithCaseReferenceNumber(caseReferenceNumber);
	}

	public void linkContactsToCaseByReferenceNumber(CaseReferenceDto caze) {
		selfReportFacade.linkContactsToCaseByReferenceNumber(caze);
	}

	public boolean existsReferencedCaseReport(SelfReportDto selfReport) {
		return selfReportFacade.existsReferencedCaseReport(selfReport.getCaseReference());
	}

	public List<CaseIndexDto> getCasesWithReferenceNumber(String caseReference) {
		// return the first 2 to make it more performant, only the first one is needed and only if there is a single one
		return caseFacade.getIndexList(new CaseCriteria().caseReferenceNumber(caseReference), 0, 2, null);
	}

	public void linkContactToCase(ContactReferenceDto contactRef, CaseReferenceDto caseRef) {
		contactFacade.linkContactToCase(contactRef, caseRef);
	}
}
