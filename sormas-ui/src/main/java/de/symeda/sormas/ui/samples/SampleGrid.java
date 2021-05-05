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
package de.symeda.sormas.ui.samples;

import java.util.Date;
import java.util.stream.Collectors;

import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.DataProviderListener;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.api.user.UserRole;
import de.symeda.sormas.api.utils.SortProperty;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UserProvider;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.FilteredGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class SampleGrid extends FilteredGrid<SampleIndexDto, SampleCriteria> {

	private static final String PATHOGEN_TEST_RESULT = Captions.Sample_pathogenTestResult;
	private static final String DISEASE_SHORT = Captions.columnDiseaseShort;

	private DataProviderListener<SampleIndexDto> dataProviderListener;

	@SuppressWarnings("unchecked")
	public SampleGrid(SampleCriteria criteria) {
		super(SampleIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(SamplesView.class).get(ViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UserProvider.getCurrent().hasUserRight(UserRight.PERFORM_BULK_OPERATIONS_CASE_SAMPLES)) {
			setCriteria(criteria);
			setEagerDataProvider();
		} else {
			setLazyDataProvider();
			setCriteria(criteria);
		}

		addEditColumn(e -> ControllerProvider.getSampleController().navigateToData(e.getUuid()));

		Column<SampleIndexDto, String> diseaseShortColumn =
			addColumn(sample -> DiseaseHelper.toString(sample.getDisease(), sample.getDiseaseDetails()));
		diseaseShortColumn.setId(DISEASE_SHORT);
		diseaseShortColumn.setSortProperty(SampleIndexDto.DISEASE);

		Column<SampleIndexDto, String> pathogenTestResultColumn = addColumn(sample -> {
			if (sample.getPathogenTestResult() != null) {
				return sample.getPathogenTestResult().toString();
			} else if (sample.getSpecimenCondition() == SpecimenCondition.NOT_ADEQUATE) {
				return I18nProperties.getCaption(Captions.sampleSpecimenNotAdequate);
			} else {
				return "";
			}
		});
		pathogenTestResultColumn.setId(PATHOGEN_TEST_RESULT);
		pathogenTestResultColumn.setSortProperty(SampleIndexDto.PATHOGEN_TEST_RESULT);

		setColumns(
			SampleIndexDto.UUID,
			SampleIndexDto.LAB_SAMPLE_ID,
			SampleIndexDto.EPID_NUMBER,
			SampleIndexDto.ASSOCIATED_CASE,
			SampleIndexDto.ASSOCIATED_CONTACT,
			SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT,
			DISEASE_SHORT,
			SampleIndexDto.DISTRICT,
			SampleIndexDto.SHIPPED,
			SampleIndexDto.RECEIVED,
			SampleIndexDto.SHIPMENT_DATE,
			SampleIndexDto.RECEIVED_DATE,
			SampleIndexDto.LAB,
			SampleIndexDto.SAMPLE_MATERIAL,
			SampleIndexDto.SAMPLE_PURPOSE,
			PATHOGEN_TEST_RESULT,
			SampleIndexDto.ADDITIONAL_TESTING_STATUS);

		((Column<SampleIndexDto, Date>) getColumn(SampleIndexDto.SHIPMENT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<SampleIndexDto, Date>) getColumn(SampleIndexDto.RECEIVED_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<SampleIndexDto, Boolean>) getColumn(SampleIndexDto.SHIPPED)).setRenderer(new BooleanRenderer());
		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.RECEIVED)).setRenderer(new BooleanRenderer());
		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.LAB)).setMaximumWidth(200);
		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.ADDITIONAL_TESTING_STATUS)).setSortable(false);

		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.UUID)).setRenderer(new UuidRenderer());
		addItemClickListener(
			new ShowDetailsListener<>(SampleIndexDto.UUID, e -> ControllerProvider.getSampleController().navigateToData(e.getUuid())));

		if (UserProvider.getCurrent().hasUserRole(UserRole.LAB_USER) || UserProvider.getCurrent().hasUserRole(UserRole.EXTERNAL_LAB_USER)) {
			removeColumn(SampleIndexDto.SHIPMENT_DATE);
		} else {
			removeColumn(SampleIndexDto.RECEIVED_DATE);
		}

		if (!UserProvider.getCurrent().hasUserRight(UserRight.CASE_VIEW)) {
			removeColumn(SampleIndexDto.ASSOCIATED_CASE);
		}

		if (!UserProvider.getCurrent().hasUserRight(UserRight.CONTACT_VIEW)) {
			removeColumn(SampleIndexDto.ASSOCIATED_CONTACT);
		}

		if (!UserProvider.getCurrent().hasUserRight(UserRight.EVENT_VIEW)) {
			removeColumn(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT);
		}

		if (!UserProvider.getCurrent().hasUserRight(UserRight.ADDITIONAL_TEST_VIEW)) {
			removeColumn(SampleIndexDto.ADDITIONAL_TESTING_STATUS);
		}

		if (criteria.getSampleAssociationType() == SampleAssociationType.CASE) {
			if (getColumn(SampleIndexDto.ASSOCIATED_CONTACT) != null) {
				removeColumn(SampleIndexDto.ASSOCIATED_CONTACT);
			}
			if (getColumn(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT) != null) {
				removeColumn(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT);
			}
		}

		if (!shouldShowEpidNumber()) {
			removeColumn(SampleIndexDto.EPID_NUMBER);
		}

		if (criteria.getSampleAssociationType() == SampleAssociationType.CONTACT) {
			removeColumnIfExists(SampleIndexDto.EPID_NUMBER);

			if (getColumn(SampleIndexDto.ASSOCIATED_CASE) != null) {
				removeColumn(SampleIndexDto.ASSOCIATED_CASE);
			}
			if (getColumn(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT) != null) {
				removeColumn(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT);
			}
		}
		if (criteria.getSampleAssociationType() == SampleAssociationType.EVENT_PARTICIPANT) {
			removeColumnIfExists(SampleIndexDto.EPID_NUMBER);
			if (getColumn(SampleIndexDto.ASSOCIATED_CASE) != null) {
				removeColumn(SampleIndexDto.ASSOCIATED_CASE);
			}
			if (getColumn(SampleIndexDto.ASSOCIATED_CONTACT) != null) {
				removeColumn(SampleIndexDto.ASSOCIATED_CONTACT);
			}
		}

		for (Column<SampleIndexDto, ?> column : getColumns()) {
			column.setCaption(I18nProperties.getPrefixCaption(SampleIndexDto.I18N_PREFIX, column.getId(), column.getCaption()));

			column.setStyleGenerator(FieldAccessColumnStyleGenerator.getDefault(getBeanType(), column.getId()));

		}
	}

	private boolean shouldShowEpidNumber() {
		ConfigFacade configFacade = FacadeProvider.getConfigFacade();
		return !configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND);
	}

	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (ViewModelProviders.of(SamplesView.class).get(ViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {
		DataProvider<SampleIndexDto, SampleCriteria> dataProvider = DataProvider.fromFilteringCallbacks(
			query -> FacadeProvider.getSampleFacade()
				.getIndexList(
					query.getFilter().orElse(null),
					query.getOffset(),
					query.getLimit(),
					query.getSortOrders()
						.stream()
						.map(sortOrder -> new SortProperty(sortOrder.getSorted(), sortOrder.getDirection() == SortDirection.ASCENDING))
						.collect(Collectors.toList()))
				.stream(),
			query -> (int) FacadeProvider.getSampleFacade().count(query.getFilter().orElse(null)));
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.NONE);
	}

	public void setEagerDataProvider() {
		ListDataProvider<SampleIndexDto> dataProvider =
			DataProvider.fromStream(FacadeProvider.getSampleFacade().getIndexList(getCriteria(), null, null, null).stream());
		setDataProvider(dataProvider);
		setSelectionMode(SelectionMode.MULTI);

		if (dataProviderListener != null) {
			dataProvider.addDataProviderListener(dataProviderListener);
		}
	}

	public void setDataProviderListener(DataProviderListener<SampleIndexDto> dataProviderListener) {
		this.dataProviderListener = dataProviderListener;
	}
}
