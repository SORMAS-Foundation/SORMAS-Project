/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2022 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils.processing;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections4.CollectionUtils;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.dataprocessing.PickOrCreateEntryResult;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public abstract class EntrySelectionComponent extends VerticalLayout {

	private static final long serialVersionUID = 5315286409460459687L;

	private final EntrySelectionField.Options selectableOptions;

	private final String infoSelectOrCreateString;
	private final String infoCreateString;
	private final String propertyI18nPrefix;

	private EntrySelectionField entrySelectionField;
	private HorizontalLayout searchDetailsLayout;

	public EntrySelectionComponent(
		EntrySelectionField.Options selectableOptions,
		String infoSelectOrCreateString,
		String infoCreateString,
		String propertyI18nPrefix) {

		this.selectableOptions = selectableOptions;
		this.infoSelectOrCreateString = infoSelectOrCreateString;
		this.infoCreateString = infoCreateString;
		this.propertyI18nPrefix = propertyI18nPrefix;

		setSpacing(true);
		setMargin(false);
		setSizeUndefined();
	}

	protected void initContent() {
		addInfoComponent();
		addSearchDetailsComponent();

		entrySelectionField = new EntrySelectionField(selectableOptions);
		entrySelectionField.setSizeFull();
		addComponent(entrySelectionField);
	}

	private void addInfoComponent() {
		if (CollectionUtils.isNotEmpty(getSelectableItems(EntrySelectionField.OptionType.SELECT_CASE))
			|| CollectionUtils.isNotEmpty(getSelectableItems(EntrySelectionField.OptionType.SELECT_CONTACT))
			|| CollectionUtils.isNotEmpty(getSelectableItems(EntrySelectionField.OptionType.SELECT_EVENT_PARTICIPANT))) {
			addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(infoSelectOrCreateString)));
		} else {
			addComponent(VaadinUiUtil.createInfoComponent(I18nProperties.getString(infoCreateString)));
		}
	}

	private void addSearchDetailsComponent() {
		searchDetailsLayout = new HorizontalLayout();
		searchDetailsLayout.setSpacing(true);

		createAndAddSearchFieldComponents();

		addComponent(searchDetailsLayout);
	}

	protected abstract void createAndAddSearchFieldComponents();

	protected void createAndAddSearchDetailLabel(Object value, String property) {
		Label label = new Label();
		if (value != null) {
			label.setValue(value.toString());
		}
		label.setCaption(I18nProperties.getPrefixCaption(propertyI18nPrefix, property));
		label.setWidthUndefined();

		searchDetailsLayout.addComponent(label);
	}

	private List<?> getSelectableItems(EntrySelectionField.OptionType optionType) {
		return selectableOptions.stream()
			.filter(o -> o.getType() == optionType)
			.map(o -> o.getSelectableItems())
			.findFirst()
			.orElseGet(Collections::emptyList);
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		entrySelectionField.setSelectionChangeCallback(callback);
	}

	public PickOrCreateEntryResult getSelectionResult() {
		return entrySelectionField.getValue();
	}
}
