/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.immunization.components.form;

import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.data.Result;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.converter.Converter;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.VaccinationInfoSource;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.immunization.ImmunizationDto;
import de.symeda.sormas.api.immunization.MeansOfImmunization;
import de.symeda.sormas.api.utils.Diseases;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.vaccination.VaccinationDto;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FormComponent;

/**
 * Simplified "Quick Immunization Entry" form showing only the 7 core fields (BR0070).
 * Jurisdiction fields (responsibleRegion, responsibleDistrict) are not shown; they must be
 * pre-set on the DTO before calling {@link #setValue(ImmunizationDto)}.
 * <p>
 * Extends {@link AbstractEditForm} for compatibility with
 * {@link de.symeda.sormas.ui.utils.CommitDiscardWrapperComponent}.
 * The FieldGroup is kept hollow; all data binding is handled by an embedded
 * Vaadin 8 {@link FormComponent} that owns its own {@link Binder}.
 */
@SuppressWarnings({
	"java:S2160", // suppress equals warning
	"java:S110" // suppress too many parents
})
public class QuickImmunizationCreationForm extends AbstractEditForm<ImmunizationDto> {

	private static final long serialVersionUID = 3981267040395726408L;

	private static final String FIELDS_COMPONENT_LOC = "fieldsComponentLoc";
	private static final String HTML_LAYOUT = loc(FIELDS_COMPONENT_LOC);

	private FieldsComponent fieldsComponent;

	public QuickImmunizationCreationForm() {
		super(
			ImmunizationDto.class,
			ImmunizationDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withCountry(FacadeProvider.getConfigFacade().getCountryLocale()));
		setWidth(540, Unit.PIXELS);
		fieldsComponent = new FieldsComponent();
		addFields();
		hideValidationUntilNextCommit();
	}

	@Override
	protected String createHtmlLayout() {
		return HTML_LAYOUT;
	}

	@Override
	protected void addFields() {
		getContent().addComponent(fieldsComponent, FIELDS_COMPONENT_LOC);
	}

	@Override
	public void setValue(ImmunizationDto newFieldValue) throws com.vaadin.v7.data.Property.ReadOnlyException, Converter.ConversionException {
		super.setValue(newFieldValue);
		fieldsComponent.setDto(newFieldValue);
	}

	@Override
	public void preCommit(CommitEvent commitEvent) throws CommitException {
		super.preCommit(commitEvent);
		fieldsComponent.validate(); // throws InvalidValueException (RuntimeException) on failure
	}

	public Date getDateOfMostRecentDose() {
		return fieldsComponent.getDateOfMostRecentDose();
	}

	public VaccinationInfoSource getVaccinationInfoSource() {
		return fieldsComponent.getVaccinationInfoSource();
	}

	// -------------------------------------------------------------------------
	// Inner component — all Vaadin 8 fields
	// -------------------------------------------------------------------------

	@SuppressWarnings({
		"java:S110", // suppress sonar too many parents warning
		"java:S2160" }) // suppress sonar missing equals
	private static final class FieldsComponent extends FormComponent<ImmunizationDto> {

		private static final long serialVersionUID = 1L;

		private DateField reportDate;
		private ComboBox<MeansOfImmunization> meansOfImmunization;
		private TextField numberOfDoses;
		private DateField dateOfMostRecentDose;
		private ComboBox<VaccinationInfoSource> vaccinationInfoSource;
		private DateField validFrom;
		private DateField validUntil;
		private Label validityWarningLabel;

		FieldsComponent() {
			super(ImmunizationDto.class);
			buildLayout();
			bindFields();
			wireEvents();
			// Vaccination-specific fields (including validity dates) are hidden until the
			// user selects a vaccination means of immunization.
			updateVaccinationFieldsVisibility(null);
		}

		@Override
		public void setDto(ImmunizationDto dto) {
			super.setDto(dto);
			refreshMeansOfImmunizationOptions(dto != null ? dto.getDisease() : null);
			// binder.setBean() only fires value-change events when the value actually
			// changes. If meansOfImmunization was already null (typical for a new DTO),
			// no event fires and the listener never runs. Re-apply visibility and
			// suggestions explicitly so the form is always in a consistent state.
			updateVaccinationFieldsVisibility(meansOfImmunization.getValue());
			if (MeansOfImmunization.isVaccination(meansOfImmunization.getValue())
				&& meansOfImmunization.getValue() != MeansOfImmunization.MATERNAL_VACCINATION) {
				refreshValiditySuggestions();
			}
		}

		private void buildLayout() {
			reportDate = createDateField(ImmunizationDto.REPORT_DATE, ImmunizationDto.I18N_PREFIX);

			meansOfImmunization = createComboBox(ImmunizationDto.MEANS_OF_IMMUNIZATION, ImmunizationDto.I18N_PREFIX);
			refreshMeansOfImmunizationOptions(null);
			meansOfImmunization.setItemCaptionGenerator(I18nProperties::getEnumCaption);
			addRow(reportDate, meansOfImmunization);

			numberOfDoses = createTextField(ImmunizationDto.NUMBER_OF_DOSES, ImmunizationDto.I18N_PREFIX);
			addRow(numberOfDoses, null);

			dateOfMostRecentDose = new DateField();
			dateOfMostRecentDose.setId("dateOfMostRecentDose");
			dateOfMostRecentDose.setCaption(I18nProperties.getCaption(Captions.quickImmunizationDateOfMostRecentDose));
			CssStyles.style(dateOfMostRecentDose, CssStyles.CAPTION_ON_TOP);
			dateOfMostRecentDose.setWidth(100, Unit.PERCENTAGE);
			dateOfMostRecentDose.setLocale(I18nProperties.getUserLanguage().getLocale());
			dateOfMostRecentDose.setDateFormat(DateFormatHelper.getDateFormatPattern());

			vaccinationInfoSource = new ComboBox<>();
			vaccinationInfoSource.setId("vaccinationInfoSource");
			vaccinationInfoSource.setCaption(I18nProperties.getPrefixCaption(VaccinationDto.I18N_PREFIX, VaccinationDto.VACCINATION_INFO_SOURCE));
			vaccinationInfoSource.setItems(Arrays.asList(VaccinationInfoSource.values()));
			vaccinationInfoSource.setItemCaptionGenerator(I18nProperties::getEnumCaption);
			CssStyles.style(vaccinationInfoSource, CssStyles.CAPTION_ON_TOP);
			vaccinationInfoSource.setWidth(100, Unit.PERCENTAGE);
			addRow(dateOfMostRecentDose, vaccinationInfoSource);

			validFrom = createDateField(ImmunizationDto.VALID_FROM, ImmunizationDto.I18N_PREFIX);
			validUntil = createDateField(ImmunizationDto.VALID_UNTIL, ImmunizationDto.I18N_PREFIX);
			addRow(validFrom, validUntil);

			validityWarningLabel = new Label(I18nProperties.getCaption(Captions.quickImmunizationValidityDatesCannotBeCalculated));
			validityWarningLabel.setId("validityDatesWarning");
			validityWarningLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(validityWarningLabel, CssStyles.LABEL_WARNING, CssStyles.LABEL_WHITE_SPACE_NORMAL);
			validityWarningLabel.setVisible(false);
			addComponent(validityWarningLabel);
		}

		private void bindFields() {
			binder.forField(reportDate)
				.asRequired(I18nProperties.getValidationError(Validations.required, reportDate.getCaption()))
				.withConverter(new LocalDateToDateConverter())
				.bind(ImmunizationDto::getReportDate, ImmunizationDto::setReportDate);

			binder.forField(meansOfImmunization)
				.asRequired(I18nProperties.getValidationError(Validations.required, meansOfImmunization.getCaption()))
				.bind(ImmunizationDto::getMeansOfImmunization, ImmunizationDto::setMeansOfImmunization);

			binder.forField(numberOfDoses)
				.withNullRepresentation("")
				.withConverter(new StringToIntegerConverter(I18nProperties.getValidationError(Validations.vaccineDosesFormat)))
				.withValidator((v, ctx) -> {
					if (v == null) {
						return ValidationResult.ok();
					}
					return (v >= 1 && v <= 10)
						? ValidationResult.ok()
						: ValidationResult.error(I18nProperties.getValidationError(Validations.vaccineDosesFormat));
				})
				.bind(ImmunizationDto::getNumberOfDoses, ImmunizationDto::setNumberOfDoses);

			binder.forField(validFrom)
				.withConverter(new LocalDateToDateConverter())
				.bind(ImmunizationDto::getValidFrom, ImmunizationDto::setValidFrom);

			// validator runs on LocalDate (before converter) so .isBefore() is available
			binder.forField(validUntil).withValidator((v, ctx) -> {
				if (v == null || validFrom.getValue() == null) {
					return ValidationResult.ok();
				}
				return !v.isBefore(validFrom.getValue())
					? ValidationResult.ok()
					: ValidationResult
						.error(I18nProperties.getValidationError(Validations.beforeDate, validUntil.getCaption(), validFrom.getCaption()));
			}).withConverter(new LocalDateToDateConverter()).bind(ImmunizationDto::getValidUntil, ImmunizationDto::setValidUntil);
		}

		private void wireEvents() {
			track(meansOfImmunization.addValueChangeListener(e -> {
				updateVaccinationFieldsVisibility(e.getValue());
				if (e.getValue() != MeansOfImmunization.MATERNAL_VACCINATION) {
					refreshValiditySuggestions();
				}
			}));
			track(numberOfDoses.addValueChangeListener(e -> refreshValiditySuggestions()));
			track(dateOfMostRecentDose.addValueChangeListener(e -> refreshValiditySuggestions()));
			track(validFrom.addValueChangeListener(e -> refreshValidUntilSuggestion()));
		}

		private void refreshMeansOfImmunizationOptions(Disease disease) {
			MeansOfImmunization currentValue = meansOfImmunization.getValue();
			List<MeansOfImmunization> visibleMeans = Diseases.DiseasesConfiguration.getVisibleValues(MeansOfImmunization.class, disease);
			visibleMeans.sort(Comparator.comparing(Enum::name));
			meansOfImmunization.setItems(visibleMeans);
			if (currentValue != null && visibleMeans.contains(currentValue)) {
				meansOfImmunization.setValue(currentValue);
			} else if (currentValue != null) {
				meansOfImmunization.clear();
			}
		}

		private void updateVaccinationFieldsVisibility(MeansOfImmunization meansOfImmunization) {
			boolean isVaccination = MeansOfImmunization.isVaccination(meansOfImmunization);
			boolean isMaternalVaccination = meansOfImmunization == MeansOfImmunization.MATERNAL_VACCINATION;

			numberOfDoses.setVisible(isVaccination && !isMaternalVaccination);
			dateOfMostRecentDose.setVisible(isVaccination && !isMaternalVaccination);
			validFrom.setVisible(isVaccination);
			validUntil.setVisible(isVaccination);
			validityWarningLabel.setVisible(false);
			if (!isVaccination || isMaternalVaccination) {
				numberOfDoses.clear();
				dateOfMostRecentDose.clear();
				clearValidityFields();
			}
		}

		private void clearValidityFields() {
			validFrom.clear();
			validUntil.clear();
		}

		Date getDateOfMostRecentDose() {
			LocalDate local = dateOfMostRecentDose.getValue();
			return local == null ? null : Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}

		private void refreshValiditySuggestions() {
			ImmunizationDto dto = binder.getBean();
			if (dto == null) {
				return;
			}

			if (validFrom.getValue() == null) {
				Date suggestedValidFrom = FacadeProvider.getImmunizationFacade()
					.suggestValidFrom(dto.getDisease(), meansOfImmunization.getValue(), getDateOfMostRecentDose(), parseNumberOfDoses());
				validFrom.setValue(toLocalDate(suggestedValidFrom));
			}
			refreshValidUntilSuggestion();
		}

		private void refreshValidUntilSuggestion() {
			ImmunizationDto dto = binder.getBean();
			if (dto == null) {
				return;
			}

			if (validUntil.getValue() == null) {
				Date suggestedValidUntil = FacadeProvider.getImmunizationFacade()
					.suggestValidUntil(dto.getDisease(), meansOfImmunization.getValue(), toDate(validFrom.getValue()), parseNumberOfDoses());
				validUntil.setValue(toLocalDate(suggestedValidUntil));
			}
			updateValidityWarning();
		}

		/**
		 * Shows a warning when the user has provided enough input for auto-calculation
		 * (dateOfMostRecentDose or validFrom) but the suggestion still returned null,
		 * e.g. because numberOfDoses does not satisfy the required dose count for the disease.
		 */
		private void updateValidityWarning() {
			if (meansOfImmunization.getValue() == MeansOfImmunization.MATERNAL_VACCINATION) {
				validityWarningLabel.setVisible(false);
				return;
			}

			boolean cannotComputeValidFrom = getDateOfMostRecentDose() != null && validFrom.getValue() == null;
			boolean cannotComputeValidUntil = validFrom.getValue() != null && validUntil.getValue() == null;
			validityWarningLabel.setVisible(cannotComputeValidFrom || cannotComputeValidUntil);
		}

		private Integer parseNumberOfDoses() {
			String value = numberOfDoses.getValue();
			if (value == null || value.trim().isEmpty()) {
				return null;
			}

			try {
				return Integer.valueOf(value.trim());
			} catch (NumberFormatException e) {
				return null;
			}
		}

		private Date toDate(LocalDate localDate) {
			return localDate == null ? null : Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		}

		private LocalDate toLocalDate(Date date) {
			return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		}

		VaccinationInfoSource getVaccinationInfoSource() {
			return vaccinationInfoSource.getValue();
		}

		// -------------------------------------------------------------------------
		// LocalDate ↔ java.util.Date converter
		// -------------------------------------------------------------------------

		private static final class LocalDateToDateConverter implements com.vaadin.data.Converter<LocalDate, Date> {

			@Override
			public Result<Date> convertToModel(LocalDate value, ValueContext context) {
				if (value == null) {
					return Result.ok(null);
				}
				return Result.ok(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}

			@Override
			public LocalDate convertToPresentation(Date value, ValueContext context) {
				if (value == null) {
					return null;
				}
				return value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
		}
	}
}
