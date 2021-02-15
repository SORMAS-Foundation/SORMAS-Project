package de.symeda.sormas.ui.person;

import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.data.sort.SortDirection;

import com.vaadin.ui.renderers.TextRenderer;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.caze.CaseIndexDetailedDto;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class PersonGrid extends FilteredGrid<PersonIndexDto, PersonCriteria> {

	public PersonGrid(PersonCriteria criteria) {
		super(PersonIndexDto.class);
		setSizeFull();
		setLazyDataProvider();
		setCriteria(criteria);
		initColumns();
		addItemClickListener(
			new ShowDetailsListener<>(PersonIndexDto.UUID, e -> ControllerProvider.getPersonController().navigateToPerson(e.getUuid())));
	}

	private void initColumns() {

		setColumns(
			PersonIndexDto.UUID,
			PersonIndexDto.FIRST_NAME,
			PersonIndexDto.LAST_NAME,
			PersonIndexDto.AGE_AND_BIRTH_DATE,
			PersonIndexDto.SEX,
			PersonIndexDto.DISTRICT,
			PersonIndexDto.STREET,
			PersonIndexDto.HOUSE_NUMBER,
			PersonIndexDto.POSTAL_CODE,
			PersonIndexDto.CITY,
			PersonIndexDto.PHONE,
			PersonIndexDto.EMAIL_ADDRESS);

		((Column<PersonIndexDto, String>) getColumn(PersonIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<PersonIndexDto, AgeAndBirthDateDto>) getColumn(PersonIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
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


		for (Column<PersonIndexDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaptionWithDefault(
					column.getId(),
					column.getCaption(),
					PersonIndexDto.I18N_PREFIX,
					PersonDto.I18N_PREFIX,
					LocationDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}
	}

	public void setLazyDataProvider() {
		DataProvider<PersonIndexDto, PersonCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getPersonFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getPersonFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void reload() {
		getDataProvider().refreshAll();
	}
}
