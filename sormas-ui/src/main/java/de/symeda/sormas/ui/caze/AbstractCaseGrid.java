/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.caze;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.contact.ContactIndexDto;
import de.symeda.sormas.api.contact.FollowUpStatus;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public abstract class AbstractCaseGrid<IndexDto extends CaseIndexDto> extends FilteredGrid<IndexDto, CaseCriteria> {

	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;
	public static final String NUMBER_OF_VISITS = Captions.CaseData_numberOfVisits;
	public static final String COLUMN_COMPLETENESS = "completenessValue";

	private final boolean caseFollowUpEnabled;
	private final boolean externalSurveillanceToolShareEnabled;

	public AbstractCaseGrid(Class<IndexDto> beanType, CaseCriteria criteria) {

		super(beanType);
		setSizeFull();
		caseFollowUpEnabled = UiUtil.enabled(FeatureType.CASE_FOLLOWUP);
		externalSurveillanceToolShareEnabled = FacadeProvider.getExternalSurveillanceToolFacade().isFeatureEnabled();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CasesView.class).get(CasesViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		initColumns();

		addItemClickListener(new ShowDetailsListener<>(CaseIndexDto.PERSON_UUID, e -> {
			if (UiUtil.enabled(FeatureType.PERSON_MANAGEMENT)) {
				ControllerProvider.getPersonController().navigateToPerson(e.getPersonUuid());
			} else {
				ControllerProvider.getCaseController().navigateToView(CasePersonView.VIEW_NAME, e.getUuid(), null);
			}
		}));
		addItemClickListener(new ShowDetailsListener<>(CaseIndexDto.UUID, e -> ControllerProvider.getCaseController().navigateToCase(e.getUuid())));
	}

	@SuppressWarnings("unchecked")
	protected void initColumns() {

		Column<IndexDto, String> diseaseShortColumn = addColumn(caze -> DiseaseHelper.toString(caze.getDisease(), caze.getDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(CaseIndexDto.DISEASE);

		Column<IndexDto, String> visitsColumn = addColumn(entry -> {
			Integer numberOfVisits = entry.getVisitCount();
			Integer numberOfMissedVisits = entry.getMissedVisitsCount();
			if (numberOfVisits != null && numberOfMissedVisits != null) {
				return String.format(I18nProperties.getCaption(Captions.formatNumberOfVisitsFormat), numberOfVisits, numberOfMissedVisits);
			} else {
				return "-";
			}

		});
		visitsColumn.setId(NUMBER_OF_VISITS);
		visitsColumn.setSortable(false);

		Column<IndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

		addComponentColumn(indexDto -> {
			Label label =
				new Label(indexDto.getCompleteness() != null ? new DecimalFormat("#").format(indexDto.getCompleteness() * 100) + " %" : "-");
			if (indexDto.getCompleteness() != null) {
				if (indexDto.getCompleteness() < 0.25f) {
					CssStyles.style(label, CssStyles.LABEL_CRITICAL);
				} else if (indexDto.getCompleteness() < 0.5f) {
					CssStyles.style(label, CssStyles.LABEL_IMPORTANT);
				} else if (indexDto.getCompleteness() < 0.75f) {
					CssStyles.style(label, CssStyles.LABEL_RELEVANT);
				} else {
					CssStyles.style(label, CssStyles.LABEL_POSITIVE);
				}
			}
			return label;
		}).setId(COLUMN_COMPLETENESS);

		setColumns(getGridColumns().toArray(String[]::new));

		if (FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			|| FacadeProvider.getConfigFacade().isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND)) {
			getColumn(CaseIndexDto.EPID_NUMBER).setHidden(true);
		} else {
			getColumn(CaseIndexDto.EXTERNAL_ID).setHidden(true);
			getColumn(CaseIndexDto.EXTERNAL_TOKEN).setHidden(true);
		}

		getColumn(COLUMN_COMPLETENESS).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.COMPLETENESS));
		getColumn(COLUMN_COMPLETENESS).setSortable(false);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<CaseIndexDto, String>) getColumn(CaseIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<CaseIndexDto, String>) getColumn(CaseIndexDto.PERSON_UUID)).setRenderer(new UuidRenderer());
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));

		if (externalSurveillanceToolShareEnabled) {
			Column<CaseIndexDto, Date> shareDateColumn = ((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.SURVEILLANCE_TOOL_LAST_SHARE_DATE));
			shareDateColumn.setSortable(false);
			shareDateColumn.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

			getColumn(CaseIndexDto.SURVEILLANCE_TOOL_SHARE_COUNT).setSortable(false);
			getColumn(CaseIndexDto.SURVEILLANCE_TOOL_STATUS).setSortable(false);
			getColumn(CaseIndexDto.SURVEILLANCE_TOOL_LAST_SHARE_DATE).setSortable(false);
		}

		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.QUARANTINE_TO))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		if (caseFollowUpEnabled) {
			((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.FOLLOW_UP_UNTIL))
				.setRenderer(new DateRenderer(DateHelper.getLocalDateFormat(userLanguage)));
		}

		if (UiUtil.permitted(UserRight.CASE_IMPORT)) {
			((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE))
				.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		} else {
			removeColumn(CaseIndexDto.CREATION_DATE);
		}

		if (!UiUtil.permitted(UserRight.CASE_DELETE)) {
			removeColumn(DELETE_REASON_COLUMN);
		}

		for (Column<IndexDto, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaptionWithDefault(
					column.getId(),
					column.getCaption(),
					CaseIndexDto.I18N_PREFIX,
					PersonDto.I18N_PREFIX,
					LocationDto.I18N_PREFIX));
			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));
		}

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(CaseIndexDto.RESPONSIBLE_DISTRICT_NAME).setHidden(true);
		}
	}

	protected Stream<String> getGridColumns() {

		return Stream
			.of(
				Stream.of(
					CaseIndexDto.UUID,
					CaseIndexDto.EPID_NUMBER,
					CaseIndexDto.EXTERNAL_ID,
					CaseIndexDto.EXTERNAL_TOKEN,
					CaseIndexDto.INTERNAL_TOKEN,
					DISEASE_SHORT,
					CaseIndexDto.DISEASE_VARIANT,
					CaseIndexDto.CASE_CLASSIFICATION,
					CaseIndexDto.OUTCOME),
				getReinfectionColumn(),
				Stream.of(CaseIndexDto.INVESTIGATION_STATUS),
				getPersonColumns(),
				getEventColumns(),
				getSymptomsColumns(),
				getSampleColumns(),
				getJurisdictionColumns(),
				Stream.of(CaseIndexDto.REPORT_DATE),
				externalSurveillanceToolShareEnabled
					? Stream.of(
						CaseIndexDto.SURVEILLANCE_TOOL_LAST_SHARE_DATE,
						CaseIndexDto.SURVEILLANCE_TOOL_STATUS,
						CaseIndexDto.SURVEILLANCE_TOOL_SHARE_COUNT)
					: Stream.<String> empty(),
				Stream.of(CaseIndexDto.QUARANTINE_TO, CaseIndexDto.CREATION_DATE),
				getFollowUpColumns(),
				Stream.of(CaseIndexDto.VACCINATION_STATUS),
				Stream.of(COLUMN_COMPLETENESS),
				Stream.of(DELETE_REASON_COLUMN))
			.flatMap(Function.identity());
	}

	protected Stream<String> getJurisdictionColumns() {
		return Stream.of(CaseIndexDto.RESPONSIBLE_DISTRICT_NAME, CaseIndexDto.HEALTH_FACILITY_NAME, CaseIndexDto.POINT_OF_ENTRY_NAME);
	}

	protected Stream<String> getReinfectionColumn() {
		return Stream.empty();
	}

	protected Stream<String> getPersonColumns() {
		return Stream.of(CaseIndexDto.PERSON_UUID, CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME);
	}

	protected Stream<String> getEventColumns() {
		return Stream.empty();
	}

	protected Stream<String> getSymptomsColumns() {
		return Stream.empty();
	}

	protected Stream<String> getSampleColumns() {
		return Stream.empty();
	}

	private Stream<String> getFollowUpColumns() {
		return caseFollowUpEnabled
			? Stream.of(CaseIndexDto.FOLLOW_UP_STATUS, CaseIndexDto.FOLLOW_UP_UNTIL, ContactIndexDto.SYMPTOM_JOURNAL_STATUS, NUMBER_OF_VISITS)
			: Stream.empty();
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (caseFollowUpEnabled) {
			boolean hidden = getCriteria().getFollowUpStatus() == FollowUpStatus.NO_FOLLOW_UP;
			this.getColumn(NUMBER_OF_VISITS).setHidden(hidden);
		}

		if (UserProvider.getCurrent().isPortHealthUser() && getColumn(CaseIndexDto.HEALTH_FACILITY_NAME) != null) {
			removeColumn(CaseIndexDto.HEALTH_FACILITY_NAME);
		} else {
			if (getCriteria().getCaseOrigin() == CaseOrigin.IN_COUNTRY && getColumn(CaseIndexDto.POINT_OF_ENTRY_NAME) != null) {
				removeColumn(CaseIndexDto.POINT_OF_ENTRY_NAME);
			} else if (getCriteria().getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY && getColumn(CaseIndexDto.HEALTH_FACILITY_NAME) != null) {
				removeColumn(CaseIndexDto.HEALTH_FACILITY_NAME);
			}
		}

		if (ViewModelProviders.of(CasesView.class).get(CasesViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(this::getGridData, FacadeProvider.getCaseFacade()::count);
	}

	public void setEagerDataProvider() {

		setEagerDataProvider(this::getGridData);
	}

	protected abstract List<IndexDto> getGridData(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties);
}
