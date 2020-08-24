/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.app.backend.clinicalcourse;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.clinicalcourse.HealthConditionsDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class HealthConditionsDtoHelper extends AdoDtoHelper<HealthConditions, HealthConditionsDto> {

	@Override
	protected Class<HealthConditions> getAdoClass() {
		return HealthConditions.class;
	}

	@Override
	protected Class<HealthConditionsDto> getDtoClass() {
		return HealthConditionsDto.class;
	}

	@Override
	protected Call<List<HealthConditionsDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<HealthConditionsDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<HealthConditionsDto> healthConditionDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	public void fillInnerFromDto(HealthConditions target, HealthConditionsDto source) {
		target.setTuberculosis(source.getTuberculosis());
		target.setAsplenia(source.getAsplenia());
		target.setHepatitis(source.getHepatitis());
		target.setDiabetes(source.getDiabetes());
		target.setHiv(source.getHiv());
		target.setHivArt(source.getHivArt());
		target.setChronicLiverDisease(source.getChronicLiverDisease());
		target.setMalignancyChemotherapy(source.getMalignancyChemotherapy());
		target.setChronicHeartFailure(source.getChronicHeartFailure());
		target.setChronicPulmonaryDisease(source.getChronicPulmonaryDisease());
		target.setChronicKidneyDisease(source.getChronicKidneyDisease());
		target.setChronicNeurologicCondition(source.getChronicNeurologicCondition());
		target.setOtherConditions(source.getOtherConditions());
		target.setDownSyndrome(source.getDownSyndrome());
		target.setCongenitalSyphilis(source.getCongenitalSyphilis());
		target.setImmunodeficiencyOtherThanHiv(source.getImmunodeficiencyOtherThanHiv());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setObesity(source.getObesity());
		target.setCurrentSmoker(source.getCurrentSmoker());
		target.setFormerSmoker(source.getFormerSmoker());
		target.setAsthma(source.getAsthma());
		target.setSickleCellDisease(source.getSickleCellDisease());
		target.setImmunodeficiencyIncludingHiv(source.getImmunodeficiencyIncludingHiv());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(HealthConditionsDto target, HealthConditions source) {
		target.setTuberculosis(source.getTuberculosis());
		target.setAsplenia(source.getAsplenia());
		target.setHepatitis(source.getHepatitis());
		target.setDiabetes(source.getDiabetes());
		target.setHiv(source.getHiv());
		target.setHivArt(source.getHivArt());
		target.setChronicLiverDisease(source.getChronicLiverDisease());
		target.setMalignancyChemotherapy(source.getMalignancyChemotherapy());
		target.setChronicHeartFailure(source.getChronicHeartFailure());
		target.setChronicPulmonaryDisease(source.getChronicPulmonaryDisease());
		target.setChronicKidneyDisease(source.getChronicKidneyDisease());
		target.setChronicNeurologicCondition(source.getChronicNeurologicCondition());
		target.setOtherConditions(source.getOtherConditions());
		target.setDownSyndrome(source.getDownSyndrome());
		target.setCongenitalSyphilis(source.getCongenitalSyphilis());
		target.setImmunodeficiencyOtherThanHiv(source.getImmunodeficiencyOtherThanHiv());
		target.setCardiovascularDiseaseIncludingHypertension(source.getCardiovascularDiseaseIncludingHypertension());
		target.setObesity(source.getObesity());
		target.setCurrentSmoker(source.getCurrentSmoker());
		target.setFormerSmoker(source.getFormerSmoker());
		target.setAsthma(source.getAsthma());
		target.setSickleCellDisease(source.getSickleCellDisease());
		target.setImmunodeficiencyIncludingHiv(source.getImmunodeficiencyIncludingHiv());

		target.setPseudonymized(source.isPseudonymized());
	}
}
