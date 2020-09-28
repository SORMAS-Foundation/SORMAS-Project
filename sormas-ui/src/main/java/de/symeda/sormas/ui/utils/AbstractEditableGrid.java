package de.symeda.sormas.ui.utils;

import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.navigator.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;

public abstract class AbstractEditableGrid<T> extends CustomLayout implements View {

	private static final String HEADING_LOC = "headingLoc";
	private static final String GRID_LOC = "gridLoc";
	private static final String ADDITIONAL_ROW_LOC = "additionalRowLoc";

	private static final String HTML_LAYOUT = loc(HEADING_LOC) + loc(GRID_LOC) + loc(ADDITIONAL_ROW_LOC);

	protected Grid<T> grid = new Grid();
	protected List<T> items = new ArrayList<>();

	public AbstractEditableGrid(List<T> savedElements, List<T> allElements) {

		setTemplateContents(HTML_LAYOUT);
		setWidth(100, Unit.PERCENTAGE);
		setHeight(100, Unit.PERCENTAGE);

		final Label headingLabel = new Label(I18nProperties.getString(getHeaderString()));
		headingLabel.addStyleName(H3);
		addComponent(headingLabel, HEADING_LOC);

		final HorizontalLayout gridLayout = new HorizontalLayout();
		gridLayout.setMargin(false);
		gridLayout.setSpacing(true);
		gridLayout.setWidth(100, Unit.PERCENTAGE);
		addComponent(gridLayout, GRID_LOC);

		grid.setSizeFull();
		gridLayout.addComponent(grid);
		gridLayout.setExpandRatio(grid, 1);

		items.addAll(savedElements);
		grid.setItems(savedElements);
		grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		setSizeFull();

		new GridRowDragger<>(grid);
		grid.getColumns().stream().forEach(col -> col.setSortable(false));

		final Binder<T> binder = addColumnsBinder(allElements);

		grid.addItemClickListener(e -> {
			int i = items.indexOf(e.getItem());
			if (i > -1) {
				grid.getEditor().editRow(i);
			}
		});

		grid.getEditor().setBinder(binder);
		grid.getEditor().setBuffered(false);
		grid.getEditor().setEnabled(true);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setMargin(false);
		buttonLayout.setSpacing(true);
		buttonLayout.setWidth(100, Unit.PERCENTAGE);
		final Button additionalRow = ButtonHelper.createButton(getAdditionalRowCaption(), newRowEvent(), ValoTheme.BUTTON_LINK);
		buttonLayout.addComponent(additionalRow);
		buttonLayout.setComponentAlignment(additionalRow, Alignment.BOTTOM_RIGHT);
		addComponent(buttonLayout, ADDITIONAL_ROW_LOC);
	}

	protected abstract String getHeaderString();

	protected abstract String getAdditionalRowCaption();

	protected abstract Button.ClickListener newRowEvent();

	protected abstract Binder<T> addColumnsBinder(List<T> allElements);

	public List<T> getItems() {
		return items;
	}
}
