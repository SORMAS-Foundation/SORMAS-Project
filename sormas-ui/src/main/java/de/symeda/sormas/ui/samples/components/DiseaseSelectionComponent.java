package de.symeda.sormas.ui.samples.components;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.environment.environmentsample.Pathogen;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Disease/pathogen selection with variant support.
 * Vaadin 8 components with own Binder, self-managed visibility.
 */
public class DiseaseSelectionComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private final FormEventBus eventBus;
	private final Disease initialDisease;
	private final boolean create;
	private final boolean isEnvironmentSample;

	private ComboBox<Disease> diseaseField;
	private TextField diseaseDetailsField;
	private Label diseaseDetailsSpacer;
	private ComboBox<Pathogen> testedPathogenField;
	private TextField testedPathogenDetailsField;
	private Label pathogenDetailsSpacer;
	private ComboBox<DiseaseVariant> diseaseVariantField;
	private TextField diseaseVariantDetailsField;
	private Label variantDetailsSpacer;

	private HorizontalLayout variantRow;

	public DiseaseSelectionComponent(FormEventBus eventBus, Disease initialDisease, boolean create, boolean isEnvironmentSample) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.initialDisease = initialDisease;
		this.create = create;
		this.isEnvironmentSample = isEnvironmentSample;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		// Disease
		diseaseField = createComboBox(PathogenTestDto.TESTED_DISEASE, PathogenTestDto.I18N_PREFIX);
		diseaseField.setItemCaptionGenerator(Disease::toString);

		List<Disease> activeDiseases = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, true, true);
		List<Disease> nonPrimary = FacadeProvider.getDiseaseConfigurationFacade().getAllDiseases(true, false, true);
		activeDiseases.addAll(nonPrimary);
		diseaseField.setItems(activeDiseases);

		diseaseDetailsField = createTextField(PathogenTestDto.TESTED_DISEASE_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		diseaseDetailsField.setVisible(false);

		diseaseDetailsSpacer = createSpacer();
		addToggleRow(diseaseField, diseaseDetailsField, diseaseDetailsSpacer);

		// Pathogen
		testedPathogenField = createComboBox(PathogenTestDto.TESTED_PATHOGEN, PathogenTestDto.I18N_PREFIX);
		testedPathogenField.setItemCaptionGenerator(Pathogen::getCaption);
		testedPathogenField.setItems(FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.PATHOGEN, initialDisease));

		testedPathogenDetailsField = createTextField(PathogenTestDto.TESTED_PATHOGEN_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		testedPathogenDetailsField.setVisible(false);

		pathogenDetailsSpacer = createSpacer();
		addToggleRow(testedPathogenField, testedPathogenDetailsField, pathogenDetailsSpacer);

		// Disease variant
		diseaseVariantField = createComboBox(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.I18N_PREFIX);
		diseaseVariantField.setItemCaptionGenerator(DiseaseVariant::getCaption);
		diseaseVariantField.setEmptySelectionAllowed(true);
		diseaseVariantField.setVisible(false);

		diseaseVariantDetailsField =
			createTextField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		diseaseVariantDetailsField.setVisible(false);

		if (DiseaseHelper.SUBTYPE_ALLOWED_DISEASES.contains(initialDisease)) {
			diseaseVariantField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariant));
			diseaseVariantDetailsField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariantDetails));
		}

		variantDetailsSpacer = createSpacer();
		variantRow = addToggleRow(diseaseVariantField, diseaseVariantDetailsField, variantDetailsSpacer);

		// Environment vs human sample visibility
		if (isEnvironmentSample) {
			diseaseField.setVisible(false);
			diseaseDetailsField.setVisible(false);
			testedPathogenField.setVisible(true);
		} else {
			diseaseField.setVisible(true);
			testedPathogenField.setVisible(false);
		}

		updateDiseaseVariants(initialDisease);
	}

	private void bindFields() {
		if (isEnvironmentSample) {
			binder.forField(diseaseField).bind(PathogenTestDto::getTestedDisease, PathogenTestDto::setTestedDisease);
		} else {
			binder.forField(diseaseField).asRequired().bind(PathogenTestDto::getTestedDisease, PathogenTestDto::setTestedDisease);
		}
		binder.forField(diseaseDetailsField).bind(PathogenTestDto::getTestedDiseaseDetails, PathogenTestDto::setTestedDiseaseDetails);
		if (isEnvironmentSample) {
			binder.forField(testedPathogenField).asRequired().bind(PathogenTestDto::getTestedPathogen, PathogenTestDto::setTestedPathogen);
		} else {
			binder.forField(testedPathogenField).bind(PathogenTestDto::getTestedPathogen, PathogenTestDto::setTestedPathogen);
		}
		binder.forField(testedPathogenDetailsField).bind(PathogenTestDto::getTestedPathogenDetails, PathogenTestDto::setTestedPathogenDetails);
		binder.forField(diseaseVariantField).bind(PathogenTestDto::getTestedDiseaseVariant, PathogenTestDto::setTestedDiseaseVariant);
		binder.forField(diseaseVariantDetailsField)
			.bind(PathogenTestDto::getTestedDiseaseVariantDetails, PathogenTestDto::setTestedDiseaseVariantDetails);
	}

	private void wireEvents() {
		// Disease change: update variants, show/hide details, fire event
		track(diseaseField.addValueChangeListener(e -> {
			Disease newDisease = e.getValue();

			boolean showDiseaseDetails = newDisease == Disease.OTHER;
			diseaseDetailsField.setVisible(showDiseaseDetails);
			diseaseDetailsSpacer.setVisible(!showDiseaseDetails);
			if (!showDiseaseDetails) {
				diseaseDetailsField.clear();
			}

			updateDiseaseVariants(newDisease);
			eventBus.fire(new DiseaseChangedEvent(newDisease));
		}));

		// Pathogen details visibility
		track(testedPathogenField.addValueChangeListener(e -> {
			Pathogen pathogen = e.getValue();
			boolean showPathogenDetails = pathogen != null && pathogen.isHasDetails();
			testedPathogenDetailsField.setVisible(showPathogenDetails);
			pathogenDetailsSpacer.setVisible(!showPathogenDetails);
			if (!showPathogenDetails) {
				testedPathogenDetailsField.clear();
			}
		}));

		// Disease variant details visibility
		track(diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant variant = e.getValue();
			boolean showVariantDetails = variant != null && variant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true);
			diseaseVariantDetailsField.setVisible(showVariantDetails);
			variantDetailsSpacer.setVisible(!showVariantDetails);
		}));
	}

	private void updateDiseaseVariants(Disease disease) {
		List<DiseaseVariant> variants = FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, disease);
		diseaseVariantField.setItems(variants);
		diseaseVariantField.setVisible(disease != null && CollectionUtils.isNotEmpty(variants));
		updateRowVisibility(variantRow);
	}

	public ComboBox<Disease> getDiseaseField() {
		return diseaseField;
	}

	public ComboBox<DiseaseVariant> getDiseaseVariantField() {
		return diseaseVariantField;
	}

	public TextField getDiseaseVariantDetailsField() {
		return diseaseVariantDetailsField;
	}
}
