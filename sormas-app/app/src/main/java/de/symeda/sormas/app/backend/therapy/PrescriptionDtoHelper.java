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
import de.symeda.sormas.api.therapy.PrescriptionDto;
import de.symeda.sormas.api.therapy.PrescriptionReferenceDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import de.symeda.sormas.app.rest.RetroProvider;
import retrofit2.Call;

public class PrescriptionDtoHelper extends AdoDtoHelper<Prescription, PrescriptionDto> {

	private TherapyDtoHelper therapyDtoHelper = new TherapyDtoHelper();

	@Override
	protected Class<Prescription> getAdoClass() {
		return Prescription.class;
	}

	@Override
	protected Class<PrescriptionDto> getDtoClass() {
		return PrescriptionDto.class;
	}

	@Override
	protected Call<List<PrescriptionDto>> pullAllSince(long since) throws NoConnectionException {
		return RetroProvider.getPrescriptionFacade().pullAllSince(since);
	}

	@Override
	protected Call<List<PrescriptionDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		return RetroProvider.getPrescriptionFacade().pullByUuids(uuids);
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<PrescriptionDto> prescriptionDtos) throws NoConnectionException {
		return RetroProvider.getPrescriptionFacade().pushAll(prescriptionDtos);
	}

	@Override
	public void fillInnerFromDto(Prescription target, PrescriptionDto source) {
		target.setTherapy(DatabaseHelper.getTherapyDao().getByReferenceDto(source.getTherapy()));
		target.setPrescriptionDate(source.getPrescriptionDate());
		target.setPrescriptionStart(source.getPrescriptionStart());
		target.setPrescriptionEnd(source.getPrescriptionEnd());
		target.setPrescribingClinician(source.getPrescribingClinician());
		target.setPrescriptionType(source.getPrescriptionType());
		target.setPrescriptionDetails(source.getPrescriptionDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setFrequency(source.getFrequency());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
	}

	@Override
	public void fillInnerFromAdo(PrescriptionDto target, Prescription source) {
		if (source.getTherapy() != null) {
			Therapy therapy = DatabaseHelper.getTherapyDao().queryForId(source.getTherapy().getId());
			target.setTherapy(TherapyDtoHelper.toReferenceDto(therapy));
		}
		target.setPrescriptionDate(source.getPrescriptionDate());
		target.setPrescriptionStart(source.getPrescriptionStart());
		target.setPrescriptionEnd(source.getPrescriptionEnd());
		target.setPrescribingClinician(source.getPrescribingClinician());
		target.setPrescriptionType(source.getPrescriptionType());
		target.setPrescriptionDetails(source.getPrescriptionDetails());
		target.setTypeOfDrug(source.getTypeOfDrug());
		target.setFrequency(source.getFrequency());
		target.setDose(source.getDose());
		target.setRoute(source.getRoute());
		target.setRouteDetails(source.getRouteDetails());
		target.setAdditionalNotes(source.getAdditionalNotes());
	}

	public static PrescriptionReferenceDto toReferenceDto(Prescription ado) {
		if (ado == null) {
			return null;
		}
		return new PrescriptionReferenceDto(ado.getUuid());
	}
}
