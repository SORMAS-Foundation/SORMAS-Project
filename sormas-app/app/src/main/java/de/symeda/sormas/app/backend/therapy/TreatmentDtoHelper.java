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

package de.symeda.sormas.app.backend.therapy;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.therapy.TreatmentDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class TreatmentDtoHelper extends AdoDtoHelper<Treatment, TreatmentDto> {

	private TherapyDtoHelper therapyDtoHelper = new TherapyDtoHelper();
	private PrescriptionDtoHelper prescriptionDtoHelper = new PrescriptionDtoHelper();

	@Override
	protected Class<Treatment> getAdoClass() {
		return Treatment.class;
	}

	@Override
	protected Class<TreatmentDto> getDtoClass() {
		return TreatmentDto.class;
	}

	@Override
	protected Call<List<TreatmentDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getTreatmentFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<TreatmentDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getTreatmentFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<TreatmentDto> treatmentDtos) throws NoConnectionException {
		return RetroProvider.getTreatmentFacade().pushAll(treatmentDtos);
	}

	@Override
	public void fillInnerFromDto(Treatment target, TreatmentDto source) {
		target.setTherapy(DatabaseHelper.getTherapyDao().getByReferenceDto(source.getTherapy()));
		target.setTreatmentDateTime(source.getTreatmentDateTime());
		target.setExecutingClinician(source.getExecutingClinician());
		target.setTreatmentType(source.getTreatmentType());
		target.setTreatmentDetails(source.getTreatmentDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		target.setPrescription(DatabaseHelper.getPrescriptionDao().getByReferenceDto(source.getPrescription()));

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	public void fillInnerFromAdo(TreatmentDto target, Treatment source) {
		if (source.getTherapy() != null) {
			Therapy therapy = DatabaseHelper.getTherapyDao().queryForId(source.getTherapy().getId());
			target.setTherapy(TherapyDtoHelper.toReferenceDto(therapy));
		}
		target.setTreatmentDateTime(source.getTreatmentDateTime());
		target.setExecutingClinician(source.getExecutingClinician());
		target.setTreatmentType(source.getTreatmentType());
		target.setTreatmentDetails(source.getTreatmentDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
		if (source.getPrescription() != null) {
			Prescription prescription = DatabaseHelper.getPrescriptionDao().queryForId(source.getTherapy().getId());
			target.setPrescription(PrescriptionDtoHelper.toReferenceDto(prescription));
		}

		target.setPseudonymized(source.isPseudonymized());
	}
}
