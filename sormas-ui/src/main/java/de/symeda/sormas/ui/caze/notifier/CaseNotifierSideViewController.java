/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.caze.notifier;

import java.util.Comparator;
import java.util.List;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.caze.surveillancereport.ReportingType;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportCriteria;
import de.symeda.sormas.api.caze.surveillancereport.SurveillanceReportDto;
import de.symeda.sormas.api.person.notifier.NotifierDto;
import de.symeda.sormas.api.therapy.TherapyDto;
import de.symeda.sormas.api.therapy.TherapyReferenceDto;
import de.symeda.sormas.api.therapy.TreatmentCriteria;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.api.therapy.TreatmentIndexDto;

/**
 * Controller for managing the side view of case notifiers.
 * Provides methods to retrieve notifier components, surveillance reports, and treatments.
 */
public class CaseNotifierSideViewController {

    /**
     * Retrieves the notifier component for a given case.
     *
     * @param caze The case data for which the notifier component is to be retrieved.
     * @return A {@link CaseNotifierSideViewContent} object containing the notifier, oldest report, and treatment details.
     */
    public CaseNotifierSideViewContent getNotifierComponent(CaseDataDto caze) {

        NotifierDto notifier =
            FacadeProvider.getNotifierFacade().getByUuidAndTime(caze.getNotifier().getUuid(), caze.getNotifier().getVersionDate().toInstant());

        CaseReferenceDto cazeRef = new CaseReferenceDto();
        cazeRef.setUuid(caze.getUuid());

        return new CaseNotifierSideViewContent(caze, notifier, getOldestReport(cazeRef), getTreatment(caze.getTherapy()), true);
    }

    /**
     * Retrieves the oldest surveillance report for a given case reference.
     *
     * @param caze The reference to the case for which the oldest report is to be retrieved.
     * @return The oldest {@link SurveillanceReportDto} for the case, or null if no reports are found.
     */
    protected SurveillanceReportDto getOldestReport(CaseReferenceDto caze) {

        SurveillanceReportCriteria criteria = new SurveillanceReportCriteria();
        criteria.caze(caze);
        criteria.setReportingType(ReportingType.DOCTOR);

        List<SurveillanceReportDto> reports = FacadeProvider.getSurveillanceReportFacade().getIndexList(criteria, null, null, null);

        // Filter to get the oldest report
        return reports.stream()
            .min(Comparator.comparing(SurveillanceReportDto::getReportDate)) // Assuming getDate() returns the report date
            .orElse(null);
    }

    /**
     * Retrieves the treatment details for a given therapy.
     *
     * @param therapy The therapy for which the treatment details are to be retrieved.
     * @return A {@link TreatmentDto} object containing the treatment details, or null if no treatment is found.
     */
    protected TreatmentDto getTreatment(TherapyDto therapy) {

        if (therapy == null) {
            return null;
        }

        TherapyReferenceDto therapyRef = new TherapyReferenceDto(therapy.getUuid());
        TreatmentCriteria criteria = new TreatmentCriteria();
        criteria.therapy(therapyRef);
        List<TreatmentIndexDto> treatments = FacadeProvider.getTreatmentFacade().getIndexList(criteria);

        if (treatments == null || treatments.isEmpty()) {
            return null;
        }

        TreatmentIndexDto treatmentIndex = treatments.get(0);

        return FacadeProvider.getTreatmentFacade().getTreatmentByUuid(treatmentIndex.getUuid());
    }

}
