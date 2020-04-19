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
package de.symeda.sormas.ui.configuration.outbreak;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_2;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_4;
import static de.symeda.sormas.ui.utils.CssStyles.style;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.ui.OptionGroup;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.ui.utils.CssStyles;

public class OutbreakRegionConfigurationForm extends VerticalLayout {
		
		private static final long serialVersionUID = 1L;
		

	// Outbreak mode statics
	private final static String OUTBREAK = I18nProperties.getCaption(Captions.outbreakOutbreak);
	private final static String NORMAL = I18nProperties.getCaption(Captions.outbreakNormal);

	// Data
	private final Set<DistrictReferenceDto> affectedDistricts;
	private int totalDistricts;
	private RegionReferenceDto region;

	// Outbreak toggles
	private OptionGroup[] outbreakToggles;
	
	// UI elements
	private Label affectedDistrictsNumberLabel;

	public OutbreakRegionConfigurationForm(OutbreakRegionConfiguration regionOutbreakConfiguration) {
		setStyleName("configuration-view");
		

		// Copy the set of affected districts because the CommitDiscardWrapperComponent is not reset when discarding this form
		affectedDistricts = new HashSet<>(regionOutbreakConfiguration.getAffectedDistricts());
		this.totalDistricts = regionOutbreakConfiguration.getTotalDistricts();
		this.region = regionOutbreakConfiguration.getRegion();
		outbreakToggles = new OptionGroup[regionOutbreakConfiguration.getTotalDistricts()];
		setWidth(860, Unit.PIXELS);

		addComponent(createHeader());
		addComponent(createAffectedDistrictsComponent());
		updateAffectedDistrictsNumberLabel();
	}

	private HorizontalLayout createHeader() {
		HorizontalLayout headerLayout = new HorizontalLayout();
		headerLayout.setWidth(100, Unit.PERCENTAGE);
		headerLayout.setSpacing(true);
		style(headerLayout, VSPACE_2);

		// Headline and info text
		Label infoTextLabel = new Label(I18nProperties.getString(Strings.headingDefineOutbreakDistricts));
		infoTextLabel.setWidthUndefined();
		style(infoTextLabel, VSPACE_TOP_4);
		headerLayout.addComponent(infoTextLabel);

		// Number of affected districts and options to toggle outbreak mode for all districts	
		HorizontalLayout allDistrictsLayout = new HorizontalLayout();
		allDistrictsLayout.setWidthUndefined();
		allDistrictsLayout.setSpacing(true);
		{
			Label allDistrictsLabel = new Label(I18nProperties.getString(Strings.headingSetOutbreakStatus));
			allDistrictsLabel.setWidthUndefined();
			style(allDistrictsLabel, VSPACE_TOP_4);
			allDistrictsLayout.addComponent(allDistrictsLabel);

			OptionGroup outbreakToggle = new OptionGroup();
			style(outbreakToggle, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_SWITCH_CRITICAL);
			outbreakToggle.addItem(OUTBREAK);
			outbreakToggle.addItem(NORMAL);

			if (affectedDistricts.isEmpty()) {
				outbreakToggle.setValue(NORMAL);
			} else if (affectedDistricts.size() == totalDistricts) {
				outbreakToggle.setValue(OUTBREAK);
			}

			outbreakToggle.addValueChangeListener(e -> {
				for (OptionGroup districtOutbreakToggle : outbreakToggles) {
					districtOutbreakToggle.setValue(e.getProperty().getValue());
				}
			});

			outbreakToggle.setWidthUndefined();
			allDistrictsLayout.addComponent(outbreakToggle);

			affectedDistrictsNumberLabel = new Label();
			affectedDistrictsNumberLabel.setWidthUndefined();
			allDistrictsLayout.addComponent(affectedDistrictsNumberLabel);
		}
		headerLayout.addComponent(allDistrictsLayout);
		headerLayout.setComponentAlignment(allDistrictsLayout, Alignment.TOP_RIGHT);

		headerLayout.setExpandRatio(infoTextLabel, 1);

		return headerLayout;
	}

	private HorizontalLayout createAffectedDistrictsComponent() {
		HorizontalLayout affectedDistrictsComponent = new HorizontalLayout();
		affectedDistrictsComponent.setWidth(100, Unit.PERCENTAGE);
		affectedDistrictsComponent.setMargin(false);
		style(affectedDistrictsComponent, VSPACE_3);

		// Create two columns to display the districts
		VerticalLayout leftColumn = new VerticalLayout();
		leftColumn.setMargin(false);
		VerticalLayout middleColumn = new VerticalLayout();
		middleColumn.setMargin(false);
		VerticalLayout rightColumn = new VerticalLayout();
		rightColumn.setMargin(false);

		affectedDistrictsComponent.addComponent(leftColumn);
		// Add spacer label
		affectedDistrictsComponent.addComponent(new Label());
		affectedDistrictsComponent.addComponent(middleColumn);
		// Add spacer label
		affectedDistrictsComponent.addComponent(new Label());
		affectedDistrictsComponent.addComponent(rightColumn);

		affectedDistrictsComponent.setExpandRatio(leftColumn, 1);
		affectedDistrictsComponent.setExpandRatio(middleColumn, 1);
		affectedDistrictsComponent.setExpandRatio(rightColumn, 1);		

		List<DistrictReferenceDto> districts = FacadeProvider.getDistrictFacade().getAllActiveByRegion(region.getUuid());
		int index = 1;
		for (DistrictReferenceDto district : districts) {
			OptionGroup outbreakToggle = createOutbreakToggle(district);
			outbreakToggle.setWidth(100, Unit.PERCENTAGE);
			outbreakToggles[index - 1] = outbreakToggle;

			// Split districts evenly to all three columns
			if ((districts.size() % 3 == 0 && index <= districts.size() / 3) || 
					(districts.size() % 3 != 0 && index <= (districts.size() / 3) + 1)) {
				leftColumn.addComponent(outbreakToggle);
			} else if ((districts.size() % 3 == 0 && index <= districts.size() / 1.5f) || 
					((districts.size() % 3 == 1 || districts.size() % 3 == 2) && index <= (districts.size() / 1.5f) + 1)) {
				middleColumn.addComponent(outbreakToggle);
			} else {
				rightColumn.addComponent(outbreakToggle);
			}

			index++;
		}

		return affectedDistrictsComponent;
	}

	private OptionGroup createOutbreakToggle(DistrictReferenceDto district) {
		OptionGroup outbreakToggle = new OptionGroup();
		style(outbreakToggle, ValoTheme.OPTIONGROUP_HORIZONTAL, CssStyles.OPTIONGROUP_HORIZONTAL_SWITCH_CRITICAL, CssStyles.OPTIONGROUP_CAPTION_INLINE);
		outbreakToggle.setCaption(district.toString());
		outbreakToggle.addItem(OUTBREAK);
		outbreakToggle.addItem(NORMAL);
		
		if (affectedDistricts.contains(district)) {
			outbreakToggle.setValue(OUTBREAK);
		} else {
			outbreakToggle.setValue(NORMAL);
		}
		
		outbreakToggle.addValueChangeListener(e -> {
			if (e.getProperty().getValue() == OUTBREAK) {
				affectedDistricts.add(district);
			} else {
				affectedDistricts.remove(district);
			}
			
			updateAffectedDistrictsNumberLabel();
		});
		
		return outbreakToggle;
	}

	private void updateAffectedDistrictsNumberLabel() {		
		affectedDistrictsNumberLabel.setValue(affectedDistricts.size() + "/" + totalDistricts + " " + I18nProperties.getCaption(Captions.outbreakAffectedDistricts));
		
		CssStyles.removeStyles(affectedDistrictsNumberLabel, 
				CssStyles.LABEL_CONFIGURATION_SEVERITY_INDICATOR, CssStyles.LABEL_CRITICAL, CssStyles.LABEL_WARNING);
		if (affectedDistricts.size() == 0) {
			style(affectedDistrictsNumberLabel, CssStyles.LABEL_CONFIGURATION_SEVERITY_INDICATOR);
		} else if (affectedDistricts.size() >= totalDistricts / 2.0f) {
			style(affectedDistrictsNumberLabel, CssStyles.LABEL_CONFIGURATION_SEVERITY_INDICATOR, CssStyles.LABEL_CRITICAL);
		} else {
			style(affectedDistrictsNumberLabel, CssStyles.LABEL_CONFIGURATION_SEVERITY_INDICATOR, CssStyles.LABEL_WARNING);
		}
	}
	
	public Set<DistrictReferenceDto> getAffectedDistricts() {
		return affectedDistricts;
	}
	
}
