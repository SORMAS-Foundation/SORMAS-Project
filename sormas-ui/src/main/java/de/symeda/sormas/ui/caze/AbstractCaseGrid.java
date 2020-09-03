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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.caze.CaseIndexDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.CssStyles;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public abstract class AbstractCaseGrid<IndexDto extends CaseIndexDto> extends FilteredGrid<IndexDto, CaseCriteria> {

	public static final String DISEASE_SHORT = Captions.columnDiseaseShort;
	public static final String COLUMN_COMPLETENESS = "completenessValue";

	public AbstractCaseGrid(Class<IndexDto> beanType, CaseCriteria criteria) {

		super(beanType);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CasesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		initColumns();

		addItemClickListener(new ShowDetailsListener<>(CaseIndexDto.UUID, e -> ControllerProvider.getCaseController().navigateToCase(e.getUuid())));
	}

	@SuppressWarnings("unchecked")
	protected void initColumns() {

		Column<IndexDto, String> diseaseShortColumn = addColumn(caze -> DiseaseHelper.toString(caze.getDisease(), caze.getDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(CaseIndexDto.DISEASE);

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

		if (FacadeProvider.getConfigFacade().isGermanServer()) {
			getColumn(CaseIndexDto.EPID_NUMBER).setHidden(true);
		} else {
			getColumn(CaseIndexDto.EXTERNAL_ID).setHidden(true);
		}

		getColumn(COLUMN_COMPLETENESS).setCaption(I18nProperties.getPrefixCaption(CaseIndexDto.I18N_PREFIX, CaseIndexDto.COMPLETENESS));
		getColumn(COLUMN_COMPLETENESS).setSortable(false);

		Language userLanguage = I18nProperties.getUserLanguage();
		((Column<CaseIndexDto, String>) getColumn(CaseIndexDto.UUID)).setRenderer(new UuidRenderer());
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.REPORT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.QUARANTINE_TO))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		if (UserProvider.getCurrent().hasUserRight(UserRight.CASE_IMPORT)) {
			((Column<CaseIndexDto, Date>) getColumn(CaseIndexDto.CREATION_DATE))
				.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		} else {
			removeColumn(CaseIndexDto.CREATION_DATE);
		}

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaptionWithDefault(
					column.getId(),
					column.getCaption(),
					CaseIndexDto.I18N_PREFIX,
					PersonDto.I18N_PREFIX,
					LocationDto.I18N_PREFIX));
		}
	}

	protected Stream<String> getGridColumns() {

		return Stream.of(
			Stream.of(
				CaseIndexDto.UUID,
				CaseIndexDto.EPID_NUMBER,
				CaseIndexDto.EXTERNAL_ID,
				DISEASE_SHORT,
				CaseIndexDto.CASE_CLASSIFICATION,
				CaseIndexDto.OUTCOME,
				CaseIndexDto.INVESTIGATION_STATUS),
			getPersonColumns(),
			Stream.of(
				CaseIndexDto.DISTRICT_NAME,
				CaseIndexDto.HEALTH_FACILITY_NAME,
				CaseIndexDto.POINT_OF_ENTRY_NAME,
				CaseIndexDto.REPORT_DATE,
				CaseIndexDto.QUARANTINE_TO,
				CaseIndexDto.CREATION_DATE,
				COLUMN_COMPLETENESS))
			.flatMap(s -> s);
	}

	protected Stream<String> getPersonColumns() {
		return Stream.of(CaseIndexDto.PERSON_FIRST_NAME, CaseIndexDto.PERSON_LAST_NAME);
	}

	public void reload() {

		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (getCriteria().getOutcome() == null) {
			this.getColumn(CaseIndexDto.OUTCOME).setHidden(false);
		} else if (this.getColumn(CaseIndexDto.OUTCOME) != null) {
			this.getColumn(CaseIndexDto.OUTCOME).setHidden(true);
		}

		if (UserRole.isPortHealthUser(UserProvider.getCurrent().getUserRoles()) && getColumn(CaseIndexDto.HEALTH_FACILITY_NAME) != null) {
			removeColumn(CaseIndexDto.HEALTH_FACILITY_NAME);
		} else {
			if (getCriteria().getCaseOrigin() == CaseOrigin.IN_COUNTRY && getColumn(CaseIndexDto.POINT_OF_ENTRY_NAME) != null) {
				removeColumn(CaseIndexDto.POINT_OF_ENTRY_NAME);
			} else if (getCriteria().getCaseOrigin() == CaseOrigin.POINT_OF_ENTRY && getColumn(CaseIndexDto.HEALTH_FACILITY_NAME) != null) {
				removeColumn(CaseIndexDto.HEALTH_FACILITY_NAME);
			}
		}

		ViewConfiguration viewConfiguration = ViewModelProviders.of(CasesView.class).get(ViewConfiguration.class);
		if (viewConfiguration.isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		DataProvider<IndexDto, CaseCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> getGridData(
				query.getFilter().orElse(null),
				query.getOffset(),
				query.getLimit(),
				query.getSortOrders()
					.stream()
					.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
					.collect(Collectors.toList())).stream(),
			query -> (int) FacadeProvider.getCaseFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {

		ListDataProvider<IndexDto> dataProvider = DataProvider.fromStream(getGridData(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);
	}

	protected abstract List<IndexDto> getGridData(CaseCriteria caseCriteria, Integer first, Integer max, List<SortProperty> sortProperties);
}
