package de.symeda.sormas.ui.samples;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleSelectionField extends CustomField<SampleDto> {

	public static final String CREATE_SAMPLE = "createSample";
	public static final String SELECT_SAMPLE = "selectSample";

	private final List<SampleDto> samples;
	private final String infoText;
	private VerticalLayout mainLayout;
	private SampleSelectionGrid sampleSelectionGrid;
	private RadioButtonGroup<String> rbSelectSample;
	private RadioButtonGroup<String> rbCreateSample;
	private Consumer<Boolean> selectionChangeCallback;

	public SampleSelectionField(List<SampleDto> samples, String infoText) {
		this.samples = samples;
		this.infoText = infoText;

		initializeGrid();
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoText));
	}

	private void initializeGrid() {
		sampleSelectionGrid = new SampleSelectionGrid(samples);

		sampleSelectionGrid.addSelectionListener(e -> {
			if (e.getSelected().size() > 0) {
				rbCreateSample.setValue(null);
			}

			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});
	}

	private void addSelectSelectRadioGroup() {

		rbSelectSample = new RadioButtonGroup<>();
		rbSelectSample.setItems(SELECT_SAMPLE);
		rbSelectSample.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.sampleSelect));
		CssStyles.style(rbSelectSample, CssStyles.VSPACE_NONE);
		rbSelectSample.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbCreateSample.setValue(null);
				sampleSelectionGrid.setEnabled(true);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(sampleSelectionGrid.getSelectedRow() != null);
				}
			}
		});

		mainLayout.addComponent(rbSelectSample);
	}

	private void addCreateSampleRadioGroup() {

		rbCreateSample = new RadioButtonGroup<>();
		rbCreateSample.setItems(CREATE_SAMPLE);
		rbCreateSample.setItemCaptionGenerator((item) -> I18nProperties.getCaption(Captions.sampleCreateNew));
		rbCreateSample.addValueChangeListener(e -> {
			if (e.getValue() != null) {
				rbSelectSample.setValue(null);
				sampleSelectionGrid.deselectAll();
				sampleSelectionGrid.setEnabled(false);
				if (selectionChangeCallback != null) {
					selectionChangeCallback.accept(true);
				}
			}
		});

		mainLayout.addComponent(rbCreateSample);
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
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		addInfoComponent();
		addSelectSelectRadioGroup();
		mainLayout.addComponent(sampleSelectionGrid);
		addCreateSampleRadioGroup();

		rbSelectSample.setValue(SELECT_SAMPLE);

		return mainLayout;
	}

	@Override
	protected void doSetValue(SampleDto sampleDto) {
		rbSelectSample.setValue(SELECT_SAMPLE);

		if (sampleDto != null) {
			sampleSelectionGrid.select(sampleDto);
		}
	}

	@Override
	public SampleDto getValue() {
		if (sampleSelectionGrid != null) {
			SampleDto value = (SampleDto) sampleSelectionGrid.getSelectedRow();
			return value;
		}

		return null;
	}
}
