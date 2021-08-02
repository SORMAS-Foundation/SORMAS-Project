package de.symeda.sormas.ui.immunization.components.fields.pickorcreate;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.DateRenderer;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.utils.DateHelper;

public class ImmunizationSelectionGrid extends Grid {

	private List<ImmunizationIndexDto> immunizations;

	public ImmunizationSelectionGrid(List<ImmunizationIndexDto> immunizations) {
		if (immunizations != null) {
			this.immunizations = immunizations;
		} else {
			this.immunizations = new ArrayList<>();
		}
		buildGrid();
		reload();
	}

	private void buildGrid() {

		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<ImmunizationIndexDto> container = new BeanItemContainer<>(ImmunizationIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
			ImmunizationIndexDto.MEANS_OF_IMMUNIZATION,
			ImmunizationIndexDto.MANAGEMENT_STATUS,
			ImmunizationIndexDto.IMMUNIZATION_STATUS,
			ImmunizationIndexDto.START_DATE,
			ImmunizationIndexDto.END_DATE,
			ImmunizationIndexDto.RECOVERY_DATE);

		getColumn(ImmunizationIndexDto.START_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		getColumn(ImmunizationIndexDto.END_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));
		getColumn(ImmunizationIndexDto.RECOVERY_DATE)
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.getPrefixCaption(ImmunizationIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<ImmunizationIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<ImmunizationIndexDto>) container.getWrappedContainer();
	}

	public void reload() {

		getContainer().removeAllItems();
		getContainer().addAll(immunizations);
		this.refreshAllRows();
		setHeightByRows(immunizations.size() > 0 ? (immunizations.size() <= 10 ? immunizations.size() : 10) : 1);
	}

	public void setCases(List<ImmunizationIndexDto> cases) {
		this.immunizations = cases;
		reload();
	}

	public void clearCases() {
		immunizations.clear();
		reload();
	}
}
