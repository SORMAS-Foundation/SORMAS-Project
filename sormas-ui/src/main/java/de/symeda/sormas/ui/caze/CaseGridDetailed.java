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
	protected Stream<String> getPersonColumns() {
		return Stream.concat(
			super.getPersonColumns(),
			Stream.of(
				CaseIndexDetailedDto.AGE_AND_BIRTH_DATE,
				CaseIndexDetailedDto.SEX,
				CaseIndexDetailedDto.CITY,
				CaseIndexDetailedDto.ADDRESS,
				CaseIndexDetailedDto.POSTAL_CODE,
				CaseIndexDetailedDto.PHONE));
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initColumns() {

		super.initColumns();
		getColumn(CaseIndexDetailedDto.SEX).setWidth(80);
		getColumn(CaseIndexDetailedDto.AGE_AND_BIRTH_DATE).setWidth(100);
		getColumn(CaseIndexDetailedDto.CITY).setWidth(150);
		getColumn(CaseIndexDetailedDto.ADDRESS).setWidth(200);
		getColumn(CaseIndexDetailedDto.POSTAL_CODE).setWidth(100);
		getColumn(CaseIndexDetailedDto.PHONE).setWidth(100);

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
