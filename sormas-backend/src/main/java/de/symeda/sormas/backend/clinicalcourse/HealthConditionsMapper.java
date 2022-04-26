/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.clinicalcourse;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;

import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.backend.util.DtoHelper;

@LocalBean
@Stateless(name = "HealthConditionsMapper")
public class HealthConditionsMapper {

	@EJB
	private HealthConditionsService healthConditionsService;

	public static HealthConditionsDto toDto(HealthConditions source) {

		if (source == null) {
			return null;
		}

		HealthConditionsDto target = new HealthConditionsDto();
		DtoHelper.fillDto(target, source);

		target.setAsplenia(source.getAsplenia());
		target.setChronicHeartFailure(source.getChronicHeartFailure());
		target.setChronicKidneyDisease(source.getChronicKidneyDisease());
		target.setChronicLiverDisease(source.getChronicLiverDisease());
		target.setChronicNeurologicCondition(source.getChronicNeurologicCondition());
		target.setChronicPulmonaryDisease(source.getChronicPulmonaryDisease());
		target.setDiabetes(source.getDiabetes());
		target.setHepatitis(source.getHepatitis());
		target.setHiv(source.getHiv());
		target.setHivArt(source.getHivArt());
		target.setMalignancyChemotherapy(source.getMalignancyChemotherapy());
		target.setTuberculosis(source.getTuberculosis());
		target.setDownSyndrome(source.getDownSyndrome());
		target.setCongenitalSyphilis(source.getCongenitalSyphilis());
		target.setOtherConditions(source.getOtherConditions());
		target.setImmunodeficiencyOtherThanHiv(source.getImmunodeficiencyOtherThanHiv());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setObesity(source.getObesity());
		target.setCurrentSmoker(source.getCurrentSmoker());
		target.setFormerSmoker(source.getFormerSmoker());
		target.setAsthma(source.getAsthma());
		target.setSickleCellDisease(source.getSickleCellDisease());
		target.setImmunodeficiencyIncludingHiv(source.getImmunodeficiencyIncludingHiv());

		return target;
	}

	public HealthConditions fillOrBuildEntity(@NotNull HealthConditionsDto source, HealthConditions target, boolean checkChangeDate) {

		target = DtoHelper.fillOrBuildEntity(source, target, HealthConditions::new, checkChangeDate);

		target.setAsplenia(source.getAsplenia());
		target.setChronicHeartFailure(source.getChronicHeartFailure());
		target.setChronicKidneyDisease(source.getChronicKidneyDisease());
		target.setChronicLiverDisease(source.getChronicLiverDisease());
		target.setChronicNeurologicCondition(source.getChronicNeurologicCondition());
		target.setChronicPulmonaryDisease(source.getChronicPulmonaryDisease());
		target.setDiabetes(source.getDiabetes());
		target.setHepatitis(source.getHepatitis());
		target.setHiv(source.getHiv());
		target.setHivArt(source.getHivArt());
		target.setMalignancyChemotherapy(source.getMalignancyChemotherapy());
		target.setTuberculosis(source.getTuberculosis());
		target.setDownSyndrome(source.getDownSyndrome());
		target.setCongenitalSyphilis(source.getCongenitalSyphilis());
		target.setOtherConditions(source.getOtherConditions());
		target.setImmunodeficiencyOtherThanHiv(source.getImmunodeficiencyOtherThanHiv());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setObesity(source.getObesity());
		target.setCurrentSmoker(source.getCurrentSmoker());
		target.setFormerSmoker(source.getFormerSmoker());
		target.setAsthma(source.getAsthma());
		target.setSickleCellDisease(source.getSickleCellDisease());
		target.setImmunodeficiencyIncludingHiv(source.getImmunodeficiencyIncludingHiv());

		return target;
	}

	public HealthConditions fromDto(@NotNull HealthConditionsDto source, boolean checkChangeDate) {

		HealthConditions target =
			DtoHelper.fillOrBuildEntity(source, healthConditionsService.getByUuid(source.getUuid()), HealthConditions::new, checkChangeDate);

		return fillOrBuildEntity(source, target, checkChangeDate);
	}
}
