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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.facility.FacilityCriteria;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

public class DevModeView extends AbstractConfigurationView {

	private static final long serialVersionUID = -6589135368637794263L;

	public static final String VIEW_NAME = ROOT_VIEW_NAME + "/devMode";

	private VerticalLayout contentLayout;

	private Binder<CaseGenerationConfig> caseGeneratorConfigBinder = new Binder<>();

	public DevModeView() {
		super(VIEW_NAME);

		contentLayout = new VerticalLayout();
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		contentLayout.setSizeFull();
		contentLayout.setStyleName("crud-main-layout");

		HorizontalLayout caseGeneratorLayout = new HorizontalLayout();

		TextField caseCountField = new TextField();
		caseCountField.setCaption(I18nProperties.getCaption(Captions.devModeCaseCount));
		caseGeneratorConfigBinder.forField(caseCountField)
		.withConverter(new StringToIntegerConverter("Must be a number"))
		.bind(CaseGenerationConfig::getCaseCount, CaseGenerationConfig::setCaseCount);
		caseGeneratorLayout.addComponent(caseCountField);

		DateField startDateField = new DateField();
		startDateField.setCaption(I18nProperties.getCaption(Captions.devModeStartDate));
		startDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		startDateField.setLenient(true);
		caseGeneratorConfigBinder.bind(startDateField, CaseGenerationConfig::getStartDate, CaseGenerationConfig::setStartDate);
		caseGeneratorLayout.addComponent(startDateField);

		DateField endDateField = new DateField();
		endDateField.setCaption(I18nProperties.getCaption(Captions.devModeEndDate));
		endDateField.setDateFormat(DateFormatHelper.getDateFormatPattern());
		endDateField.setLenient(true);
		caseGeneratorConfigBinder.bind(endDateField, CaseGenerationConfig::getEndDate, CaseGenerationConfig::setEndDate);
		caseGeneratorLayout.addComponent(endDateField);

		ComboBox<Disease> diseaseField = new ComboBox<>(null, FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true));
		diseaseField.setCaption(I18nProperties.getCaption(Captions.devModeDisease));
		caseGeneratorConfigBinder.bind(diseaseField, CaseGenerationConfig::getDisease, CaseGenerationConfig::setDisease);
		caseGeneratorLayout.addComponent(diseaseField);

		List<RegionReferenceDto> regions = FacadeProvider.getRegionFacade().getAllActiveAsReference();
		ComboBox<RegionReferenceDto> regionField = new ComboBox<RegionReferenceDto>(null, regions);
		regionField.setCaption(I18nProperties.getCaption(Captions.devModeRegion));
		caseGeneratorConfigBinder.bind(regionField, CaseGenerationConfig::getRegion, CaseGenerationConfig::setRegion);
		caseGeneratorLayout.addComponent(regionField);

		ComboBox<DistrictReferenceDto> districtField = new ComboBox<DistrictReferenceDto>();
		districtField.setCaption(I18nProperties.getCaption(Captions.devModeDistrict));
		caseGeneratorConfigBinder.bind(districtField, CaseGenerationConfig::getDistrict, CaseGenerationConfig::setDistrict);
		caseGeneratorLayout.addComponent(districtField);

		regionField.addValueChangeListener(event -> {
			RegionReferenceDto region = event.getValue();
			if (region != null) {
				districtField.setItems(FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid()));
			} else {
				districtField.setItems(new ArrayList<DistrictReferenceDto>());
			}
		});

		Button generateButton = new Button("generate cases");
		CssStyles.style(generateButton, CssStyles.FORCE_CAPTION);
		generateButton.addClickListener(e -> generateCases());
		caseGeneratorLayout.addComponent(generateButton);

		contentLayout.addComponent(caseGeneratorLayout);

		CaseGenerationConfig config = new CaseGenerationConfig();
		config.setRegion(regions.get(0));
		caseGeneratorConfigBinder.setBean(config);

		addComponent(contentLayout);
	}

	private final String[] maleFirstNames = new String[] {
			"Nelson", "Malik", "Thato", "Omar", "Dion", "Darius", "Bandile", "Demarco" };
	private final String[] femaleFirstNames = new String[] { 
			"Ayana", "Shaka", "Shaniqua", "Charlize", "Zari", "Jayla", "Aisha", "Iminathi"};
	private final String[] lastNames = new String[] { 
			"Ajanlekoko", "Omiata", "Apeloko", "Adisa", "Abioye", "Chipo", "Baako", "Akua", 
			"Ekua", "Katlego", "Furaha", "Chuks", "Babak", "Tinibu", "Okar", "Egwu" };
	//	private final String[] words = new String[] {
	//		    "gold","snail","card","taste","hanging","materialistic","wasteful","lamp","wry","rhetorical","scene","unlock" };

	public void fillEntity(EntityDto entity, LocalDateTime referenceDateTime) {
		Random random = new Random();
		try {
			Class<? extends EntityDto> entityClass = entity.getClass();
			List<Method> setters = Arrays.stream(entityClass.getDeclaredMethods())
					.filter(method -> method.getName().startsWith("set") && method.getParameterTypes().length == 1)
					.collect(Collectors.toList());
			for (Method setter : setters) {
				if (random.nextInt(10) > 6) {
					continue; // leave some empty/default
				}
				Class<?> parameterType = setter.getParameterTypes()[0];
				// doesn't make sense
				//				if (parameterType.isAssignableFrom(String.class)) {
				//					setter.invoke(entity, words[random.nextInt(words.length)]);
				//				} 
				//				else 
				if (parameterType.isAssignableFrom(Date.class)) {
					setter.invoke(entity, Date.from(referenceDateTime.plusMinutes(random.nextInt(60*24*5))
							.atZone(ZoneId.systemDefault()).toInstant()));
				}
				else if (parameterType.isEnum()) {
					Object[] enumConstants = null;
					// Only use active primary diseases
					if (parameterType == Disease.class) {
						enumConstants = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true).toArray();
					} else {
						enumConstants = parameterType.getEnumConstants();
					}
					// Generate more living persons
					if (parameterType == PresentCondition.class && random.nextInt(10) <= 5) {
						setter.invoke(entity, PresentCondition.ALIVE);
					} else {
						setter.invoke(entity, enumConstants[random.nextInt(enumConstants.length)]);
					}
				}
				else if (EntityDto.class.isAssignableFrom(parameterType)) {
					Method getter = entityClass.getDeclaredMethod(setter.getName().replaceFirst("set", "get"));
					if (getter != null) {
						Object subEntity = getter.invoke(entity);
						if (subEntity instanceof EntityDto) {
							fillEntity((EntityDto)subEntity, referenceDateTime);
						}
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public void generateCases() {
		CaseGenerationConfig config = caseGeneratorConfigBinder.getBean();

		List<Disease> diseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		Random random = new Random();
		float baseOffset = random.nextFloat();
		int daysBetween = (int)ChronoUnit.DAYS.between(config.startDate, config.endDate);

		FacilityCriteria facilityCriteria = new FacilityCriteria();
		facilityCriteria.region(config.getRegion());
		facilityCriteria.district(config.getDistrict());
		// just load some health facilities. Alphabetical order is not random, but the best we can get
		List<FacilityDto> healthFacilities = FacadeProvider.getFacilityFacade().getIndexList(facilityCriteria, 0, Math.min(config.getCaseCount()*2, 300), Arrays.asList(new SortProperty(FacilityDto.NAME)));

		for (int i=0; i<config.getCaseCount(); i++) {

			Disease disease = config.getDisease();
			if (disease == null) {
				int rnd = random.nextInt(diseases.size());
				disease = diseases.get(rnd);
			}

			float x = (float)i/config.getCaseCount();
			x += baseOffset;
			x += 0.13f * disease.ordinal();
			x += 0.5f * random.nextFloat();
			x = (float)(Math.asin((x % 2 ) - 1) / Math.PI/2) + 0.5f;

			LocalDateTime referenceDateTime = config.getStartDate().atStartOfDay().plusMinutes((int)(x*60*24*daysBetween));

			// person
			PersonDto person = PersonDto.build();
			fillEntity(person, referenceDateTime);

			Sex sex = Sex.values()[random.nextInt(2)];
			person.setSex(sex);
			if (sex == Sex.MALE) {
				person.setFirstName(maleFirstNames[random.nextInt(maleFirstNames.length)] + " " + maleFirstNames[random.nextInt(maleFirstNames.length)]);
			} else {
				person.setFirstName(femaleFirstNames[random.nextInt(femaleFirstNames.length)] + " " + femaleFirstNames[random.nextInt(femaleFirstNames.length)]);
			}
			person.setLastName(lastNames[random.nextInt(lastNames.length)] + "-" + lastNames[random.nextInt(lastNames.length)]);

			CaseDataDto caze = CaseDataDto.build(person.toReference(), disease);
			fillEntity(caze, referenceDateTime);
			caze.setDisease(disease); // reset
			if (caze.getDisease() == Disease.OTHER) {
				caze.setDiseaseDetails("RD " + (random.nextInt(20) + 1));
			}

			// report
			UserReferenceDto userReference = UserProvider.getCurrent().getUserReference();
			caze.setReportingUser(userReference);
			caze.setReportDate(Date.from(referenceDateTime.atZone(ZoneId.systemDefault()).toInstant()));

			// region & facility
			FacilityDto healthFacility = healthFacilities.get(random.nextInt(healthFacilities.size()));
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

	@Override
	public void enter(ViewChangeEvent event) {
		super.enter(event);
	}

	public static class CaseGenerationConfig {
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
}
