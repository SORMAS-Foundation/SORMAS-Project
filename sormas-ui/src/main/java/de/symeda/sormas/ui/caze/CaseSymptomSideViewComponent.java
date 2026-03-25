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

package de.symeda.sormas.ui.caze;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.ui.Field;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomState;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.components.sidecomponent.SideComponent;

/**
 * This component is used to display the symptoms of a case in the side view of the symptom details view.
 * It allows to toggle the symptoms based on the flag and refreshes the layout accordingly.
 */
public class CaseSymptomSideViewComponent extends SideComponent {

	private static final long serialVersionUID = -1L;
	private HorizontalLayout topLayout = new HorizontalLayout();
	private final Label noComplicationsLabel;
	private VerticalLayout layout = new VerticalLayout();
	private Map<String, Component> componentMap = new TreeMap<>();
	private final List<String> complicatedSymptoms;

	public CaseSymptomSideViewComponent(Disease disease) {
		super(I18nProperties.getCaption(String.format(Captions.titleComplications)));
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		addComponent(topLayout);
		noComplicationsLabel = new Label(I18nProperties.getCaption(Captions.titleNoComplications));
		topLayout.addComponents(noComplicationsLabel, layout);
		complicatedSymptoms = AnnotationFieldHelper.getComplicatedSymptomsWithDiseases(SymptomsDto.class, disease);
	}

	/**
	 * Toggle the symptom based on the flag
	 * 
	 * @param sourceField
	 */
	public void toggleComplicationSymptom(Field sourceField) {

		if (sourceField == null) {
			return;
		}

		Object sourceFieldObj = FieldHelper.getNullableSourceFieldValue(sourceField);

		// if the source field value is NO, UNKNOWN or deselect Yes and the value will be empty (in case of other complications symptoms data),
		// and a map has it, remove it from the layout and map
		if ((sourceFieldObj == null
			|| sourceFieldObj == SymptomState.NO
			|| sourceFieldObj == SymptomState.UNKNOWN
			|| StringUtils.isBlank(sourceFieldObj.toString())) && componentMap.containsKey(sourceField.getId())) {
			layout.removeComponent(componentMap.get(sourceField.getId()));
			componentMap.entrySet().removeIf(entry -> entry.getKey().equals(sourceField.getId()));
			return;
		}
		// This is to set the symptom name as the caption.

		// if the sourceField value YES and map does not have it, and it's a complicated symptom,
		// add it to the layout and map
		if ((sourceFieldObj == SymptomState.YES)
			&& !componentMap.containsKey(sourceField.getId())
			&& complicatedSymptoms.contains(sourceField.getId())) {
			Label label = new Label(I18nProperties.getCaption(Captions.Symptoms + "." + sourceField.getId()));
			componentMap.put(sourceField.getId(), label);
			return;
		}
		// if the source field value is String type, and it's value is not empty, and a map does not have that symptom, and its a complicated symptom, add it to the layout and map
		// This is to set the entered value as a caption.
		if (sourceFieldObj instanceof String
			&& StringUtils.isNotBlank((String) sourceFieldObj)
			&& !componentMap.containsKey(sourceField.getId())
			&& complicatedSymptoms.contains(sourceField.getId())) {
			Label label = new Label(sourceField.getValue().toString());
			componentMap.put(sourceField.getId(), label);
		}
	}

	/**
	 * Refresh the layout based on the list of symptoms
	 */
	public void refreshLayout() {
		layout.setMargin(false);
		layout.setSpacing(false);
		componentMap.keySet().forEach(symptom -> {
			Component comp = componentMap.get(symptom);
			comp.addStyleNames(CssStyles.LABEL_PRIMARY, CssStyles.LABEL_BOLD);
			layout.addComponent(comp);
		});
		if (componentMap.isEmpty()) {
			noComplicationsLabel.setVisible(true);
		} else {
			noComplicationsLabel.setVisible(false);
		}
	}
}
