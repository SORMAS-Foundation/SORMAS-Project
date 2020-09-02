/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.CaseReferenceDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactLogic;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictCriteria;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictIndexDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.api.visit.VisitDto;
import de.symeda.sormas.api.visit.VisitStatus;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.DateHelper8;

public class DevModeView extends AbstractConfigurationView {

	private static final long serialVersionUID = -6589135368637794263L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/devMode";

	private VerticalLayout contentLayout;

	private Binder<CaseGenerationConfig> caseGeneratorConfigBinder = new Binder<>();
	private Binder<ContactGenerationConfig> contactGeneratorConfigBinder = new Binder<>();

	public DevModeView() {

		super(VIEW_NAME);

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		contentLayout.setWidth(100, Unit.PERCENTAGE);
		contentLayout.setStyleName("crud-main-layout");

		contentLayout.addComponent(
			new Label(VaadinIcons.INFO_CIRCLE.getHtml() + " " + I18nProperties.getString(Strings.infoDeveloperOptions), ContentMode.HTML));
		contentLayout.addComponent(createCaseGeneratorLayout());
		contentLayout.addComponent(createContactGeneratorLayout());

		addComponent(contentLayout);
	}

	private VerticalLayout createCaseGeneratorLayout() {

		VerticalLayout caseGeneratorLayout = new VerticalLayout();
		caseGeneratorLayout.setMargin(false);
		caseGeneratorLayout.setSpacing(false);

		Label heading = new Label(I18nProperties.getString(Strings.headingGenerateCases));
		CssStyles.style(heading, CssStyles.H2);
		caseGeneratorLayout.addComponent(heading);

		HorizontalLayout caseOptionsLayout = new HorizontalLayout();

		TextField caseCountField = new TextField();
		caseCountField.setCaption(I18nProperties.getCaption(Captions.devModeCaseCount));
		caseGeneratorConfigBinder.forField(caseCountField)
			.withConverter(new StringToIntegerConverter("Must be a number"))
			.bind(CaseGenerationConfig::getCaseCount, CaseGenerationConfig::setCaseCount);
		caseOptionsLayout.addComponent(caseCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeCaseStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		caseGeneratorConfigBinder.bind(startDateField, CaseGenerationConfig::getStartDate, CaseGenerationConfig::setStartDate);
		caseOptionsLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeCaseEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		caseGeneratorConfigBinder.bind(endDateField, CaseGenerationConfig::getEndDate, CaseGenerationConfig::setEndDate);
		caseOptionsLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeCaseDisease));
		caseGeneratorConfigBinder.bind(diseaseField, CaseGenerationConfig::getDisease, CaseGenerationConfig::setDisease);
		caseOptionsLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveAsReference();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<RegionReferenceDto>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeCaseRegion));
		caseGeneratorConfigBinder.bind(regionField, CaseGenerationConfig::getRegion, CaseGenerationConfig::setRegion);
		caseOptionsLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeCaseDistrict));
		caseGeneratorConfigBinder.bind(districtField, CaseGenerationConfig::getDistrict, CaseGenerationConfig::setDistrict);
		caseOptionsLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton = ButtonHelper.createButton(Captions.devModeGenerateCases, e -> generateCases(), CssStyles.FORCE_CAPTION);
		caseOptionsLayout.addComponent(generateButton);

		caseGeneratorLayout.addComponent(caseOptionsLayout);

		CaseGenerationConfig config = new CaseGenerationConfig();
		config.setRegion(regions.get(0));
		caseGeneratorConfigBinder.setBean(config);

		return caseGeneratorLayout;
	}

	private VerticalLayout createContactGeneratorLayout() {

		VerticalLayout contactGeneratorLayout = new VerticalLayout();
		contactGeneratorLayout.setMargin(false);
		contactGeneratorLayout.setSpacing(false);

		Label heading = new Label(I18nProperties.getString(Strings.headingGenerateContacts));
		CssStyles.style(heading, CssStyles.H2);
		contactGeneratorLayout.addComponent(heading);

		HorizontalLayout contactOptionsFirstLineLayout = new HorizontalLayout();

		TextField contactCountField = new TextField();
		contactCountField.setCaption(I18nProperties.getCaption(Captions.devModeContactCount));
		contactGeneratorConfigBinder.forField(contactCountField)
			.withConverter(new StringToIntegerConverter("Must be a number"))
			.bind(ContactGenerationConfig::getContactCount, ContactGenerationConfig::setContactCount);
		contactOptionsFirstLineLayout.addComponent(contactCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeContactStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		contactGeneratorConfigBinder.bind(startDateField, ContactGenerationConfig::getStartDate, ContactGenerationConfig::setStartDate);
		contactOptionsFirstLineLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeContactEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		contactGeneratorConfigBinder.bind(endDateField, ContactGenerationConfig::getEndDate, ContactGenerationConfig::setEndDate);
		contactOptionsFirstLineLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeContactDisease));
		contactGeneratorConfigBinder.bind(diseaseField, ContactGenerationConfig::getDisease, ContactGenerationConfig::setDisease);
		contactOptionsFirstLineLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveAsReference();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<RegionReferenceDto>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeContactRegion));
		contactGeneratorConfigBinder.bind(regionField, ContactGenerationConfig::getRegion, ContactGenerationConfig::setRegion);
		contactOptionsFirstLineLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeContactDistrict));
		contactGeneratorConfigBinder.bind(districtField, ContactGenerationConfig::getDistrict, ContactGenerationConfig::setDistrict);
		contactOptionsFirstLineLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton = ButtonHelper.createButton(Captions.devModeGenerateContacts, e -> generateContacts(), CssStyles.FORCE_CAPTION);
		contactOptionsFirstLineLayout.addComponent(generateButton);

		contactGeneratorLayout.addComponent(contactOptionsFirstLineLayout);

		HorizontalLayout contactOptionsSecondLineLayout = new HorizontalLayout();

		CheckBox createWithoutSourceCasesField = new CheckBox(I18nProperties.getCaption(Captions.devModeContactCreateWithoutSourceCases));
		contactGeneratorConfigBinder.bind(
			createWithoutSourceCasesField,
			ContactGenerationConfig::isCreateWithoutSourceCases,
			ContactGenerationConfig::setCreateWithoutSourceCases);
		contactOptionsSecondLineLayout.addComponent(createWithoutSourceCasesField);

		CheckBox createMultipleContactsPerPersonField =
			new CheckBox(I18nProperties.getCaption(Captions.devModeContactCreateMultipleContactsPerPerson));
		contactGeneratorConfigBinder.bind(
			createMultipleContactsPerPersonField,
			ContactGenerationConfig::isCreateMultipleContactsPerPerson,
			ContactGenerationConfig::setCreateMultipleContactsPerPerson);
		contactOptionsSecondLineLayout.addComponent(createMultipleContactsPerPersonField);

		CheckBox createWithVisitsField = new CheckBox(I18nProperties.getCaption(Captions.devModeContactCreateWithVisits));
		contactGeneratorConfigBinder
			.bind(createWithVisitsField, ContactGenerationConfig::isCreateWithVisits, ContactGenerationConfig::setCreateWithVisits);
		contactOptionsSecondLineLayout.addComponent(createWithVisitsField);

		contactGeneratorLayout.addComponent(contactOptionsSecondLineLayout);

		ContactGenerationConfig config = new ContactGenerationConfig();
		config.setRegion(regions.get(0));
		contactGeneratorConfigBinder.setBean(config);

		return contactGeneratorLayout;
	}

	private final String[] maleFirstNames = new String[] {
		"Nelson",
		"Malik",
		"Thato",
		"Omar",
		"Dion",
		"Darius",
		"Bandile",
		"Demarco" };
	private final String[] femaleFirstNames = new String[] {
		"Ayana",
		"Shaka",
		"Shaniqua",
		"Charlize",
		"Zari",
		"Jayla",
		"Aisha",
		"Iminathi" };
	private final String[] lastNames = new String[] {
		"Ajanlekoko",
		"Omiata",
		"Apeloko",
		"Adisa",
		"Abioye",
		"Chipo",
		"Baako",
		"Akua",
		"Ekua",
		"Katlego",
		"Furaha",
		"Chuks",
		"Babak",
		"Tinibu",
		"Okar",
		"Egwu" };

	private static Random random() {
		return ThreadLocalRandom.current();
	}

	private static boolean randomPercent(int p) {
		return random().nextInt(100) <= p;
	}

	private static <T> T random(List<T> list) {
		return list.get(random().nextInt(list.size()));
	}

	private static <T> T random(T[] a) {
		return a[random().nextInt(a.length)];
	}

	private static Date randomDate(LocalDateTime referenceDateTime) {
		return Date.from(referenceDateTime.plusMinutes(random().nextInt(60 * 24 * 5)).atZone(ZoneId.systemDefault()).toInstant());
	}

	private void fillEntity(EntityDto entity, LocalDateTime referenceDateTime) {

		try {
			Class<? extends EntityDto> entityClass = entity.getClass();
			List<Method> setters = setters(entityClass);
			for (Method setter : setters) {
				if (randomPercent(40)) {
					continue; // leave some empty/default
				}
				Class<?> parameterType = setter.getParameterTypes()[0];
				// doesn't make sense
				//				if (parameterType.isAssignableFrom(String.class)) {
				//					setter.invoke(entity, words[random.nextInt(words.length)]);
				//				} 
				//				else 
				if (parameterType.isAssignableFrom(Date.class)) {
					setter.invoke(entity, randomDate(referenceDateTime));
				} else if (parameterType.isEnum()) {
					Object[] enumConstants;
					// Only use active primary diseases
					enumConstants = getEnumConstants(parameterType);
					// Generate more living persons
					if (parameterType == PresentCondition.class && randomPercent(50)) {
						setter.invoke(entity, PresentCondition.ALIVE);
					} else {
						setter.invoke(entity, random(enumConstants));
					}
				} else if (EntityDto.class.isAssignableFrom(parameterType)) {
					getter(setter).ifPresent(g -> {
						Object subEntity;
						try {
							subEntity = g.invoke(entity);
							if (subEntity instanceof EntityDto) {
								fillEntity((EntityDto) subEntity, referenceDateTime);
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException(e.getMessage(), e);
						}
					});
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	Object[] diseaseEnumConstants;

	private Object[] getEnumConstants(Class<?> parameterType) {
		if (parameterType == Disease.class) {
			if (diseaseEnumConstants == null) {
				diseaseEnumConstants = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray();
			}
			return diseaseEnumConstants;
		} else {
			return parameterType.getEnumConstants();
		}
	}

	private Map<Class<? extends EntityDto>, List<Method>> setters = new HashMap<>();
	private Map<Method, Optional<Method>> getters = new HashMap<>();

	private List<Method> setters(Class<? extends EntityDto> entityClass) {

		return setters.computeIfAbsent(
			entityClass,
			c -> Arrays.stream(c.getDeclaredMethods())
				.filter(method -> method.getName().startsWith("set") && method.getParameterTypes().length == 1)
				.collect(Collectors.toList()));
	}

	private Optional<Method> getter(Method setter) throws NoSuchMethodException {

		return getters.computeIfAbsent(setter, s -> {
			try {
				return Optional.of(s.getDeclaringClass().getDeclaredMethod(s.getName().replaceFirst("set", "get")));
			} catch (NoSuchMethodException | SecurityException e) {
				return Optional.empty();
			}
		});
	}

	private void generateCases() {

		CaseGenerationConfig config = caseGeneratorConfigBinder.getBean();

		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(config.startDate, config.endDate);

		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(config.getRegion());
		facilityCriteria.district(config.getDistrict());
		// just load some health facilities. Alphabetical order is not random, but the best we can get
		List<FacilityDto> healthFacilities = FacadeProvider.getFacilityFacade()
			.getIndexList(facilityCriteria, 0, Math.min(config.getCaseCount() * 2, 300), Arrays.asList(new SortProperty(FacilityDto.NAME)));

		for (int i = 0; i < config.getCaseCount(); i++) {
			Disease disease = config.getDisease();
			if (disease == null) {
				disease = random(diseases);
			}

			LocalDateTime referenceDateTime = getReferenceDateTime(i, config.getCaseCount(), baseOffset, disease, config.getStartDate(), daysBetween);

			// person
			PersonDto person = PersonDto.build();
			fillEntity(person, referenceDateTime);
			setPersonName(person);

			CaseDataDto caze = CaseDataDto.build(person.toReference(), disease);
			fillEntity(caze, referenceDateTime);
			caze.setDisease(disease); // reset
			if (caze.getDisease() == Disease.OTHER) {
				caze.setDiseaseDetails("RD " + (random().nextInt(20) + 1));
			}

			// report
			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			caze.setReportingUser(userReference);
			caze.setReportDate(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			// region & facility
			FacilityDto healthFacility = random(healthFacilities);
			caze.setCaseOrigin(CaseOrigin.IN_COUNTRY);
			caze.setRegion(healthFacility.getRegion());
			caze.setDistrict(healthFacility.getDistrict());
			caze.setCommunity(healthFacility.getCommunity());
			caze.setHealthFacility(healthFacility.toReference());
			caze.setReportLat(healthFacility.getLatitude());
			caze.setReportLon(healthFacility.getLongitude());

			FacadeProvider.getPersonFacade().savePerson(person);
			FacadeProvider.getCaseFacade().saveCase(caze);
		}
	}

	private void generateContacts() {

		ContactGenerationConfig config = contactGeneratorConfigBinder.getBean();

		List<Disease> diseases = config.getDisease() == null ? FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true) : null;
		List<String> personUuids = new ArrayList<>();
		List<CaseReferenceDto> cases = null;
		List<DistrictIndexDto> districts = config.getDistrict() == null
			? FacadeProvider.getDistrictFacade()
				.getIndexList(
					new DistrictCriteria().region(config.getRegion()),
					0,
					Math.min(config.getContactCount() * 2, 50),
					Arrays.asList(new SortProperty(DistrictDto.NAME)))
			: null;
		if (!config.isCreateWithoutSourceCases()) {
			cases = FacadeProvider.getCaseFacade()
				.getRandomCaseReferences(
					new CaseCriteria().region(config.getRegion()).district(config.getDistrict()).disease(config.getDisease()),
					config.getContactCount() * 2);
		}

		float baseOffset = random().nextFloat();
		int daysBetween = (int) ChronoUnit.DAYS.between(config.startDate, config.endDate);

		for (int i = 0; i < config.getContactCount(); i++) {
			Disease disease = config.getDisease();
			if (disease == null) {
				disease = random(diseases);
			}

			LocalDateTime referenceDateTime =
				getReferenceDateTime(i, config.getContactCount(), baseOffset, disease, config.getStartDate(), daysBetween);

			PersonDto person;
			if (config.isCreateMultipleContactsPerPerson() && !personUuids.isEmpty() && randomPercent(25)) {
				String personUuid = random(personUuids);
				person = FacadeProvider.getPersonFacade().getPersonByUuid(personUuid);
			} else {
				person = PersonDto.build();
				fillEntity(person, referenceDateTime);
				setPersonName(person);

				if (config.isCreateMultipleContactsPerPerson()) {
					personUuids.add(person.getUuid());
				}
			}

			CaseReferenceDto contactCase = null;
			if (!config.isCreateWithoutSourceCases()) {
				contactCase = random(cases);
			}

			ContactDto contact = ContactDto.build();
			contact.setPerson(person.toReference());
			fillEntity(contact, referenceDateTime);
			if (contactCase != null) {
				contact.setCaze(contactCase);
			}
			contact.setDisease(config.getDisease());
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
				contact.setRegion(config.getRegion());
				contact.setDistrict(config.getDistrict());
			}

			if (contact.getLastContactDate() != null && contact.getLastContactDate().after(contact.getReportDateTime())) {
				contact.setLastContactDate(contact.getReportDateTime());
			}
			if (FollowUpStatus.CANCELED.equals(contact.getFollowUpStatus()) || FollowUpStatus.LOST.equals(contact.getFollowUpStatus())) {
				contact.setFollowUpComment("-");
			}

			FacadeProvider.getPersonFacade().savePerson(person);
			contact = FacadeProvider.getContactFacade().saveContact(contact);

			if (FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(contact.getDisease())) {
				contact.setFollowUpStatus(random(FollowUpStatus.values()));
			} else {
				contact.setFollowUpStatus(FollowUpStatus.NO_FOLLOW_UP);
			}
			contact.setFollowUpUntil(contact.getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP ? null : randomDate(referenceDateTime));

			// Create visits
			if (config.isCreateWithVisits()
				&& FacadeProvider.getDiseaseConfigurationFacade().hasFollowUp(contact.getDisease())
				&& FollowUpStatus.NO_FOLLOW_UP != contact.getFollowUpStatus()) {
				Date latestFollowUpDate = contact.getFollowUpUntil().before(new Date()) ? contact.getFollowUpUntil() : new Date();
				Date contactStartDate = ContactLogic.getStartDate(contact.getLastContactDate(), contact.getReportDateTime());
				int followUpCount = random().nextInt(DateHelper.getDaysBetween(contactStartDate, latestFollowUpDate) + 1);
				if (followUpCount > 0) {
					int[] followUpDays = new Random().ints(1, followUpCount + 1).distinct().limit(followUpCount).toArray();
					List<LocalDateTime> followUpDates = new ArrayList<>();
					for (int day : followUpDays) {
						followUpDates.add(
							DateHelper8.toLocalDate(contactStartDate).atStartOfDay().plusDays(day - 1).plusMinutes(random().nextInt(60 * 24 + 1)));
					}

					for (LocalDateTime date : followUpDates) {
						VisitDto visit = VisitDto.build(contact.getPerson(), contact.getDisease());
						fillEntity(visit, date);
						visit.setVisitUser(userReference);
						visit.setVisitDateTime(DateHelper8.toDate(date));
						visit.setDisease(contact.getDisease());
						if (visit.getVisitStatus() == null) {
							visit.setVisitStatus(VisitStatus.COOPERATIVE);
						}
						FacadeProvider.getVisitFacade().saveVisit(visit);
					}
				}
			}
		}
	}

	private LocalDateTime getReferenceDateTime(int i, int count, float baseOffset, Disease disease, LocalDate startDate, int daysBetween) {

		float x = (float) i / count;
		x += baseOffset;
		x += 0.13f * disease.ordinal();
		x += 0.5f * random().nextFloat();
		x = (float) (Math.asin((x % 2) - 1) / Math.PI / 2) + 0.5f;

		return startDate.atStartOfDay().plusMinutes((int) (x * 60 * 24 * daysBetween));
	}

	private void setPersonName(PersonDto person) {

		Sex sex = Sex.values()[random().nextInt(2)];
		person.setSex(sex);
		if (sex == Sex.MALE) {
			person.setFirstName(random(maleFirstNames) + " " + random(maleFirstNames));
		} else {
			person.setFirstName(random(femaleFirstNames) + " " + random(femaleFirstNames));
		}
		person.setLastName(random(lastNames) + "-" + random(lastNames));

	}

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}

	private static class CaseGenerationConfig {

		private int caseCount = 10;
		private LocalDate startDate = LocalDate.now().minusDays(90);
		private LocalDate endDate = LocalDate.now();
		private Disease disease = null;
		private RegionReferenceDto region = null;
		private DistrictReferenceDto district = null;

		public int getCaseCount() {
			return caseCount;
		}

		public void setCaseCount(int caseCount) {
			this.caseCount = caseCount;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}

		public Disease getDisease() {
			return disease;
		}

		public void setDisease(Disease disease) {
			this.disease = disease;
		}

		public RegionReferenceDto getRegion() {
			return region;
		}

		public void setRegion(RegionReferenceDto region) {
			this.region = region;
		}

		public DistrictReferenceDto getDistrict() {
			return district;
		}

		public void setDistrict(DistrictReferenceDto district) {
			this.district = district;
		}
	}

	private static class ContactGenerationConfig {

		private int contactCount = 10;
		private LocalDate startDate = LocalDate.now().minusDays(90);
		private LocalDate endDate = LocalDate.now();
		private Disease disease = null;
		private RegionReferenceDto region = null;
		private DistrictReferenceDto district = null;
		private boolean createWithoutSourceCases = false;
		private boolean createMultipleContactsPerPerson = false;
		private boolean createWithVisits = false;

		public int getContactCount() {
			return contactCount;
		}

		public void setContactCount(int contactCount) {
			this.contactCount = contactCount;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDate startDate) {
			this.startDate = startDate;
		}

		public LocalDate getEndDate() {
			return endDate;
		}

		public void setEndDate(LocalDate endDate) {
			this.endDate = endDate;
		}

		public Disease getDisease() {
			return disease;
		}

		public void setDisease(Disease disease) {
			this.disease = disease;
		}

		public RegionReferenceDto getRegion() {
			return region;
		}

		public void setRegion(RegionReferenceDto region) {
			this.region = region;
		}

		public DistrictReferenceDto getDistrict() {
			return district;
		}

		public void setDistrict(DistrictReferenceDto district) {
			this.district = district;
		}

		public boolean isCreateWithoutSourceCases() {
			return createWithoutSourceCases;
		}

		public void setCreateWithoutSourceCases(boolean createWithoutSourceCases) {
			this.createWithoutSourceCases = createWithoutSourceCases;
		}

		public boolean isCreateMultipleContactsPerPerson() {
			return createMultipleContactsPerPerson;
		}

		public void setCreateMultipleContactsPerPerson(boolean createMultipleContactsPerPerson) {
			this.createMultipleContactsPerPerson = createMultipleContactsPerPerson;
		}

		public boolean isCreateWithVisits() {
			return createWithVisits;
		}

		public void setCreateWithVisits(boolean createWithVisits) {
			this.createWithVisits = createWithVisits;
		}
	}
}
