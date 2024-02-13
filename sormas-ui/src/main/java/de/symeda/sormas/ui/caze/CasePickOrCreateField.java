package de.symeda.sormas.ui.caze;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.v7.ui.CustomField;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.caze.components.caseselection.CaseSelectionGrid;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;

@SuppressWarnings("serial")
public class CasePickOrCreateField extends CustomField<CaseSelectionDto> {

	public static final String CREATE_CASE = "createCase";
	public static final String PICK_CASE = "pickCase";

	protected List<CaseSelectionDto> similarCases;
	protected CaseSelectionGrid grid;
	protected OptionGroup pickCase;
	protected OptionGroup createCase;
	protected Consumer<Boolean> selectionChangeCallback;
	protected CaseDataDto newCase;
	protected PersonDto newPerson;

	protected VerticalLayout mainLayout;

	public CasePickOrCreateField(CaseDataDto newCase, PersonDto newPerson, List<CaseSelectionDto> similarCases) {

		this.similarCases = similarCases;
		this.newCase = newCase;
		this.newPerson = newPerson;

		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);
	}

	protected void addInfoComponent() {

		HorizontalLayout infoLayout = new HorizontalLayout();
		infoLayout.setWidth(100, Unit.PERCENTAGE);
		infoLayout.setSpacing(true);
		Image icon = new Image(null, new ThemeResource("img/info-icon.png"));
		icon.setHeight(35, Unit.PIXELS);
		icon.setWidth(35, Unit.PIXELS);
		infoLayout.addComponent(icon);
		Label infoLabel = new Label(I18nProperties.getString(Strings.infoPickOrCreateCase));
		infoLabel.setContentMode(ContentMode.HTML);
		infoLayout.addComponent(infoLabel);
		infoLayout.setExpandRatio(infoLabel, 1);
		mainLayout.addComponent(infoLayout);
		CssStyles.style(infoLayout, CssStyles.VSPACE_3);
		// Imported case info
		VerticalLayout caseInfoContainer = new VerticalLayout();
		caseInfoContainer.setWidth(100, Unit.PERCENTAGE);
		CssStyles
			.style(caseInfoContainer, CssStyles.BACKGROUND_ROUNDED_CORNERS, CssStyles.BACKGROUND_SUB_CRITERIA, CssStyles.VSPACE_3, "v-scrollable");

		Label newCaseLabel = new Label(I18nProperties.getString(Strings.infoPickOrCreateCaseNewCase));
		CssStyles.style(newCaseLabel, CssStyles.LABEL_BOLD, CssStyles.VSPACE_4);
		caseInfoContainer.addComponent(newCaseLabel);

		HorizontalLayout caseInfoLayout = new HorizontalLayout();
		caseInfoLayout.setSpacing(true);
		caseInfoLayout.setSizeUndefined();
		{
			Label firstNameField = new Label();
			firstNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.FIRST_NAME));
			firstNameField.setValue(newPerson.getFirstName());
			firstNameField.setWidthUndefined();
			caseInfoLayout.addComponent(firstNameField);

			Label lastNameField = new Label();
			lastNameField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.LAST_NAME));
			lastNameField.setValue(newPerson.getLastName());
			lastNameField.setWidthUndefined();
			caseInfoLayout.addComponent(lastNameField);

			Label ageAndBirthDateField = new Label();
			ageAndBirthDateField.setCaption(I18nProperties.getCaption(Captions.personAgeAndBirthdate));
			ageAndBirthDateField.setValue(
				PersonHelper.getAgeAndBirthdateString(
					newPerson.getApproximateAge(),
					newPerson.getApproximateAgeType(),
					newPerson.getBirthdateDD(),
					newPerson.getBirthdateMM(),
					newPerson.getBirthdateYYYY()));
			ageAndBirthDateField.setWidthUndefined();
			caseInfoLayout.addComponent(ageAndBirthDateField);

			if (UiUtil.disabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
				Label responsibleDistrictField = new Label();
				responsibleDistrictField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.RESPONSIBLE_DISTRICT));
				responsibleDistrictField.setValue(newCase.getResponsibleDistrict() != null ? newCase.getResponsibleDistrict().buildCaption() : "");
				responsibleDistrictField.setWidthUndefined();
				caseInfoLayout.addComponent(responsibleDistrictField);

				if (newCase.getDistrict() != null) {
					Label districtField = new Label();
					districtField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISTRICT));
					districtField.setValue(newCase.getDistrict().buildCaption());
					districtField.setWidthUndefined();
					caseInfoLayout.addComponent(districtField);
				}
			}

			Label facilityField = new Label();
			facilityField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY));
			facilityField.setValue(
				FacilityHelper.buildFacilityString(
					null,
					newCase.getHealthFacility() != null ? newCase.getHealthFacility().buildCaption() : "",
					newCase.getHealthFacilityDetails()));
			facilityField.setWidthUndefined();
			caseInfoLayout.addComponent(facilityField);

			Label reportDateField = new Label();
			reportDateField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.REPORT_DATE));
			reportDateField.setValue(DateFormatHelper.formatDate(newCase.getReportDate()));
			reportDateField.setWidthUndefined();
			caseInfoLayout.addComponent(reportDateField);

			Label sexField = new Label();
			sexField.setCaption(I18nProperties.getPrefixCaption(PersonDto.I18N_PREFIX, PersonDto.SEX));
			sexField.setValue(newPerson.getSex() != null ? newPerson.getSex().toString() : "");
			sexField.setWidthUndefined();
			caseInfoLayout.addComponent(sexField);

			Label classificationField = new Label();
			classificationField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.CASE_CLASSIFICATION));
			classificationField.setValue(newCase.getCaseClassification().toString());
			classificationField.setWidthUndefined();
			caseInfoLayout.addComponent(classificationField);

			Label outcomeField = new Label();
			outcomeField.setCaption(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.OUTCOME));
			outcomeField.setValue(newCase.getOutcome().toString());
			outcomeField.setWidthUndefined();
			caseInfoLayout.addComponent(outcomeField);

		}

		caseInfoContainer.addComponent(caseInfoLayout);
		mainLayout.addComponent(caseInfoContainer);
	}

	protected void addPickCaseComponent() {

		pickCase = new OptionGroup(null);
		pickCase.addItem(PICK_CASE);
		pickCase.setItemCaption(PICK_CASE, I18nProperties.getCaption(Captions.casePickCase));
		CssStyles.style(pickCase, CssStyles.VSPACE_NONE);
		pickCase.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				createCase.setValue(null);
				grid.setEnabled(true);
				if (similarCases.size() == 1) {
					pickCase.setValue(PICK_CASE);
					grid.select(similarCases.get(0));
				}
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(grid.getSelectedRow() != null);
				}
			}
		});
		mainLayout.addComponent(pickCase);
	}

	protected void addAndConfigureGrid() {

		initGrid();

		grid.setEnabled(false);
		grid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				createCase.setValue(null);
			}
		});
		mainLayout.addComponent(grid);

		grid.addSelectionListener(e -> {
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
	}

	protected void addCreateCaseComponent() {

		createCase = new OptionGroup(null);
		createCase.addItem(CREATE_CASE);
		createCase.setItemCaption(CREATE_CASE, I18nProperties.getCaption(Captions.caseCreateCase));
		createCase.addValueChangeListener(e -> {
			if (e.getProperty().getValue() != null) {
				pickCase.setValue(null);
				grid.select(null);
				grid.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});
		mainLayout.addComponent(createCase);
	}

	@Override
	protected Component initContent() {

		addInfoComponent();
		addPickCaseComponent();
		addAndConfigureGrid();
		addCreateCaseComponent();

		grid.setEnabled(false);

		return mainLayout;
	}

	private void initGrid() {
		if (grid == null) {
			grid = new CaseSelectionGrid(similarCases);
		}
	}

	@Override
	protected void setInternalValue(CaseSelectionDto newValue) {

		super.setInternalValue(newValue);

		if (pickCase != null) {
			pickCase.setValue(PICK_CASE);
		}

		if (newValue != null) {
			grid.select(newValue);
		}
	}

	@Override
	protected CaseSelectionDto getInternalValue() {

		if (grid != null) {
			CaseSelectionDto value = (CaseSelectionDto) grid.getSelectedRow();
			return value;
		}

		return super.getInternalValue();
	}

	@Override
	public Class<? extends CaseSelectionDto> getType() {
		return CaseSelectionDto.class;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}
}
