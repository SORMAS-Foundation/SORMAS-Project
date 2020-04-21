package de.symeda.sormas.ui.samples;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.AdditionalTestDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class AdditionalTestListEntry extends HorizontalLayout {

	private final AdditionalTestDto additionalTest;
	private Button editButton;

	public AdditionalTestListEntry(AdditionalTestDto additionalTest) {
		setSpacing(true);
		setMargin(false);
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST_ENTRY);
		this.additionalTest = additionalTest;

		VerticalLayout labelLayout = new VerticalLayout();
		labelLayout.setSpacing(false);
		labelLayout.setMargin(false);
		labelLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(labelLayout);
		setExpandRatio(labelLayout, 1);

		Label dateLabel = new Label(DateHelper.formatLocalDateTime(additionalTest.getTestDateTime()));
		CssStyles.style(dateLabel, CssStyles.VSPACE_3);
		labelLayout.addComponent(dateLabel);

		if (additionalTest.getHaemoglobinuria() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.HAEMOGLOBINURIA),
					additionalTest.getHaemoglobinuria().toString()));
		}
		if (additionalTest.getProteinuria() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.PROTEINURIA),
					additionalTest.getProteinuria().toString()));
		}
		if (additionalTest.getHematuria() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.HEMATURIA),
					additionalTest.getHematuria().toString()));
		}
		if (additionalTest.hasArterialVenousGasValue()) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.ARTERIAL_VENOUS_BLOOD_GAS),
					additionalTest.buildArterialVenousGasValuesString()));
			if (additionalTest.getGasOxygenTherapy() != null) {
				labelLayout.addComponent(new Label(I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.GAS_OXYGEN_THERAPY)
						+ ": " + additionalTest.getGasOxygenTherapy()));
			}
		}
		if (additionalTest.getAltSgpt() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.ALT_SGPT),
					additionalTest.getAltSgpt().toString()));
		}
		if (additionalTest.getAstSgot() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.AST_SGOT),
					additionalTest.getAstSgot().toString()));
		}
		if (additionalTest.getCreatinine() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.CREATININE),
					additionalTest.getCreatinine().toString()));
		}
		if (additionalTest.getPotassium() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.POTASSIUM),
					additionalTest.getPotassium().toString()));
		}
		if (additionalTest.getUrea() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.UREA),
					additionalTest.getUrea().toString()));
		}
		if (additionalTest.getHaemoglobin() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.HAEMOGLOBIN),
					additionalTest.getHaemoglobin().toString()));
		}
		if (additionalTest.getTotalBilirubin() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.TOTAL_BILIRUBIN),
					additionalTest.getTotalBilirubin().toString()));
		}
		if (additionalTest.getConjBilirubin() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.CONJ_BILIRUBIN),
					additionalTest.getConjBilirubin().toString()));
		}
		if (additionalTest.getWbcCount() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.WBC_COUNT),
					additionalTest.getWbcCount().toString()));
		}
		if (additionalTest.getPlatelets() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.PLATELETS),
					additionalTest.getPlatelets().toString()));
		}
		if (additionalTest.getProthrombinTime() != null) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.PROTHROMBIN_TIME),
					additionalTest.getProthrombinTime().toString()));
		}
		if (!StringUtils.isEmpty(additionalTest.getOtherTestResults())) {
			labelLayout.addComponent(createFieldLabel(
					I18nProperties.getPrefixCaption(AdditionalTestDto.I18N_PREFIX, AdditionalTestDto.OTHER_TEST_RESULTS),
					""));
			labelLayout.addComponent(new Label(additionalTest.getOtherTestResults()));
		}
	}

	private Label createFieldLabel(String caption, String value) {
		return new Label(caption.toUpperCase() + ": " + value);
	}

	public void addEditListener(ClickListener editClickListener) {
		if (editButton == null) {
			editButton = new Button(VaadinIcons.PENCIL);
			CssStyles.style(editButton, ValoTheme.BUTTON_LINK, CssStyles.BUTTON_COMPACT);
			addComponent(editButton);
			setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
			setExpandRatio(editButton, 0);
		}
		editButton.addClickListener(editClickListener);
	}

	public AdditionalTestDto getAdditionalTest() {
		return additionalTest;
	}

}
