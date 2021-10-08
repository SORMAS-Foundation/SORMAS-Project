package de.symeda.sormas.ui.samples;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class PathogenTestSelectionField extends CustomField<PathogenTestDto> {

	public static final String CREATE_TEST = "createTest";
	public static final String SELECT_TEST = "selectTest";

	private final List<PathogenTestDto> tests;
	private final String infoText;
	private VerticalLayout mainLayout;
	private PathogenTestSelectionGrid testSelectionGrid;
	private RadioButtonGroup<String> rbSelectTest;
	private RadioButtonGroup<String> rbCreateTest;
	private Consumer<Boolean> selectionChangeCallback;

	public PathogenTestSelectionField(List<PathogenTestDto> tests, String infoText) {
		this.tests = tests;
		this.infoText = infoText;

		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoText));
	}

	private void initializeGrid() {
		testSelectionGrid = new PathogenTestSelectionGrid(tests);

		testSelectionGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				rbCreateTest.setValue(null);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
	}

	private void addSelectTestRadioGroup() {

		rbSelectTest = new RadioButtonGroup<>();
		rbSelectTest.setItems(SELECT_TEST);
		rbSelectTest.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.pathogenTestSelect));
		CssStyles.style(rbSelectTest, CssStyles.VSPACE_NONE);
		rbSelectTest.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateTest.setValue(null);
				testSelectionGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(testSelectionGrid.getSelectedRow() != null);
				}
			}
		});

		mainLayout.addComponent(rbSelectTest);
	}

	private void addCreateTestRadioGroup() {

		rbCreateTest = new RadioButtonGroup<>();
		rbCreateTest.setItems(CREATE_TEST);
		rbCreateTest.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.pathogenTestCreateNew));
		rbCreateTest.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbSelectTest.setValue(null);
				testSelectionGrid.deselectAll();
				testSelectionGrid.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		mainLayout.addComponent(rbCreateTest);
	}

	/**
	 * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
	 */
	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Sizeable.Unit.PERCENTAGE);

		addInfoComponent();
		addSelectTestRadioGroup();
		mainLayout.addComponent(testSelectionGrid);
		addCreateTestRadioGroup();

		rbSelectTest.setValue(SELECT_TEST);

		return mainLayout;
	}

	@Override
	protected void doSetValue(PathogenTestDto testDto) {
		rbSelectTest.setValue(SELECT_TEST);

		if (testDto != null) {
			testSelectionGrid.select(testDto);
		}
	}

	@Override
	public PathogenTestDto getValue() {
		if (testSelectionGrid != null) {
			PathogenTestDto value = (PathogenTestDto) testSelectionGrid.getSelectedRow();
			return value;
		}

		return null;
	}
}
