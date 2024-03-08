package de.symeda.sormas.ui.person;

import java.util.List;

import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonCriteria;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.api.person.PersonIndexDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class PersonGrid extends FilteredGrid<PersonIndexDto, PersonCriteria> {

	private boolean bulkEditMode;

	public PersonGrid() {
		super(PersonIndexDto.class);
		initColumns();
	}

	public PersonGrid(PersonCriteria criteria) {
		super(PersonIndexDto.class);
		setSizeFull();
		setLazyDataProvider();
		setCriteria(criteria);
		initColumns();

		setBulkEditMode(isInEagerMode());

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
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY()),
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

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(PersonIndexDto.DISTRICT).setHidden(true);
		}
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(
			FacadeProvider.getPersonFacade()::getIndexList,
			FacadeProvider.getPersonFacade()::count,
			UiUtil.permitted(bulkEditMode, UserRight.PERFORM_BULK_OPERATIONS) ? SelectionMode.MULTI : SelectionMode.NONE);
	}

	public void setFixDataProvider(List<PersonIndexDto> list) {

		setDataProvider(query -> list.stream(), query -> list.size());
	}

	protected void setBulkEditMode(boolean bulkEditMode) {
		this.bulkEditMode = bulkEditMode;
		if (UiUtil.permitted(bulkEditMode, UserRight.PERFORM_BULK_OPERATIONS)) {
			setSelectionMode(SelectionMode.MULTI);
		} else {
			setSelectionMode(SelectionMode.NONE);
		}
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		getDataProvider().refreshAll();
	}
}
