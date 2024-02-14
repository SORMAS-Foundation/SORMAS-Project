package de.symeda.sormas.ui.caze.components.caseselection;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.data.util.GeneratedPropertyContainer;
import com.vaadin.v7.shared.ui.grid.HeightMode;
import com.vaadin.v7.ui.Grid;
import com.vaadin.v7.ui.renderers.DateRenderer;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseSelectionDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.AgeAndBirthDateDtoConverterV7;
import de.symeda.sormas.ui.utils.V7UuidRenderer;

@SuppressWarnings("serial")
public class CaseSelectionGrid extends Grid {

	private List<CaseSelectionDto> cases;

	public CaseSelectionGrid(List<CaseSelectionDto> cases) {

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

		BeanItemContainer<CaseSelectionDto> container = new BeanItemContainer<>(CaseSelectionDto.class);
		GeneratedPropertyContainer generatedContainer = new GeneratedPropertyContainer(container);
		setContainerDataSource(generatedContainer);

		setColumns(
			CaseSelectionDto.UUID,
			CaseSelectionDto.EPID_NUMBER,
			CaseSelectionDto.EXTERNAL_ID,
			CaseSelectionDto.PERSON_FIRST_NAME,
			CaseSelectionDto.PERSON_LAST_NAME,
			CaseSelectionDto.AGE_AND_BIRTH_DATE,
			CaseSelectionDto.RESPONSIBLE_DISTRICT_NAME,
			CaseSelectionDto.HEALTH_FACILITY_NAME,
			CaseSelectionDto.REPORT_DATE,
			CaseSelectionDto.SEX,
			CaseSelectionDto.CASE_CLASSIFICATION,
			CaseSelectionDto.OUTCOME);

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)) {
			getColumn(CaseSelectionDto.EPID_NUMBER).setHidden(true);
		} else {
			getColumn(CaseSelectionDto.EXTERNAL_ID).setHidden(true);
		}

		getColumn(CaseSelectionDto.UUID).setRenderer(new V7UuidRenderer());
		getColumn(CaseSelectionDto.AGE_AND_BIRTH_DATE).setConverter(new AgeAndBirthDateDtoConverterV7());
		getColumn(CaseSelectionDto.REPORT_DATE).setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(I18nProperties.getUserLanguage())));

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(CaseSelectionDto.RESPONSIBLE_DISTRICT_NAME).setHidden(true);
		}

		for (Column column : getColumns()) {
			column.setHeaderCaption(
				I18nProperties.getPrefixCaption(CaseSelectionDto.I18N_PREFIX, column.getPropertyId().toString(), column.getHeaderCaption()));
		}
	}

	@SuppressWarnings("unchecked")
	private BeanItemContainer<CaseSelectionDto> getContainer() {
		GeneratedPropertyContainer container = (GeneratedPropertyContainer) super.getContainerDataSource();
		return (BeanItemContainer<CaseSelectionDto>) container.getWrappedContainer();
	}

	public void reload() {

		getContainer().removeAllItems();
		getContainer().addAll(cases);
		this.refreshAllRows();
		setHeightByRows(cases.size() > 0 ? (cases.size() <= 10 ? cases.size() : 10) : 1);
	}

	public void setCases(List<CaseSelectionDto> cases) {
		this.cases = cases;
		reload();
	}

	public void clearCases() {
		cases.clear();
		reload();
	}
}
