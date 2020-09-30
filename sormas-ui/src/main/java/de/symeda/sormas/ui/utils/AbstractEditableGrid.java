package de.symeda.sormas.ui.utils;

import static com.vaadin.ui.themes.ValoTheme.BUTTON_SMALL;
import static de.symeda.sormas.ui.utils.ButtonHelper.createButton;
import static de.symeda.sormas.ui.utils.CssStyles.H3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.components.grid.GridRowDragger;
import com.vaadin.ui.themes.ValoTheme;

import de.symeda.sormas.api.i18n.I18nProperties;

public abstract class AbstractEditableGrid<T> extends CustomLayout implements View {

	private static final String HEADING_LOC = "headingLoc";
	private static final String GRID_LOC = "gridLoc";
	private static final String ADDITIONAL_ROW_LOC = "additionalRowLoc";

	private static final String HTML_LAYOUT = loc(HEADING_LOC) + loc(GRID_LOC) + loc(ADDITIONAL_ROW_LOC);

	protected Grid<T> grid = new Grid();
	protected List<T> items = new ArrayList<>();
	protected List<T> savedItems = new ArrayList<>();

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

		gridLayout.addComponent(grid);
		gridLayout.setExpandRatio(grid, 1);

		grid.setHeightMode(HeightMode.UNDEFINED);
		grid.setWidth(100, Unit.PERCENTAGE);

		savedItems.addAll(savedElements);
		items.addAll(savedElements);
		grid.setItems(savedElements);
		grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		setSizeFull();

		new GridRowDragger<>(grid);
		grid.getColumns().stream().forEach(col -> {
			col.setSortable(false);
			col.setStyleGenerator(t -> "");
		});

		final Binder<T> binder = addColumnsBinder(allElements);

		CustomField deleteField = new CustomField() {

			@Override
			public Object getValue() {
				return null;
			}

			@Override
			protected void doSetValue(Object o) {

			}

			@Override
			protected Component initContent() {
				CssLayout cssLayout = new CssLayout();
				final Button deleteButton = createButton(Button::new, "deleteButton", "", BUTTON_SMALL);
				deleteButton.setIcon(VaadinIcons.TRASH);
				cssLayout.addComponent(deleteButton);
				return cssLayout;
			}
		};
		Binder.Binding<T, String> deleteBind = binder.bind(deleteField, d -> "X", (t, s) -> {
		});
		Grid.Column<T, String> deleteColumn = grid.addColumn(t -> "").setId("delete").setCaption("");
		deleteColumn.setEditorBinding(deleteBind);

		grid.addItemClickListener(e -> {
			int i = items.indexOf(e.getItem());

			if (e.getColumn() != null && "delete".equals(e.getColumn().getId())) {
				items.remove(i);
				grid.setItems(items);
			} else if (i > -1) {
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
		final Button additionalRow = createButton(getAdditionalRowCaption(), newRowEvent(), ValoTheme.BUTTON_LINK);
		buttonLayout.addComponent(additionalRow);
		buttonLayout.setComponentAlignment(additionalRow, Alignment.BOTTOM_RIGHT);
		addComponent(buttonLayout, ADDITIONAL_ROW_LOC);
	}

	public void discardGrid() {
		this.grid.setItems(savedItems);
	}

	protected abstract String getHeaderString();

	protected abstract String getAdditionalRowCaption();

	protected abstract Button.ClickListener newRowEvent();

	protected abstract Binder<T> addColumnsBinder(List<T> allElements);

	public List<T> getItems() {
		return items;
	}

	public void setSavedItems(List<T> savedItems) {
		this.savedItems = savedItems;
	}
}
