/*
 * ******************************************************************************
 * * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program. If not, see <https://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.symeda.sormas.ui.events.groups;

import java.util.Set;
import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.event.EventGroupCriteria;
import de.symeda.sormas.api.event.EventGroupIndexDto;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

@SuppressWarnings("serial")
public class EventGroupSelectionField extends CustomField<EventGroupIndexDto> {

	public static final String SELECT_EVENT_GROUP = "selectEventGroup";
	public static final String CREATE_EVENT_GROUP = "createEventGroup";

	private VerticalLayout mainLayout;
	private EventGroupSelectionGrid eventGroupGrid;
	private final String infoPickOrCreateEventGroup;

	private RadioButtonGroup<String> rbSelectEventGroup;
	private RadioButtonGroup<String> rbCreateEventGroup;
	private Consumer<Boolean> selectionChangeCallback;
	private final TextField searchField;
	private final TextField searchEventField;
	private final EventGroupCriteria criteria;

	public EventGroupSelectionField() {
		this(null);
	}

	public EventGroupSelectionField(Set<String> excludedUuids) {
		this.searchField = new TextField();
		this.searchEventField = new TextField();
		this.infoPickOrCreateEventGroup = I18nProperties.getString(Strings.infoPickOrCreateEventGroupForEvent);

		this.criteria = new EventGroupCriteria();
		criteria.setUserFilterIncluded(false);
		criteria.setExcludedUuids(excludedUuids);

		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoPickOrCreateEventGroup));
	}

	private void addSelectEventGroupRadioGroup() {
		rbSelectEventGroup = new RadioButtonGroup<>();
		rbSelectEventGroup.setItems(SELECT_EVENT_GROUP);
		rbSelectEventGroup.setItemCaptionGenerator((item) -> {
			return I18nProperties.getCaption(Captions.eventSelectGroup);
		});
		CssStyles.style(rbSelectEventGroup, CssStyles.VSPACE_NONE);
		rbSelectEventGroup.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				eventGroupGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(eventGroupGrid.getSelectedItems().size() > 0);
				}
			}
		});

		mainLayout.addComponent(rbSelectEventGroup);
	}

	public void initializeGrid() {

		eventGroupGrid = new EventGroupSelectionGrid(criteria);
		eventGroupGrid.addSelectionListener(e -> {

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getAllSelectedItems().isEmpty());
			}
		});
	}

	private void addCreateEventGroupRadioGroup() {
		rbCreateEventGroup = new RadioButtonGroup<>();
		rbCreateEventGroup.setItems(CREATE_EVENT_GROUP);
		rbCreateEventGroup.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.eventNewEventGroup));
		rbCreateEventGroup.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				if (UiUtil.permitted(UserRight.EVENTGROUP_LINK, UserRight.EVENTGROUP_CREATE)) {
					rbSelectEventGroup.setValue(null);
				}
				eventGroupGrid.deselectAll();
				eventGroupGrid.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		mainLayout.addComponent(rbCreateEventGroup);
	}

	@Override
	protected Component initContent() {

		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		addInfoComponent();

		VerticalLayout filterLayout = new VerticalLayout();
		filterLayout.setSpacing(false);
		filterLayout.setMargin(false);
		filterLayout.setWidth(100, Unit.PERCENTAGE);

		filterLayout.addComponent(createFilterBar());

		mainLayout.addComponent(filterLayout);

		if (UiUtil.permitted(UserRight.EVENTGROUP_LINK)) {
			addSelectEventGroupRadioGroup();
		}
		mainLayout.addComponent(eventGroupGrid);
		if (UiUtil.permitted(UserRight.EVENTGROUP_CREATE)) {
			addCreateEventGroupRadioGroup();
		}

		if (UiUtil.permitted(UserRight.EVENTGROUP_LINK)) {
			rbSelectEventGroup.setValue(SELECT_EVENT_GROUP);
		} else if (UiUtil.permitted(UserRight.EVENTGROUP_CREATE)) {
			rbSelectEventGroup.setValue(CREATE_EVENT_GROUP);
		}

		return mainLayout;
	}

	@Override
	protected void doSetValue(EventGroupIndexDto newValue) {
		rbSelectEventGroup.setValue(SELECT_EVENT_GROUP);

		if (newValue != null) {
			eventGroupGrid.select(newValue);
		}
	}

	@Override
	public EventGroupIndexDto getValue() {

		if (eventGroupGrid != null) {
			EventGroupIndexDto value = eventGroupGrid.getSelectedItems().stream().findFirst().orElse(null);
			return value;
		}

		return null;
	}

	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	public HorizontalLayout createFilterBar() {

		HorizontalLayout filterLayout = new HorizontalLayout();
		filterLayout.setSpacing(true);
		filterLayout.setMargin(false);
		filterLayout.setSizeUndefined();

		searchField.setId("search");
		searchField.setWidth(200, Unit.PIXELS);

		searchField.setCaption(I18nProperties.getString(Strings.promptEventGroupSearchField));

		searchField.addValueChangeListener(e -> updateFreeTextFilter(e.getValue()));

		filterLayout.addComponent(searchField);

		searchEventField.setId("searchEvent");
		searchEventField.setWidth(200, Unit.PIXELS);

		searchEventField.setCaption(I18nProperties.getString(Strings.promptEventGroupSearchFieldEvent));

		searchEventField.addValueChangeListener(e -> updateFreeTextEventFilter(e.getValue()));

		filterLayout.addComponent(searchEventField);

		return filterLayout;
	}

	private void updateFreeTextFilter(String freeText) {
		criteria.setFreeText(freeText);

		eventGroupGrid.setCriteria(criteria);
		eventGroupGrid.getSelectedItems();
	}

	private void updateFreeTextEventFilter(String freeText) {
		criteria.setFreeTextEvent(freeText);

		eventGroupGrid.setCriteria(criteria);
		eventGroupGrid.getSelectedItems();
	}

	public EventGroupSelectionGrid getEventGroupGrid() {
		return eventGroupGrid;
	}
}
