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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.AnnotationFieldHelper;
import de.symeda.sormas.ui.utils.CssStyles;
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
	private Map<String, Component> componentMap = new HashMap<>();
	private final List<String> complicatedSymptoms;

	public CaseSymptomSideViewComponent(Disease disease) {
		super(I18nProperties.getCaption(String.format(Captions.titleComplications)));
		topLayout.setWidth(100, Unit.PERCENTAGE);
		topLayout.setMargin(false);
		topLayout.setSpacing(false);
		addComponent(topLayout);
		noComplicationsLabel = new Label(I18nProperties.getCaption(Captions.titleNoComplications));
		topLayout.addComponents(noComplicationsLabel);
		topLayout.addComponent(layout);
		complicatedSymptoms = AnnotationFieldHelper.getComplicatedSymptomsWithDiseases(SymptomsDto.class, disease);
	}

	/**
	 * Toggle the symptom based on the flag
	 * 
	 * @param symptomName
	 * @param validComplication
	 */
	public void toggleComplicationSymptom(String symptomName, boolean validComplication) {
		if (symptomName == null) {
			return;
		}
		// find the symptom in the complicated symptoms list, if it exists, update the map
		complicatedSymptoms.stream().filter(symptom -> symptom.equals(symptomName)).findFirst().ifPresent(symptom -> {
			if (validComplication) {
				Label label = new Label(I18nProperties.getCaption(Captions.Symptoms + "." + symptomName));
				componentMap.put(symptomName, label);
			}
			if (componentMap.isEmpty()) {
				return;
			}
			if (!validComplication && componentMap.containsKey(symptomName)) {
				layout.removeComponent(componentMap.get(symptomName));
				componentMap.entrySet().removeIf(entry -> entry.getKey().equals(symptomName));
			}
		});
	}

	/**
	 * Refresh the layout based on the list of symptoms
	 */
	public void refreshLayout() {
		layout.setMargin(false);
		layout.setSpacing(false);
		componentMap.keySet().forEach(symptom -> {
			Component comp = componentMap.get(symptom);
			comp.addStyleNames(CssStyles.LABEL_CRITICAL, CssStyles.LABEL_BOLD);
			layout.addComponent(comp);
		});
		if (componentMap.isEmpty()) {
			noComplicationsLabel.setVisible(true);
		} else {
			noComplicationsLabel.setVisible(false);
		}
		topLayout.addComponent(layout);
	}
}
