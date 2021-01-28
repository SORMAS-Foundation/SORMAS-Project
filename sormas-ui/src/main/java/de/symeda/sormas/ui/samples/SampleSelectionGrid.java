package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleDto;

public class SampleSelectionGrid extends Grid {

	public SampleSelectionGrid(List<SampleDto> samples) {
		buildGrid();
		setContainerData(samples);
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<SampleDto> container = new BeanItemContainer<>(SampleDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
			SampleDto.UUID,
			SampleDto.SAMPLE_DATE_TIME,
			SampleDto.RECEIVED_DATE,
			SampleDto.LAB_SAMPLE_ID,
			SampleDto.SAMPLE_MATERIAL,
			SampleDto.LAB,
			SampleDto.PATHOGEN_TEST_RESULT);

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(SampleDto.I18N_PREFIX, column.getPropertyId().toString()));
		}
	}

	private void setContainerData(List<SampleDto> samples) {
		getContainer().removeAllItems();
		getContainer().addAll(samples);
		setHeightByRows(samples.size() > 0 ? (samples.size() <= 10 ? samples.size() : 10) : 1);
	}

	private BeanItemContainer<SampleDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<SampleDto>) container.getWrappedContainer();
	}
}
