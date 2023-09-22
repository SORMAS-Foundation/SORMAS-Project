/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.samples.humansample;

import java.util.List;
import java.util.function.Consumer;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.VerticalLayout;

import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.VaadinUiUtil;

public class SampleSelectionField extends CustomField<SampleDto> {

	public static final String CREATE_SAMPLE = "createSample";
	public static final String SELECT_SIMILAR_SAMPLE = "selectSimilarSample";
	public static final String SELECT_OTHER_SAMPLE = "selectOtherSample";

	private final List<SampleDto> similarSamples;
	private final List<SampleDto> otherSamples;
	private final String infoText;
	private final HorizontalLayout detailsComponent;
	private VerticalLayout mainLayout;
	private SampleSelectionGrid similarSampleGrid;
	private SampleSelectionGrid otherSampleGrid;
	private RadioButtonGroup<String> rbSelectSimilarSample;
	private RadioButtonGroup<String> rbSelectOtherSample;
	private RadioButtonGroup<String> rbCreateSample;
	private Consumer<Boolean> selectionChangeCallback;

	public SampleSelectionField(List<SampleDto> similarSamples, List<SampleDto> otherSamples, String infoText, HorizontalLayout detailsComponent) {
		this.similarSamples = similarSamples;
		this.otherSamples = otherSamples;
		this.infoText = infoText;
		this.detailsComponent = detailsComponent;
	}

	/**
	 * Callback is executed with 'true' when a grid entry or "Create new person" is selected.
	 */
	public void setSelectionChangeCallback(Consumer<Boolean> callback) {
		this.selectionChangeCallback = callback;
	}

	private static RadioButtonGroup<String> createSelectSelectRadioGroup(String value, String captionTag) {

		RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
		radioGroup.setItems(value);
		radioGroup.setItemCaptionGenerator(item -> I18nProperties.getCaption(captionTag));
		CssStyles.style(radioGroup, CssStyles.VSPACE_NONE);

		return radioGroup;
	}

	private static RadioButtonGroup<String> createSampleCreateRadioGroup() {

		RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
		radioGroup.setItems(CREATE_SAMPLE);
		radioGroup.setItemCaptionGenerator(item -> I18nProperties.getCaption(Captions.sampleCreateNew));

		return radioGroup;
	}

	@SafeVarargs
	private static void addRadioListener(RadioButtonGroup<String> radio, SampleSelectionGrid sampleGrid, RadioButtonGroup<String>... otherRadios) {
		radio.addValueChangeListener(e -> {
			if (e.getValue() == null) {
				if (sampleGrid != null) {
					sampleGrid.deselectAll();
					sampleGrid.setEnabled(false);
				}
			} else {
				for (RadioButtonGroup<String> otherRadio : otherRadios) {
					otherRadio.setValue(null);
				}

				if (sampleGrid != null) {
					sampleGrid.setEnabled(true);
				}
			}
		});
	}

	@Override
	protected Component initContent() {
		mainLayout = new VerticalLayout();
		mainLayout.setSpacing(true);
		mainLayout.setMargin(false);
		mainLayout.setSizeUndefined();
		mainLayout.setWidth(100, Unit.PERCENTAGE);

		addInfoComponent();
		addDetailsComponent();

		similarSampleGrid = createSampleGrid(similarSamples);
		rbSelectSimilarSample = createSelectSelectRadioGroup(SELECT_SIMILAR_SAMPLE, Captions.selectSimilarSample);
		mainLayout.addComponent(rbSelectSimilarSample);
		if (similarSamples.isEmpty()) {
			rbSelectSimilarSample.setEnabled(false);
		} else {
			mainLayout.addComponent(similarSampleGrid);
		}

		otherSampleGrid = createSampleGrid(otherSamples);
		rbSelectOtherSample = createSelectSelectRadioGroup(SELECT_OTHER_SAMPLE, Captions.selectOtherSample);
		mainLayout.addComponent(rbSelectOtherSample);
		if (otherSamples.isEmpty()) {
			rbSelectOtherSample.setEnabled(false);
		} else {
			mainLayout.addComponent(otherSampleGrid);
		}

		rbCreateSample = createSampleCreateRadioGroup();
		mainLayout.addComponent(rbCreateSample);

		addRadioListeners();

		if (!similarSamples.isEmpty()) {
			rbSelectSimilarSample.setValue(SELECT_SIMILAR_SAMPLE);
		} else if (!otherSamples.isEmpty()) {
			rbSelectOtherSample.setValue(SELECT_OTHER_SAMPLE);
		} else {
			rbCreateSample.setValue(CREATE_SAMPLE);
		}

		return mainLayout;
	}

	private void addDetailsComponent() {
		mainLayout.addComponent(detailsComponent);
	}

	@Override
	protected void doSetValue(SampleDto sampleDto) {
		if (similarSamples.contains(sampleDto)) {
			rbSelectSimilarSample.setValue(SELECT_SIMILAR_SAMPLE);
			similarSampleGrid.select(sampleDto);
		} else if (otherSamples.contains(sampleDto)) {
			rbSelectOtherSample.setValue(SELECT_OTHER_SAMPLE);
			otherSampleGrid.select(sampleDto);
		} else if (!similarSamples.isEmpty()) {
			rbSelectSimilarSample.setValue(SELECT_SIMILAR_SAMPLE);
		} else if (!otherSamples.isEmpty()) {
			rbSelectOtherSample.setValue(SELECT_OTHER_SAMPLE);
		} else {
			rbCreateSample.setValue(CREATE_SAMPLE);
		}
	}

	@Override
	public SampleDto getValue() {
		if (similarSampleGrid != null && similarSampleGrid.isEnabled()) {
			return (SampleDto) similarSampleGrid.getSelectedRow();
		}

		if (otherSampleGrid != null && otherSampleGrid.isEnabled()) {
			return (SampleDto) otherSampleGrid.getSelectedRow();
		}

		return null;
	}

	private void addInfoComponent() {
		mainLayout.addComponent(VaadinUiUtil.createInfoComponent(infoText));
	}

	private SampleSelectionGrid createSampleGrid(List<SampleDto> samples) {
		SampleSelectionGrid grid = new SampleSelectionGrid(samples);

		grid.addSelectionListener(e -> {
			if (selectionChangeCallback != null) {
				selectionChangeCallback.accept(!e.getSelected().isEmpty());
			}
		});

		grid.setEnabled(false);

		return grid;
	}

	private void addRadioListeners() {
		addRadioListener(rbSelectSimilarSample, similarSampleGrid, rbSelectOtherSample, rbCreateSample);
		addRadioListener(rbSelectOtherSample, otherSampleGrid, rbSelectSimilarSample, rbCreateSample);
		addRadioListener(rbCreateSample, null, rbSelectSimilarSample, rbSelectOtherSample);

		if (selectionChangeCallback != null) {
			rbCreateSample.addValueChangeListener(e -> {
				selectionChangeCallback.accept(e.getValue() != null);
			});
		}
	}
}
