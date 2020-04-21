package de.symeda.sormas.ui.caze;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.utils.AgeAndBirthDateRendererV7;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

@SuppressWarnings("serial")
public class CaseSelectionGrid extends Grid {

	private List<CaseIndexDto> cases;

	public CaseSelectionGrid(List<CaseIndexDto> cases) {
		if (cases != null) {
			this.cases = cases;
		} else {
			this.cases = new ArrayList<>();
		}
		buildGrid();
		reload();
	}

	private void buildGrid() {
		setSizeFull();
		setSelectionMode(SelectionMode.SINGLE);
		setHeightMode(HeightMode.ROW);

		BeanItemContainer<CaseIndexDto> container = new BeanItemContainer<>(CaseIndexDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(CaseIndexDto.UUID, CaseIndexDto.EPID_NUMBER, CaseIndexDto.EXTERNAL_ID, CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME,
				CaseIndexDto.AGE_AND_BIRTH_DATE, CaseIndexDto.DISTRICT_NAME, CaseIndexDto.HEALTH_FACILITY_NAME, CaseIndexDto.REPORT_DATE,
				CaseIndexDto.SEX, CaseIndexDto.CASE_CLASSIFICATION, CaseIndexDto.OUTCOME);

		if (FacadeProvider.getConfigFacade().isGermanServer()) {
			getColumn(CaseIndexDto.EPID_NUMBER).setHidden(true);
		} else {
			getColumn(CaseIndexDto.EXTERNAL_ID).setHidden(true);			
		}

		getColumn(CaseIndexDto.UUID).setRenderer(new V7UuidRenderer());
		getColumn(CaseIndexDto.AGE_AND_BIRTH_DATE).setRenderer(new AgeAndBirthDateRendererV7());
		getColumn(CaseIndexDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(FacadeProvider.getUserFacade().getCurrentUser().getLanguage())));

		for (Column column : getColumns()) {
			column.setHeaderCaption(I18nProperties.getPrefixCaption(
					CaseIndexDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<CaseIndexDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<CaseIndexDto>) container.getWrappedContainer();
	}

	public void reload() {
		getContainer().removeAllItems();
		getContainer().addAll(cases);
		this.refreshAllRows();
		setHeightByRows(cases.size() > 0 ? (cases.size() <= 10 ? cases.size() : 10) : 1);
	}

	public void setCases(List<CaseIndexDto> cases) {
		this.cases = cases;
		reload();
	}

	public void clearCases() {
		cases.clear();
		reload();
	}

}
