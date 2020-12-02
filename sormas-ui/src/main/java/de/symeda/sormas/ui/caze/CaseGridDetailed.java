package de.symeda.sormas.ui.caze;

import java.util.List;
import java.util.stream.Stream;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class CaseGridDetailed extends AbstractCaseGrid<CaseIndexDetailedDto> {

	private static final long serialVersionUID = 3734206041728541742L;

	public CaseGridDetailed(CaseCriteria criteria) {
		super(CaseIndexDetailedDto.class, criteria);
	}

	@Override
	protected List<CaseIndexDetailedDto> getGridData(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties) {
		return FacadeProvider.getCaseFacade().getIndexDetailedList(caseCriteria, first, max, sortProperties);
	}

	@Override
	protected Stream<String> getGridColumns() {
		return Stream.concat(super.getGridColumns(), Stream.of(CaseIndexDetailedDto.REPORTING_USER));
	}

	@Override
	public Stream<String> getEventColumns() {
		return Stream.of(
			CaseIndexDetailedDto.EVENT_COUNT,
			CaseIndexDetailedDto.LATEST_EVENT_ID,
			CaseIndexDetailedDto.LATEST_EVENT_STATUS,
			CaseIndexDetailedDto.LATEST_EVENT_TITLE);
	}

	@Override
	protected Stream<String> getPersonColumns() {
		return Stream.concat(
			super.getPersonColumns(),
			Stream.of(
				CaseIndexDetailedDto.SEX,
				CaseIndexDetailedDto.AGE_AND_BIRTH_DATE,
				CaseIndexDetailedDto.POSTAL_CODE,
				CaseIndexDetailedDto.CITY,
				CaseIndexDetailedDto.STREET,
				CaseIndexDetailedDto.HOUSE_NUMBER,
				CaseIndexDetailedDto.ADDITIONAL_INFORMATION,
				CaseIndexDetailedDto.PHONE));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initColumns() {

		super.initColumns();

		getColumn(CaseIndexDetailedDto.SEX).setWidth(80);
		getColumn(CaseIndexDetailedDto.AGE_AND_BIRTH_DATE).setWidth(100);
		getColumn(CaseIndexDetailedDto.POSTAL_CODE).setWidth(100);
		getColumn(CaseIndexDetailedDto.CITY).setWidth(150);
		getColumn(CaseIndexDetailedDto.STREET).setWidth(150);
		getColumn(CaseIndexDetailedDto.HOUSE_NUMBER).setWidth(50);
		getColumn(CaseIndexDetailedDto.ADDITIONAL_INFORMATION).setWidth(200);
		getColumn(CaseIndexDetailedDto.PHONE).setWidth(100);
		getColumn(CaseIndexDetailedDto.EVENT_COUNT).setWidth(80).setSortable(false);
		getColumn(CaseIndexDetailedDto.LATEST_EVENT_ID).setWidth(80).setSortable(false);
		getColumn(CaseIndexDetailedDto.LATEST_EVENT_STATUS).setWidth(80).setSortable(false);
		getColumn(CaseIndexDetailedDto.LATEST_EVENT_TITLE).setWidth(150).setSortable(false);

		((Column<CaseIndexDetailedDto, String>) getColumn(CaseIndexDetailedDto.LATEST_EVENT_ID)).setRenderer(new UuidRenderer());
		addItemClickListener(
			new ShowDetailsListener<>(
				CaseIndexDetailedDto.LATEST_EVENT_ID,
				c -> ControllerProvider.getEventController().navigateToData(c.getLatestEventId())));

		((Column<CaseIndexDetailedDto, AgeAndBirthDateDto>) getColumn(CaseIndexDetailedDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getBirthdateDD(),
					value.getBirthdateMM(),
					value.getBirthdateYYYY(),
					I18nProperties.getUserLanguage()),
			new TextRenderer());
	}
}
