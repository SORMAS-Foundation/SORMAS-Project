package de.symeda.sormas.ui.samples.components;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.ui.samples.events.DiseaseChangedEvent;
import de.symeda.sormas.ui.samples.events.TestTypeChangedEvent;
import de.symeda.sormas.ui.utils.FormComponent;
import de.symeda.sormas.ui.utils.FormEventBus;

/**
 * Disease variant selection with details support.
 * Extracted from DiseaseSelectionComponent to allow correct field ordering
 * (variant appears after test result row in the original form layout).
 */
public class DiseaseVariantComponent extends FormComponent<PathogenTestDto> {

	private static final long serialVersionUID = 1L;

	private static final Set<PathogenTestType> VARIANT_ALLOWED_TEST_TYPES = new HashSet<>(
		Arrays.asList(
			PathogenTestType.SEQUENCING,
			PathogenTestType.WHOLE_GENOME_SEQUENCING,
			PathogenTestType.PCR_RT_PCR,
			PathogenTestType.ISOLATION,
			PathogenTestType.OTHER));

	private final FormEventBus eventBus;
	private Disease currentDisease;
	private PathogenTestType currentTestType;
	private List<DiseaseVariant> currentVariants;

	private ComboBox<DiseaseVariant> diseaseVariantField;
	private TextField diseaseVariantDetailsField;
	private Label variantDetailsSpacer;
	private HorizontalLayout variantRow;

	public DiseaseVariantComponent(FormEventBus eventBus, Disease initialDisease) {
		super(PathogenTestDto.class);
		this.eventBus = eventBus;
		this.currentDisease = initialDisease;
		buildLayout();
		bindFields();
		wireEvents();
	}

	private void buildLayout() {
		diseaseVariantField = createComboBox(PathogenTestDto.TESTED_DISEASE_VARIANT, PathogenTestDto.I18N_PREFIX);
		diseaseVariantField.setItemCaptionGenerator(DiseaseVariant::getCaption);
		diseaseVariantField.setEmptySelectionAllowed(true);
		diseaseVariantField.setVisible(false);

		diseaseVariantDetailsField =
			createTextField(PathogenTestDto.TESTED_DISEASE_VARIANT_DETAILS, PathogenTestDto.I18N_PREFIX, ValueChangeMode.BLUR);
		diseaseVariantDetailsField.setVisible(false);

		updateCaptions(currentDisease);

		variantDetailsSpacer = createSpacer();
		variantRow = addToggleRow(diseaseVariantField, diseaseVariantDetailsField, variantDetailsSpacer);

		refreshVariantItems();
		updateVisibility();
	}

	private void bindFields() {
		binder.forField(diseaseVariantField).bind(PathogenTestDto::getTestedDiseaseVariant, PathogenTestDto::setTestedDiseaseVariant);
		binder.forField(diseaseVariantDetailsField)
			.bind(PathogenTestDto::getTestedDiseaseVariantDetails, PathogenTestDto::setTestedDiseaseVariantDetails);
	}

	private void wireEvents() {
		// Variant details visibility
		track(diseaseVariantField.addValueChangeListener(e -> {
			DiseaseVariant variant = e.getValue();
			boolean showVariantDetails = variant != null && variant.matchPropertyValue(DiseaseVariant.HAS_DETAILS, true);
			diseaseVariantDetailsField.setVisible(showVariantDetails);
			variantDetailsSpacer.setVisible(!showVariantDetails);
		}));

		// Disease change -> update variant list, captions, and visibility
		track(eventBus.on(DiseaseChangedEvent.class, event -> {
			currentDisease = event.getDisease();
			refreshVariantItems();
			updateCaptions(currentDisease);
			updateVisibility();
		}));

		// Test type change -> update visibility
		track(eventBus.on(TestTypeChangedEvent.class, event -> {
			currentTestType = event.getTestType();
			updateVisibility();
		}));
	}

	private void refreshVariantItems() {
		currentVariants = FacadeProvider.getCustomizableEnumFacade().getEnumValues(CustomizableEnumType.DISEASE_VARIANT, currentDisease);
		diseaseVariantField.setItems(currentVariants);
	}

	private void updateVisibility() {
		boolean visible = currentDisease != null
			&& DiseaseHelper.SUBTYPE_ALLOWED_DISEASES.contains(currentDisease)
			&& VARIANT_ALLOWED_TEST_TYPES.contains(currentTestType)
			&& CollectionUtils.isNotEmpty(currentVariants);
		diseaseVariantField.setVisible(visible);
		if (!visible) {
			diseaseVariantField.clear();
			diseaseVariantDetailsField.clear();
		}
		updateRowVisibility(variantRow);
		setVisible(variantRow.isVisible());
	}

	private void updateCaptions(Disease disease) {
		if (DiseaseHelper.SUBTYPE_ALLOWED_DISEASES.contains(disease)) {
			diseaseVariantField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariant));
			diseaseVariantDetailsField.setCaption(I18nProperties.getCaption(Captions.PathogenTest_rsv_testedDiseaseVariantDetails));
		}
	}

	public ComboBox<DiseaseVariant> getDiseaseVariantField() {
		return diseaseVariantField;
	}

	public TextField getDiseaseVariantDetailsField() {
		return diseaseVariantDetailsField;
	}
}
