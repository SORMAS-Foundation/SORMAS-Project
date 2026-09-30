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
package de.symeda.sormas.ui.events.sevenoneseven;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717AssessmentDto;
import de.symeda.sormas.api.event.sevenoneseven.Event717Interval;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.ui.utils.ButtonHelper;
import de.symeda.sormas.ui.utils.CssStyles;

/**
 * Shows the entries of a 7-1-7 assessment grouped by interval, with a colored interval cell on the left and the entries of
 * that interval next to it, following the 7-1-7 assessment tool. The entries themselves are held by the given (not
 * displayed) field, which takes care of committing and discarding them together with the assessment form.
 */
@SuppressWarnings({
	"serial",
	"java:S2160" })
public class Event717IntervalEntriesLayout<E extends EntityDto> extends VerticalLayout {

	private static final int INTERVAL_CELL_WIDTH = 90;

	private final transient AbstractEvent717EntriesField<E> entriesField;
	private final transient Function<E, Event717Interval> intervalProvider;
	private final transient Function<E, String> titleProvider;
	private final transient Function<E, String> detailsProvider;
	private final transient Consumer<Event717Interval> createEntryHandler;
	private final boolean isEditAllowed;

	private final GridLayout grid;

	public Event717IntervalEntriesLayout(
		String caption,
		AbstractEvent717EntriesField<E> entriesField,
		Function<E, Event717Interval> intervalProvider,
		Function<E, String> titleProvider,
		Function<E, String> detailsProvider,
		Consumer<Event717Interval> createEntryHandler,
		boolean isEditAllowed) {

		this.entriesField = entriesField;
		this.intervalProvider = intervalProvider;
		this.titleProvider = titleProvider;
		this.detailsProvider = detailsProvider;
		this.createEntryHandler = createEntryHandler;
		this.isEditAllowed = isEditAllowed;

		setWidth(100, Unit.PERCENTAGE);
		setMargin(false);
		setSpacing(false);
		addStyleName(CssStyles.VSPACE_3);

		Label captionLabel = new Label(caption);
		captionLabel.addStyleName(CssStyles.H4);
		addComponent(captionLabel);

		grid = new GridLayout(2, Event717Interval.values().length * 2 + 1);
		grid.setWidth(100, Unit.PERCENTAGE);
		grid.setSpacing(false);
		grid.setColumnExpandRatio(0, 0);
		grid.setColumnExpandRatio(1, 1);
		addComponent(grid);

		refresh();

		entriesField.addValueChangeListener(e -> refresh());
	}

	/**
	 * Rebuilds the rows from the current entries of the field.
	 */
	public final void refresh() {

		grid.removeAllComponents();

		// every interval is enclosed by a divider row, so that the lines align with the colored interval cells
		Event717Interval[] intervals = Event717Interval.values();
		addDividerRow(0);
		for (int i = 0; i < intervals.length; i++) {
			int row = i * 2 + 1;
			grid.addComponent(createIntervalCell(intervals[i]), 0, row);
			grid.addComponent(createEntriesCell(intervals[i]), 1, row);
			addDividerRow(row + 1);
		}
	}

	private void addDividerRow(int row) {

		Label divider = new Label();
		divider.setWidth(100, Unit.PERCENTAGE);
		divider.addStyleName(CssStyles.LABEL_BOTTOM_LINE);
		grid.addComponent(divider, 0, row, 1, row);
	}

	private Label createIntervalCell(Event717Interval interval) {

		Label label = Event717IntervalColors.createIntervalLabel(interval);
		label.setWidth(INTERVAL_CELL_WIDTH, Unit.PIXELS);
		label.setHeight(100, Unit.PERCENTAGE);
		return label;
	}

	private VerticalLayout createEntriesCell(Event717Interval interval) {

		VerticalLayout layout = new VerticalLayout();
		layout.setWidth(100, Unit.PERCENTAGE);
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.addStyleName(CssStyles.HSPACE_LEFT_4);

		List<E> entries = getEntries(interval);

		if (isEditAllowed && entries.size() < Event717AssessmentDto.MAX_ENTRIES_PER_INTERVAL) {
			Button addButton =
				ButtonHelper.createButton(Captions.actionNewEntry, e -> createEntryHandler.accept(interval), ValoTheme.BUTTON_LINK);
			layout.addComponent(addButton);
		} else if (entries.isEmpty()) {
			// keeps the height of the colored interval cell when there is nothing to show
			Label spacer = new Label("&nbsp;", ContentMode.HTML);
			spacer.addStyleName(CssStyles.VSPACE_3);
			layout.addComponent(spacer);
		}

		entries.forEach(entry -> layout.addComponent(createEntryRow(entry)));

		return layout;
	}

	private HorizontalLayout createEntryRow(E entry) {

		HorizontalLayout row = new HorizontalLayout();
		row.setWidth(100, Unit.PERCENTAGE);
		row.setSpacing(false);
		row.setMargin(false);

		Button editButton = ButtonHelper.createIconButtonWithCaption(
			entry.getUuid() + "-edit",
			null,
			isEditAllowed ? VaadinIcons.EDIT : VaadinIcons.EYE,
			e -> entriesField.editExistingEntry(entry),
			ValoTheme.BUTTON_BORDERLESS);

		VerticalLayout texts = new VerticalLayout();
		texts.setWidth(100, Unit.PERCENTAGE);
		texts.setSpacing(false);
		texts.setMargin(false);

		Label titleLabel = new Label(titleProvider.apply(entry));
		titleLabel.setWidth(100, Unit.PERCENTAGE);
		titleLabel.addStyleName(CssStyles.LABEL_WHITE_SPACE_NORMAL);
		texts.addComponent(titleLabel);

		String details = detailsProvider != null ? detailsProvider.apply(entry) : null;
		if (StringUtils.isNotBlank(details)) {
			// slightly smaller than the description, but larger than the small label style of the theme
			Label detailsLabel = new Label(details);
			detailsLabel.setWidth(100, Unit.PERCENTAGE);
			CssStyles.style(detailsLabel, CssStyles.LABEL_SECONDARY, CssStyles.LABEL_WHITE_SPACE_NORMAL);
			texts.addComponent(detailsLabel);
		}

		row.addComponents(editButton, texts);
		row.setComponentAlignment(editButton, Alignment.TOP_LEFT);
		row.setExpandRatio(editButton, 0);
		row.setExpandRatio(texts, 1);
		row.addStyleName(CssStyles.VSPACE_4);

		return row;
	}

	private List<E> getEntries(Event717Interval interval) {

		Collection<E> entries = entriesField.getValue();
		if (entries == null) {
			return Collections.emptyList();
		}
		return entries.stream().filter(entry -> intervalProvider.apply(entry) == interval).collect(Collectors.toList());
	}

}
