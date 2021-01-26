package de.symeda.sormas.ui.samples;

import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;

public class PathogenTestSelectionGrid extends Grid {

	public PathogenTestSelectionGrid(List<PathogenTestDto> tests) {
		buildGrid();
		setContainerData(tests);
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<PathogenTestDto> container = new BeanItemContainer<>(PathogenTestDto.class);
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
			column.setHeaderCaption(I18nProperties.getPrefixCaption(PathogenTestDto.I18N_PREFIX, column.getPropertyId().toString()));
		}
	}

	private void setContainerData(List<PathogenTestDto> tests) {
		getContainer().removeAllItems();
		getContainer().addAll(tests);
		setHeightByRows(tests.size() > 0 ? (tests.size() <= 10 ? tests.size() : 10) : 1);
	}

	private BeanItemContainer<PathogenTestDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<PathogenTestDto>) container.getWrappedContainer();
	}
}
