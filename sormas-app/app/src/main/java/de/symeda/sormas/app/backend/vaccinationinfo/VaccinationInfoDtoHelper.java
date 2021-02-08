package de.symeda.sormas.app.backend.vaccinationinfo;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.vaccinationinfo.VaccinationInfoDto;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class VaccinationInfoDtoHelper extends AdoDtoHelper<VaccinationInfo, VaccinationInfoDto> {

	@Override
	protected Class<VaccinationInfo> getAdoClass() {
		return VaccinationInfo.class;
	}

	@Override
	protected Class<VaccinationInfoDto> getDtoClass() {
		return VaccinationInfoDto.class;
	}

	@Override
	protected Call<List<VaccinationInfoDto>> pullAllSince(long since) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<VaccinationInfoDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<VaccinationInfoDto> vaccinationInfoDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(VaccinationInfo target, VaccinationInfoDto source) {
		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setFirstVaccinationDate(source.getFirstVaccinationDate());
		target.setLastVaccinationDate(source.getLastVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineBatchNumber(source.getVaccineBatchNumber());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());
	}

	@Override
	protected void fillInnerFromAdo(VaccinationInfoDto target, VaccinationInfo source) {
		target.setVaccination(source.getVaccination());
		target.setVaccinationDoses(source.getVaccinationDoses());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setFirstVaccinationDate(source.getFirstVaccinationDate());
		target.setLastVaccinationDate(source.getLastVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineBatchNumber(source.getVaccineBatchNumber());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());
	}
}
