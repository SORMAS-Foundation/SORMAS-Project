package de.symeda.sormas.app.backend.vaccination;

import java.util.List;

import de.symeda.sormas.api.PushResult;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditions;
import de.symeda.sormas.app.backend.clinicalcourse.HealthConditionsDtoHelper;
import de.symeda.sormas.app.backend.common.AdoDtoHelper;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.immunization.Immunization;
import de.symeda.sormas.app.backend.immunization.ImmunizationDtoHelper;
import de.symeda.sormas.app.backend.user.User;
import de.symeda.sormas.app.backend.user.UserDtoHelper;
import de.symeda.sormas.app.rest.NoConnectionException;
import retrofit2.Call;

public class VaccinationDtoHelper extends AdoDtoHelper<Vaccination, VaccinationDto> {

	private HealthConditionsDtoHelper healthConditionsDtoHelper = new HealthConditionsDtoHelper();

	@Override
	protected Class<Vaccination> getAdoClass() {
		return Vaccination.class;
	}

	@Override
	protected Class<VaccinationDto> getDtoClass() {
		return VaccinationDto.class;
	}

	@Override
	protected Call<List<VaccinationDto>> pullAllSince(long since, Integer size, String lastSynchronizedUuid)  throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<VaccinationDto>> pullByUuids(List<String> uuids) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected Call<List<PushResult>> pushAll(List<VaccinationDto> vaccinationDtos) throws NoConnectionException {
		throw new UnsupportedOperationException("Entity is embedded");
	}

	@Override
	protected void fillInnerFromDto(Vaccination target, VaccinationDto source) {

		target.setImmunization(DatabaseHelper.getImmunizationDao().getByReferenceDto(source.getImmunization()));
		target.setHealthConditions(healthConditionsDtoHelper.fillOrCreateFromDto(target.getHealthConditions(), source.getHealthConditions()));
		target.setReportDate(source.getReportDate());
		target.setReportingUser(DatabaseHelper.getUserDao().getByReferenceDto(source.getReportingUser()));
		target.setVaccinationDate(source.getVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineType(source.getVaccineType());
		target.setVaccineDose(source.getVaccineDose());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setPregnant(source.getPregnant());
		target.setTrimester(source.getTrimester());

		target.setPseudonymized(source.isPseudonymized());
	}

	@Override
	protected void fillInnerFromAdo(VaccinationDto target, Vaccination source) {

		if (source.getImmunization() != null) {
			Immunization immunization = DatabaseHelper.getImmunizationDao().queryForId(source.getImmunization().getId());
			target.setImmunization(ImmunizationDtoHelper.toReferenceDto(immunization));
		}

		if (source.getHealthConditions() != null) {
			HealthConditions healthConditions = DatabaseHelper.getHealthConditionsDao().queryForId(source.getHealthConditions().getId());
			target.setHealthConditions(healthConditionsDtoHelper.adoToDto(healthConditions));
		} else {
			target.setHealthConditions(null);
		}

		target.setReportDate(source.getReportDate());

		if (source.getReportingUser() != null) {
			User user = DatabaseHelper.getUserDao().queryForId(source.getReportingUser().getId());
			target.setReportingUser(UserDtoHelper.toReferenceDto(user));
		} else {
			target.setReportingUser(null);
		}

		target.setVaccinationDate(source.getVaccinationDate());
		target.setVaccineName(source.getVaccineName());
		target.setOtherVaccineName(source.getOtherVaccineName());
		target.setVaccineManufacturer(source.getVaccineManufacturer());
		target.setOtherVaccineManufacturer(source.getOtherVaccineManufacturer());
		target.setVaccineType(source.getVaccineType());
		target.setVaccineDose(source.getVaccineDose());
		target.setVaccineInn(source.getVaccineInn());
		target.setVaccineUniiCode(source.getVaccineUniiCode());
		target.setVaccineAtcCode(source.getVaccineAtcCode());
		target.setVaccinationInfoSource(source.getVaccinationInfoSource());
		target.setPregnant(source.getPregnant());
		target.setTrimester(source.getTrimester());

		target.setPseudonymized(source.isPseudonymized());
	}

    @Override
    protected long getApproximateJsonSizeInBytes() {
        return 0;
    }
}
