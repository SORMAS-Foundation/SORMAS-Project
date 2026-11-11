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

package de.symeda.sormas.ui.dashboard.climate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.ItemCaptionGenerator;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Picks the diseases of the Climate dashboard (decision 146): a dropdown that can be typed into to find a disease, and
 * under it only the chosen diseases, one per line with a button to remove it. The dropdown offers the diseases not yet
 * chosen and is disabled once the cap of the view is reached, so the card stays short however many diseases are active.
 * The value is the chosen diseases in the order they are offered.
 */
public class ClimateDiseasePicker extends CustomField<Set<Disease>> {

	private static final long serialVersionUID = 1L;

	private final ComboBox<Disease> addBox = new ComboBox<>();
	private final CssLayout chosenLayout = new CssLayout();
	private final VerticalLayout content = new VerticalLayout(addBox, chosenLayout);

	private List<Disease> offered = Collections.emptyList();
	private Set<Disease> chosen = new LinkedHashSet<>();
	private int cap = Integer.MAX_VALUE;
	private ItemCaptionGenerator<Disease> captions = Disease::toString;

	public ClimateDiseasePicker() {

		addStyleName(CssStyles.CLIMATE_DISEASE_PICKER);
		content.setMargin(false);
		content.setSpacing(false);
		content.setWidthFull();

		addBox.setWidthFull();
		addBox.setPlaceholder(I18nProperties.getCaption(Captions.climateDashboardAddDisease));
		addBox.setItemCaptionGenerator(disease -> captions.apply(disease));
		addBox.setEmptySelectionAllowed(false);
		addBox.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				select(e.getValue());
				addBox.clear();
			}
		});

		chosenLayout.setWidthFull();
		refresh();
	}

	@Override
	protected Component initContent() {
		return content;
	}

	@Override
	public Set<Disease> getValue() {
		return Collections.unmodifiableSet(chosen);
	}

	@Override
	protected void doSetValue(Set<Disease> value) {
		chosen = ordered(value != null ? value : Collections.emptySet());
		refresh();
	}

	/**
	 * Sets the diseases that can be chosen, in the order they are shown; chosen diseases no longer offered are dropped.
	 */
	public void setItems(List<Disease> offered) {
		this.offered = new ArrayList<>(offered);
		setValue(ordered(chosen));
	}

	/**
	 * Sets the caption of each disease, in the dropdown and in the list of chosen diseases.
	 */
	public void setItemCaptionGenerator(ItemCaptionGenerator<Disease> captions) {
		this.captions = captions;
		refresh();
	}

	/**
	 * Sets how many diseases can be chosen; the dropdown is disabled at the cap. Diseases already chosen are kept.
	 */
	public void setCap(int cap) {
		this.cap = cap;
		refresh();
	}

	/**
	 * Adds the disease, as when it is picked in the dropdown; ignored at the cap or when it is not offered.
	 */
	public void select(Disease disease) {
		if (offered.contains(disease) && !chosen.contains(disease) && chosen.size() < cap) {
			Set<Disease> value = new LinkedHashSet<>(chosen);
			value.add(disease);
			setValue(ordered(value), true);
		}
	}

	/**
	 * Removes the disease, as its remove button does.
	 */
	public void deselect(Disease disease) {
		if (chosen.contains(disease)) {
			Set<Disease> value = new LinkedHashSet<>(chosen);
			value.remove(disease);
			setValue(value, true);
		}
	}

	ComboBox<Disease> getAddBox() {
		return addBox;
	}

	/**
	 * @return the captions of the chosen lines, in order
	 */
	List<String> getChosenCaptions() {
		List<String> lines = new ArrayList<>();
		chosenLayout.forEach(row -> lines.add(((Label) ((CssLayout) row).getComponent(0)).getValue()));
		return lines;
	}

	private Set<Disease> ordered(Collection<Disease> diseases) {
		return offered.stream().filter(diseases::contains).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private void refresh() {

		chosenLayout.removeAllComponents();
		for (Disease disease : chosen) {
			Label name = new Label(captions.apply(disease));
			name.setWidthUndefined();
			Button remove = new Button(VaadinIcons.CLOSE_SMALL, e -> deselect(disease));
			remove.addStyleNames(ValoTheme.BUTTON_BORDERLESS, ValoTheme.BUTTON_ICON_ONLY, ValoTheme.BUTTON_SMALL);
			remove.setDescription(I18nProperties.getCaption(Captions.climateDashboardRemoveDisease));
			// the remove button right after the name, so it is next to the disease it removes (decision 149)
			CssLayout row = new CssLayout(name, remove);
			row.setWidthFull();
			row.addStyleName(CssStyles.CLIMATE_DISEASE_CHOSEN);
			chosenLayout.addComponent(row);
		}
		chosenLayout.setVisible(!chosen.isEmpty());

		addBox.setItems(offered.stream().filter(disease -> !chosen.contains(disease)).collect(Collectors.toList()));
		addBox.setEnabled(chosen.size() < cap && chosen.size() < offered.size());
	}
}
