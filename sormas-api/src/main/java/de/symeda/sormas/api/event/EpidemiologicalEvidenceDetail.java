/*
 *  SORMAS® - Surveillance Outbreak Response Management & Analysis System
 *  Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.symeda.sormas.api.event;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum EpidemiologicalEvidenceDetail {

    STUDY(null),
    CASE_CONTROL_STUDY(STUDY),
    COHORT_STUDY(STUDY),
    TEST_SERGIU1(COHORT_STUDY),
    TEST_FOURTH_LEVEL11(TEST_SERGIU1),
    TEST_FOURTH_LEVEL12(TEST_SERGIU1),
    TEST_SERGIU2(COHORT_STUDY),
    TEST_FOURTH_LEVEL21(TEST_SERGIU2),
    TEST_FOURTH_LEVEL22(TEST_SERGIU2),
    EXPLORATIVE_SURVEY_OF_AFFECTED(null),
    CONTACT_TO_SICK_PERSON(EXPLORATIVE_SURVEY_OF_AFFECTED),
    CONTACT_TO_CONTAMINATED_MATERIAL(EXPLORATIVE_SURVEY_OF_AFFECTED),
    DESCRIPTIVE_ANALYSIS_OF_ASCERTAINED_DATA(null),
    TEMPORAL_OCCURENCE(DESCRIPTIVE_ANALYSIS_OF_ASCERTAINED_DATA),
    SPACIAL_OCCURENCE(DESCRIPTIVE_ANALYSIS_OF_ASCERTAINED_DATA),
    DIRECT_OCCURENCE(DESCRIPTIVE_ANALYSIS_OF_ASCERTAINED_DATA),
    SUSPICION(null),
    EXPRESSED_BY_DISEASED(SUSPICION),
    EXPRESSED_BY_HEALTH_DEPARTMENT(SUSPICION);

    private EpidemiologicalEvidenceDetail parent;

    EpidemiologicalEvidenceDetail(EpidemiologicalEvidenceDetail parent) {
        this.parent = parent;
    }

    public EpidemiologicalEvidenceDetail getParent() {
        return parent;
    }

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
