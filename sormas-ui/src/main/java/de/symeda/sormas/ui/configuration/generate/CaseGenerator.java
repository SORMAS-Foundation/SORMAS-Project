package de.symeda.sormas.ui.configuration.generate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.contact.QuarantineType;
import de.symeda.sormas.api.infrastructure.facility.FacilityCriteria;
import de.symeda.sormas.api.infrastructure.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityIndexDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.configuration.generate.config.CaseGenerationConfig;

public class CaseGenerator extends EntityGenerator<CaseGenerationConfig> {

	public void generate(Binder<CaseGenerationConfig> binder) {
		CaseGenerationConfig config = binder.getBean();
		StringBuilder errorMessage = new StringBuilder();
		boolean valid = true;
		if (config.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated cases' in 'Generate Cases'").append("<br>");
			valid = false;
		}
		if (config.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Earliest Case start date' in 'Generate Cases'").append("<br>");
			valid = false;
		}

		if (config.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Latest Case start date' in 'Generate Cases'").append("<br>");
			valid = false;
		}

		if (config.getDistrict() == null) {
			errorMessage.append("You must set a valid value for field 'District of the cases' in 'Generate Cases'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateCases(config);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}

	}

	public void generateCases(CaseGenerationConfig config) {
		initializeRandomGenerator();

		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(config.getStartDate(), config.getEndDate());

		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(config.getRegion());
		facilityCriteria.district(config.getDistrict());

		// just load some health facilities. Alphabetical order is not random, but the best we can get
		List<FacilityIndexDto> healthFacilities = FacadeProvider.getFacilityFacade()
			.getIndexList(facilityCriteria, 0, Math.min(config.getEntityCountAsNumber() * 2, 300), Arrays.asList(new SortProperty(FacilityDto.NAME)));

		// Filter list, so that only health facilities meant for accomodation are selected
		healthFacilities.removeIf(el -> (!el.getType().isAccommodation()));

		long dt = System.nanoTime();

		for (int i = 0; i < config.getEntityCountAsNumber(); i++) {
			Disease disease = config.getDisease();
			if (disease == null) {
				disease = random(diseases);
			}

			fieldVisibilityCheckers =
				FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());

			LocalDateTime referenceDateTime =
				getReferenceDateTime(i, config.getEntityCountAsNumber(), baseOffset, disease, config.getStartDate(), daysBetween);

			// person
			PersonDto person = PersonDto.build();
			fillEntity(person, referenceDateTime);
			person.setSymptomJournalStatus(null);
			setPersonName(person);

			CaseDataDto caze = CaseDataDto.build(person.toReference(), disease);
			fillEntity(caze, referenceDateTime);
			caze.setDisease(disease); // reset
			if (caze.getDisease() == Disease.OTHER) {
				caze.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
			}

			if (!QuarantineType.isQuarantineInEffect(caze.getQuarantine())) {
				caze.setQuarantineFrom(null);
				caze.setQuarantineTo(null);
				caze.setQuarantineExtended(false);
				caze.setQuarantineReduced(false);
			}

			// description
			caze.setAdditionalDetails("Case generated using DevMode on " + LocalDate.now());

			// report
			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			caze.setReportingUser(userReference);
			caze.setReportDate(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			// region & facility
			if (healthFacilities.isEmpty() || randomPercent(20)) {
				FacilityReferenceDto noFacilityRef = FacadeProvider.getFacilityFacade().getByUuid(FacilityDto.NONE_FACILITY_UUID).toReference();
				caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
				caze.setHealthFacility(noFacilityRef);
				caze.setFacilityType(null);
				caze.setResponsibleRegion(config.getRegion());
				caze.setResponsibleDistrict(config.getDistrict());
			} else {
				FacilityIndexDto healthFacility = random(healthFacilities);
				caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
				caze.setResponsibleRegion(healthFacility.getRegion());
				caze.setResponsibleDistrict(healthFacility.getDistrict());
				caze.setResponsibleCommunity(healthFacility.getCommunity());
				caze.setHealthFacility(healthFacility.toReference());
				caze.setFacilityType(healthFacility.getType());
				caze.setReportLat(healthFacility.getLatitude());
				caze.setReportLon(healthFacility.getLongitude());
			}

			FacadeProvider.getPersonFacade().save(person);
			FacadeProvider.getCaseFacade().save(caze);
		}

		dt = System.nanoTime() - dt;
		long perCase = dt / config.getEntityCountAsNumber();
		String msg = String
			.format("Generating %,d cases took %,d  ms (%,d ms per case)", config.getEntityCountAsNumber(), dt / 1_000_000, perCase / 1_000_000);
		logger.info(msg);
		Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
	}
}
