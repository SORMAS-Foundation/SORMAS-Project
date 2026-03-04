package de.symeda.sormas.backend.patch.vaccine;

import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.Vaccine;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.ImmunizationStatus;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.vaccination.VaccinationDto;

public class RsvVaccinationPatchHelper {

	public void createImmunziation(Request request) {
		CaseDataDto caze = request.caze;
		ImmunizationDto result = ImmunizationDto.build(caze.getPerson());
		result.setDisease(caze.getDisease());
		result.setReportDate(new Date());

		// TODO: for successfull vaccine
		result.setMeansOfImmunization(MeansOfImmunization.VACCINATION);
		result.setImmunizationStatus(ImmunizationStatus.ACQUIRED);

		// TODO: mandatory
		result.setNumberOfDoses(5);

		// TODO: not vaccinated
		result.setMeansOfImmunization(MeansOfImmunization.OTHER);
		result.setMeansOfImmunizationDetails("NOT_VACCINATED: TODO: must be retrieved from the text ?");
		// OR
		result.setMeansOfImmunizationDetails("DON'T KNOW");
		result.setImmunizationStatus(ImmunizationStatus.NOT_ACQUIRED);

		// TODO: create ngSurvey user
		VaccinationDto vaccine = VaccinationDto.build(null);

		// TODO: missing vaccines for RSV and Pertusis
		vaccine.setVaccineName(Vaccine.LC_16);
		vaccine.setVaccineType("Something useful here ?");
		// TODO: set actual date // MANDATORY
		vaccine.setVaccinationDate(new Date());

		// TODO: create

		result.setVaccinations(List.of(vaccine));
	}

	public static class Request {

		private CaseDataDto caze;
	}
}
