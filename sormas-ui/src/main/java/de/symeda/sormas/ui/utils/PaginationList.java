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
package de.symeda.sormas.ui.utils;

import java.util.List;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public abstract class PaginationList<T> extends VerticalLayout {

	private static final long serialVersionUID = -1949084832307944448L;
	public static final String[] BUTTON_STYLES = {ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT, CssStyles.BUTTON_FILTER_SMALL};
	public static final String[] CURRENT_BUTTON_STYLES = {ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_DARK, CssStyles.BUTTON_FILTER_SMALL};

	protected final VerticalLayout listLayout;
	protected final HorizontalLayout paginationLayout;
	protected final int maxDisplayedEntries;
	private List<T> entries;
	private List<T> displayedEntries;
	private int currentPage;
	
	// Pagination buttons
	private Button firstPageButton;
	private Button lastPageButton;
	private Button nextPageButton;
	private Button nextNextPageButton;
	private Button previousPageButton;
	private Button previousPreviousPageButton;
	private Button currentPageButton;
	private Label previousGapLabel;
	private Label nextGapLabel;
	
	public PaginationList(int maxDisplayedEntries) {
		setMargin(false);
		setSpacing(false);
		this.maxDisplayedEntries = maxDisplayedEntries;
		this.currentPage = 1;
		
		this.listLayout = new VerticalLayout();
		listLayout.setMargin(false);
		listLayout.setSpacing(false);
		this.paginationLayout = new HorizontalLayout();
		paginationLayout.setMargin(false);
		paginationLayout.setSpacing(true);
		CssStyles.style(paginationLayout, CssStyles.SPACING_SMALL, CssStyles.VSPACE_TOP_4);
		initializePaginationLayout();
		
		addComponent(listLayout);
		addComponent(paginationLayout);
		setComponentAlignment(paginationLayout, Alignment.BOTTOM_RIGHT);
		
		setWidth(100, Unit.PERCENTAGE);
		addStyleName(CssStyles.SORMAS_LIST);
	}
	
	public abstract void reload();
	protected abstract void drawDisplayedEntries();
	
	protected void showPage(int pageNumber) {
		listLayout.removeAllComponents();
		int firstDisplayedEntryIndex = (pageNumber - 1) * maxDisplayedEntries;
		int lastDisplayedEntryIndex = pageNumber * maxDisplayedEntries;
		displayedEntries = entries.subList(firstDisplayedEntryIndex,
				entries.size() < lastDisplayedEntryIndex ? entries.size() : lastDisplayedEntryIndex);
		currentPage = pageNumber;
		drawDisplayedEntries();
		updatePaginationLayout();
	}
	
	private void initializePaginationLayout() {
		firstPageButton = ButtonHelper.createButtonWithCaption("fistPage", "|<", e -> {
			showPage(1);
		}, ValoTheme.BUTTON_BORDERLESS, CssStyles.BUTTON_FILTER, CssStyles.BUTTON_FILTER_LIGHT, CssStyles.BUTTON_FILTER_SMALL);

		paginationLayout.addComponent(firstPageButton);
		
		previousGapLabel = new Label("...");
		CssStyles.style(previousGapLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_PRIMARY);
		paginationLayout.addComponent(previousGapLabel);

		previousPreviousPageButton = ButtonHelper.createButtonWithCaption("previousPreviousPage", null, e -> {
			showPage(currentPage - 2);
		}, BUTTON_STYLES);

		paginationLayout.addComponent(previousPreviousPageButton);
		
		previousPageButton = ButtonHelper.createButtonWithCaption("previousPage", null, e -> {
			showPage(currentPage - 1);
		}, BUTTON_STYLES);

		paginationLayout.addComponent(previousPageButton);

		currentPageButton = ButtonHelper.createButtonWithCaption("currentPage", null, null,
				CURRENT_BUTTON_STYLES);
		paginationLayout.addComponent(currentPageButton);
		
		nextPageButton = ButtonHelper.createButtonWithCaption("nextPage", null, e -> {
			showPage(currentPage + 1);
		}, BUTTON_STYLES);

		paginationLayout.addComponent(nextPageButton);

		nextNextPageButton = ButtonHelper.createButtonWithCaption("nextNextPage", null, e -> {
			showPage(currentPage + 2);
		}, BUTTON_STYLES);

		paginationLayout.addComponent(nextNextPageButton);
		
		nextGapLabel = new Label("...");
		CssStyles.style(nextGapLabel, CssStyles.LABEL_BOLD, CssStyles.LABEL_PRIMARY);
		paginationLayout.addComponent(nextGapLabel);
		
		lastPageButton = ButtonHelper.createButtonWithCaption("", ">|", e -> {
			showPage(calculateLastPageNumber());
		}, BUTTON_STYLES);

		paginationLayout.addComponent(lastPageButton);
	}
	
	protected void updatePaginationLayout() {
		// Remove or re-add the pagination layout based on the number of list entries
		if (entries.size() <= maxDisplayedEntries) {
			removeComponent(paginationLayout);
			return;
		} else if (getComponentIndex(paginationLayout) == -1) {
			addComponent(paginationLayout);
			setComponentAlignment(paginationLayout, Alignment.BOTTOM_RIGHT);
		}
		
		int firstDisplayedEntryIndex = entries.indexOf(displayedEntries.get(0));
		int lastPageNumber = calculateLastPageNumber();

		// Enable/disable first and last page buttons
		firstPageButton.setEnabled(currentPage > 1);
		lastPageButton.setEnabled(currentPage < lastPageNumber);

		// Numbered button and gap labels visibility
		previousGapLabel.setVisible(currentPage >= 4);
		previousPreviousPageButton.setVisible(currentPage >= 3);
		previousPageButton.setVisible(currentPage >= 2);
		nextPageButton.setVisible(currentPage < lastPageNumber);
		nextNextPageButton.setVisible(currentPage < lastPageNumber - 1);
		nextGapLabel.setVisible(currentPage < lastPageNumber - 2);
		
		// Numbered button captions
		currentPageButton.setCaption(String.valueOf((int) Math.ceil((firstDisplayedEntryIndex + 1) / (double) maxDisplayedEntries)));
		if (previousPreviousPageButton.isVisible()) {
			previousPreviousPageButton.setCaption(String.valueOf(currentPage - 2));
		}
		if (previousPageButton.isVisible()) {
			previousPageButton.setCaption(String.valueOf(currentPage - 1));
		}
		if (nextPageButton.isVisible()) {
			nextPageButton.setCaption(String.valueOf(currentPage + 1));
		}
		if (nextNextPageButton.isVisible()) {
			nextNextPageButton.setCaption(String.valueOf(currentPage + 2));
		}
	}
	
	private int calculateLastPageNumber() {
		return (int) Math.ceil(entries.size() / (double) maxDisplayedEntries);
	}

	protected List<T> getDisplayedEntries() {
		return displayedEntries;
	}
	
	protected void setEntries(List<T> entries) {
		this.entries = entries;
	}
	
}
