package de.symeda.sormas.ui.configuration.linelisting;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

@SuppressWarnings("serial")
public class LineListingAddDiseaseLayout extends HorizontalLayout {

	private final List<Disease> diseases;
	private Consumer<Disease> addDiseaseCallback;

	private ComboBox<Disease> cbDiseases;
	private Button btnAddDisease;

	public LineListingAddDiseaseLayout(List<Disease> diseases) {
		this.diseases = diseases;

		buildLayout();
	}

	public void setAddDiseaseCallback(Consumer<Disease> addDiseaseCallback) {
		this.addDiseaseCallback = addDiseaseCallback;
	}

	public void removeDiseaseFromList(Disease disease) {
		diseases.remove(disease);
		cbDiseases.setValue(null);
		cbDiseases.setItems(diseases);
	}

	public void addDiseaseToList(Disease disease) {
		diseases.add(disease);
		diseases.sort((d1, d2) -> d1.toString().compareTo(d2.toString()));
		cbDiseases.setItems(diseases);
	}

	private void buildLayout() {
		cbDiseases = new ComboBox<>();
		cbDiseases.setId("disease");
		cbDiseases.setItems(diseases);
		cbDiseases.setItemCaptionGenerator(disease -> disease.toString());
		cbDiseases.setPlaceholder(I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.DISEASE));
		cbDiseases.setWidth(200, Unit.PIXELS);
		cbDiseases.addValueChangeListener(e -> {
			btnAddDisease.setEnabled(e.getValue() != null);
		});
		addComponent(cbDiseases);

		btnAddDisease = ButtonHelper.createButton(Captions.lineListingEnableForDisease, e -> {
			if (addDiseaseCallback != null) {
				addDiseaseCallback.accept(cbDiseases.getValue());
			}
		}, ValoTheme.BUTTON_PRIMARY);
		btnAddDisease.setEnabled(false);

		addComponent(btnAddDisease);

		setExpandRatio(btnAddDisease, 1);
	}

}
