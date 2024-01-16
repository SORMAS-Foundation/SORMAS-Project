package de.symeda.sormas.ui.immunization.components.grid;

import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.TextRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.AgeAndBirthDateDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.immunization.ImmunizationCriteria;
import de.symeda.sormas.api.immunization.ImmunizationIndexDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.immunization.ImmunizationPersonView;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class ImmunizationGrid extends FilteredGrid<ImmunizationIndexDto, ImmunizationCriteria> {

	public ImmunizationGrid(ImmunizationCriteria criteria) {
		super(ImmunizationIndexDto.class);
		setSizeFull();
		setLazyDataProvider();
		setCriteria(criteria);

		Column<ImmunizationIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		initColumns();
		addItemClickListener(new ShowDetailsListener<>(ImmunizationIndexDto.PERSON_UUID, e -> {
			if (FacadeProvider.getFeatureConfigurationFacade().isFeatureEnabled(FeatureType.PERSON_MANAGEMENT)) {
				ControllerProvider.getPersonController().navigateToPerson(e.getPersonUuid());
			} else {
				ControllerProvider.getImmunizationController().navigateToView(ImmunizationPersonView.VIEW_NAME, e.getUuid());
			}
		}));
		addItemClickListener(
			new ShowDetailsListener<>(
				ImmunizationIndexDto.UUID,
				e -> ControllerProvider.getImmunizationController().navigateToImmunization(e.getUuid())));
	}

	public void reload() {
		getDataProvider().refreshAll();
	}

	private void initColumns() {
		setColumns(
			ImmunizationIndexDto.UUID,
			ImmunizationIndexDto.PERSON_UUID,
			ImmunizationIndexDto.PERSON_FIRST_NAME,
			ImmunizationIndexDto.PERSON_LAST_NAME,
			ImmunizationIndexDto.DISEASE,
			ImmunizationIndexDto.AGE_AND_BIRTH_DATE,
			ImmunizationIndexDto.SEX,
			ImmunizationIndexDto.DISTRICT,
			ImmunizationIndexDto.MEANS_OF_IMMUNIZATION,
			ImmunizationIndexDto.MANAGEMENT_STATUS,
			ImmunizationIndexDto.IMMUNIZATION_STATUS,
			ImmunizationIndexDto.START_DATE,
			ImmunizationIndexDto.END_DATE,
			ImmunizationIndexDto.LAST_VACCINE_TYPE,
			ImmunizationIndexDto.RECOVERY_DATE,
			DELETE_REASON_COLUMN);

		((Column<ImmunizationIndexDto, String>) getColumn(ImmunizationIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<ImmunizationIndexDto, String>) getColumn(ImmunizationIndexDto.PERSON_UUID)).setRenderer(new UuidRenderer());

		((Column<ImmunizationIndexDto, AgeAndBirthDateDto>) getColumn(ImmunizationIndexDto.AGE_AND_BIRTH_DATE)).setRenderer(
			value -> value == null
				? ""
				: PersonHelper.getAgeAndBirthdateString(
					value.getAge(),
					value.getAgeType(),
					value.getDateOfBirthDD(),
					value.getDateOfBirthMM(),
					value.getDateOfBirthYYYY()),
			new TextRenderer());

		((Column<ImmunizationIndexDto, Date>) getColumn(ImmunizationIndexDto.START_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<ImmunizationIndexDto, Date>) getColumn(ImmunizationIndexDto.END_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<ImmunizationIndexDto, Date>) getColumn(ImmunizationIndexDto.RECOVERY_DATE))
			.setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		for (Column<ImmunizationIndexDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties
					.findPrefixCaptionWithDefault(column.getId(), column.getCaption(), ImmunizationIndexDto.I18N_PREFIX, PersonDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(ImmunizationIndexDto.DISTRICT).setHidden(true);
		}
	}

	private void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getImmunizationFacade()::getIndexList, FacadeProvider.getImmunizationFacade()::count);
	}
}
