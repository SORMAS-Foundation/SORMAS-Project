/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.api.therapy;

import java.util.Date;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.YesNoUnknown;

@DependingOnFeatureType(featureType = FeatureType.CASE_SURVEILANCE)
public class TherapyDto extends EntityDto {

	private static final long serialVersionUID = -1467303502817738376L;

	public static final String I18N_PREFIX = "Therapy";

	public static final String DIRECTLY_OBSERVED_TREATMENT = "directlyObservedTreatment";
	public static final String MDR_XDR_TUBERCULOSIS = "mdrXdrTuberculosis";
	public static final String BEIJING_LINEAGE = "beijingLineage";

	public static final String TREATMENT_STARTED = "treatmentStarted";
	public static final String TREATMENT_NOT_APPLICABLE = "treatmentNotApplicable";

	private boolean directlyObservedTreatment;
	private boolean mdrXdrTuberculosis;
	private boolean beijingLineage;

	private YesNoUnknown treatmentStarted;
	private boolean treatmentNotApplicable;
	private Date treatmentStartDate;

	public static TherapyDto build() {

		TherapyDto therapy = new TherapyDto();
		therapy.setUuid(DataHelper.createUuid());
		return therapy;
	}

	public TherapyReferenceDto toReference() {
		return new TherapyReferenceDto(getUuid());
	}

	public boolean isDirectlyObservedTreatment() {
		return directlyObservedTreatment;
	}

	public void setDirectlyObservedTreatment(boolean directlyObservedTreatment) {
		this.directlyObservedTreatment = directlyObservedTreatment;
	}

	public boolean isMdrXdrTuberculosis() {
		return mdrXdrTuberculosis;
	}

	public void setMdrXdrTuberculosis(boolean mdrXdrTuberculosis) {
		this.mdrXdrTuberculosis = mdrXdrTuberculosis;
	}

	public boolean isBeijingLineage() {
		return beijingLineage;
	}

	public void setBeijingLineage(boolean beijingLineage) {
		this.beijingLineage = beijingLineage;
	}

	public YesNoUnknown getTreatmentStarted() {
		return treatmentStarted;
	}

	public void setTreatmentStarted(YesNoUnknown treatmentStarted) {
		this.treatmentStarted = treatmentStarted;
	}

	public boolean isTreatmentNotApplicable() {
		return treatmentNotApplicable;
	}

	public void setTreatmentNotApplicable(boolean treatmentNotApplicable) {
		this.treatmentNotApplicable = treatmentNotApplicable;
	}

	public Date getTreatmentStartDate() {
		return treatmentStartDate;
	}

	public void setTreatmentStartDate(Date treatmentStartDate) {
		this.treatmentStartDate = treatmentStartDate;
	}
}
