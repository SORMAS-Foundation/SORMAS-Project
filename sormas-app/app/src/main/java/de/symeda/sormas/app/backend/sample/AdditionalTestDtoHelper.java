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

package de.symeda.sormas.app.backend.sample;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class AdditionalTestDtoHelper extends AdoDtoHelper<AdditionalTest, AdditionalTestDto> {

	@Override
	protected Class<AdditionalTest> getAdoClass() {
		return AdditionalTest.class;
	}

	@Override
	protected Class<AdditionalTestDto> getDtoClass() {
		return AdditionalTestDto.class;
	}

	@Override
	protected Call<List<AdditionalTestDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getAdditionalTestFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<AdditionalTestDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getAdditionalTestFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<AdditionalTestDto> additionalTestDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Can't change additional tests in app");
	}

	@Override
	protected void fillInnerFromDto(AdditionalTest target, AdditionalTestDto source) {

		target.setSample(DatabaseHelper.getSampleDao().getByReferenceDto(source.getSample()));
		target.setTestDateTime(source.getTestDateTime());
		target.setHaemoglobinuria(source.getHaemoglobinuria());
		target.setProteinuria(source.getProteinuria());
		target.setHematuria(source.getHematuria());
		target.setArterialVenousGasPh(source.getArterialVenousGasPH());
		target.setArterialVenousGasPco2(source.getArterialVenousGasPco2());
		target.setArterialVenousGasPao2(source.getArterialVenousGasPao2());
		target.setArterialVenousGasHco3(source.getArterialVenousGasHco3());
		target.setGasOxygenTherapy(source.getGasOxygenTherapy());
		target.setAltSgpt(source.getAltSgpt());
		target.setAstSgot(source.getAstSgot());
		target.setCreatinine(source.getCreatinine());
		target.setPotassium(source.getPotassium());
		target.setUrea(source.getUrea());
		target.setHaemoglobin(source.getHaemoglobin());
		target.setTotalBilirubin(source.getTotalBilirubin());
		target.setConjBilirubin(source.getConjBilirubin());
		target.setWbcCount(source.getWbcCount());
		target.setPlatelets(source.getPlatelets());
		target.setProthrombinTime(source.getProthrombinTime());
		target.setOtherTestResults(source.getOtherTestResults());
	}

	@Override
	protected void fillInnerFromAdo(AdditionalTestDto target, AdditionalTest source) {
		if (source.getSample() != null) {
			Sample sample = DatabaseHelper.getSampleDao().queryForId(source.getSample().getId());
			target.setSample(SampleDtoHelper.toReferenceDto(sample));
		} else {
			target.setSample(null);
		}

		target.setTestDateTime(source.getTestDateTime());
		target.setHaemoglobinuria(source.getHaemoglobinuria());
		target.setProteinuria(source.getProteinuria());
		target.setHematuria(source.getHematuria());
		target.setArterialVenousGasPH(source.getArterialVenousGasPh());
		target.setArterialVenousGasPco2(source.getArterialVenousGasPco2());
		target.setArterialVenousGasPao2(source.getArterialVenousGasPao2());
		target.setArterialVenousGasHco3(source.getArterialVenousGasHco3());
		target.setGasOxygenTherapy(source.getGasOxygenTherapy());
		target.setAltSgpt(source.getAltSgpt());
		target.setAstSgot(source.getAstSgot());
		target.setCreatinine(source.getCreatinine());
		target.setPotassium(source.getPotassium());
		target.setUrea(source.getUrea());
		target.setHaemoglobin(source.getHaemoglobin());
		target.setTotalBilirubin(source.getTotalBilirubin());
		target.setConjBilirubin(source.getConjBilirubin());
		target.setWbcCount(source.getWbcCount());
		target.setPlatelets(source.getPlatelets());
		target.setProthrombinTime(source.getProthrombinTime());
		target.setOtherTestResults(source.getOtherTestResults());
	}
}
