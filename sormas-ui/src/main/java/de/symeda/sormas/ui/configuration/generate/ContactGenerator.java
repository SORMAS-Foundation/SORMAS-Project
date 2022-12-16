package de.symeda.sormas.ui.configuration.generate;

import de.symeda.sormas.ui.configuration.generate.config.ContactGenerationConfig;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.ui.Notification;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.VisitOrigin;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.district.DistrictCriteria;
import de.symeda.sormas.api.infrastructure.district.DistrictDto;
import de.symeda.sormas.api.infrastructure.district.DistrictIndexDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.utils.UtilDate;
import de.symeda.sormas.api.utils.ValidationRuntimeException;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.UserProvider;

public class ContactGenerator extends EntityGenerator<ContactGenerationConfig> {

	public void generate(Binder<ContactGenerationConfig> binder) {
		ContactGenerationConfig contactGenerationConfig = binder.getBean();
		boolean valid = true;
		StringBuilder errorMessage = new StringBuilder();

		if (contactGenerationConfig.getEntityCountAsNumber() <= 0) {
			errorMessage.append("You must set a valid value for field 'Number of generated contacts' in 'Generate Contacts'").append("<br>");
			valid = false;
		}
		if (contactGenerationConfig.getStartDate() == null) {
			errorMessage.append("You must set a valid value for field 'Earliest Contact start date' in 'Generate Contacts'").append("<br>");
			valid = false;
		}

		if (contactGenerationConfig.getEndDate() == null) {
			errorMessage.append("You must set a valid value for field 'Latest Contact start date'  in 'Generate Contacts'").append("<br>");
			valid = false;
		}

		if (valid) {
			generateContacts(contactGenerationConfig);
		} else {
			throw new ValidationRuntimeException(errorMessage.toString());
		}
	}

	public void generateContacts(ContactGenerationConfig contactGenerationConfig) {
		initializeRandomGenerator();

		Disease disease = contactGenerationConfig.getDisease();
		List<Disease> diseases = disease == null ? FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true) : null;

		if (disease == null) {
			disease = random(diseases);
			Notification.show("", "Automatically chosen disease: " + disease.getName(), Notification.Type.TRAY_NOTIFICATION);
		}
		List<String> personUuids = new ArrayList<>();
		List<CaseReferenceDto> cases = null;
		List<DistrictIndexDto> districts = contactGenerationConfig.getDistrict() == null
			? FacadeProvider.getDistrictFacade()
				.getIndexList(
					new DistrictCriteria().region(contactGenerationConfig.getRegion()),
					0,
					Math.min(contactGenerationConfig.getEntityCountAsNumber() * 2, 50),
					Arrays.asList(new SortProperty(DistrictDto.NAME)))
			: null;
		if (!contactGenerationConfig.isCreateWithoutSourceCases()) {
			cases = FacadeProvider.getCaseFacade()
				.getRandomCaseReferences(
					new CaseCriteria().region(contactGenerationConfig.getRegion()).district(contactGenerationConfig.getDistrict()).disease(disease),
					contactGenerationConfig.getEntityCountAsNumber() * 2,
					random());
			if (cases == null) {
				Notification.show("Error", I18nProperties.getString(Strings.messageMissingCases), Notification.Type.ERROR_MESSAGE);
				return;
			}
		}

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(contactGenerationConfig.getStartDate(), contactGenerationConfig.getEndDate());

		long dt = System.nanoTime();

		for (int i = 0; i < contactGenerationConfig.getEntityCountAsNumber(); i++) {
			fieldVisibilityCheckers =
				FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale());

			LocalDateTime referenceDateTime = getReferenceDateTime(
				i,
				contactGenerationConfig.getEntityCountAsNumber(),
				baseOffset,
				disease,
				contactGenerationConfig.getStartDate(),
				daysBetween);

			PersonDto person;
			if (contactGenerationConfig.isCreateMultipleContactsPerPerson() && !personUuids.isEmpty() && randomPercent(25)) {
				String personUuid = random(personUuids);
				person = FacadeProvider.getPersonFacade().getByUuid(personUuid);
			} else {
				person = PersonDto.build();
				fillEntity(person, referenceDateTime);
				person.setSymptomJournalStatus(null);
				setPersonName(person);

				if (contactGenerationConfig.isCreateMultipleContactsPerPerson()) {
					personUuids.add(person.getUuid());
				}
			}

			CaseReferenceDto contactCase = null;
			if (!contactGenerationConfig.isCreateWithoutSourceCases()) {
				contactCase = random(cases);
			}

			ContactDto contact = ContactDto.build();
			contact.setPerson(person.toReference());
			fillEntity(contact, referenceDateTime);
			if (contactCase != null) {
				contact.setCaze(contactCase);
			}
			contact.setDisease(disease);
			if (contact.getDisease() == Disease.OTHER) {
				contact.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
			}

			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			contact.setReportingUser(userReference);
			contact.setReportDateTime(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			if (districts != null) {
				DistrictIndexDto district = random(districts);
				contact.setRegion(district.getRegion());
				contact.setDistrict(district.toReference());
			} else {
				contact.setRegion(contactGenerationConfig.getRegion());
				contact.setDistrict(contactGenerationConfig.getDistrict());
			}

			if (contact.getLastContactDate() != null && contact.getLastContactDate().after(contact.getReportDateTime())) {
				contact.setLastContactDate(contact.getReportDateTime());
			}
			if (FollowUpStatus.CANCELED.equals(contact.getFollowUpStatus()) || FollowUpStatus.LOST.equals(contact.getFollowUpStatus())) {
				contact.setFollowUpComment("-");
			}

			// description
			contact.setDescription("Contact generated using DevMode on " + LocalDate.now());

			FacadeProvider.getPersonFacade().save(person);
			contact = FacadeProvider.getContactFacade().save(contact);

			if (FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(contact.getDisease())) {
				contact.setFollowUpStatus(random(FollowUpStatus.values()));
			} else {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
			contact.setFollowUpUntil(contact.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP ? null : randomDate(referenceDateTime));

			// Create visits
			if (contactGenerationConfig.isCreateWithVisits()
				&& FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(contact.getDisease())
				&& FollowUpStatus.NO_FOLLOW_UP != contact.getFollowUpStatus()) {
				Date latestFollowUpDate = contact.getFollowUpUntil().before(new Date()) ? contact.getFollowUpUntil() : new Date();
				Date contactStartDate = ContactLogic.getStartDate(contact);
				int followUpCount = random().nextInt(DateHelper.getDaysBetween(contactStartDate, latestFollowUpDate) + 1);
				if (followUpCount > 0) {
					int[] followUpDays = random().ints(1, followUpCount + 1).distinct().limit(followUpCount).toArray();
					List<LocalDateTime> followUpDates = new ArrayList<>();
					for (int day : followUpDays) {
						followUpDates
							.add(UtilDate.toLocalDate(contactStartDate).atStartOfDay().plusDays(day - 1).plusMinutes(random().nextInt(60 * 24 + 1)));
					}

					for (LocalDateTime date : followUpDates) {
						VisitDto visit = VisitDto.build(contact.getPerson(), contact.getDisease(), VisitOrigin.USER);
						fillEntity(visit, date);
						visit.setVisitUser(userReference);
						visit.setVisitDateTime(UtilDate.from(date));
						visit.setDisease(contact.getDisease());
						if (visit.getVisitStatus() == null) {
							visit.setVisitStatus(VisitStatus.COOPERATIVE);
						}
						FacadeProvider.getVisitFacade().saveVisit(visit);
					}
				}
			}
		}

		dt = System.nanoTime() - dt;
		long perContact = dt / contactGenerationConfig.getEntityCountAsNumber();
		String msg = String.format(
			"Generating %,d contacts took %,d  ms (%,d ms per contact)",
			contactGenerationConfig.getEntityCountAsNumber(),
			dt / 1_000_000,
			perContact / 1_000_000);
		logger.info(msg);
		Notification.show("", msg, Notification.Type.TRAY_NOTIFICATION);
	}
}
