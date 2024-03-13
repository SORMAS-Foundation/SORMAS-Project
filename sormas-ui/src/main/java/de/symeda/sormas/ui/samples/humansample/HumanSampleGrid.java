/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package de.symeda.sormas.ui.samples.humansample;

import java.util.Date;

import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.ConfigFacade;
import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.DiseaseHelper;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.sample.PathogenTestType;
import de.symeda.sormas.api.sample.SampleAssociationType;
import de.symeda.sormas.api.sample.SampleCriteria;
import de.symeda.sormas.api.sample.SampleIndexDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.user.UserRight;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.UiUtil;
import de.symeda.sormas.ui.ViewModelProviders;
import de.symeda.sormas.ui.samples.SamplesView;
import de.symeda.sormas.ui.samples.SamplesViewConfiguration;
import de.symeda.sormas.ui.utils.BooleanRenderer;
import de.symeda.sormas.ui.utils.DateFormatHelper;
import de.symeda.sormas.ui.utils.FieldAccessColumnStyleGenerator;
import de.symeda.sormas.ui.utils.ReloadableGrid;
import de.symeda.sormas.ui.utils.ShowDetailsListener;
import de.symeda.sormas.ui.utils.UuidRenderer;
import de.symeda.sormas.ui.utils.ViewConfiguration;

@SuppressWarnings("serial")
public class HumanSampleGrid extends ReloadableGrid<SampleIndexDto, SampleCriteria> {

	private static final String PATHOGEN_TEST_RESULT = Captions.Sample_pathogenTestResult;
	private static final String DISEASE_SHORT = Captions.columnDiseaseShort;
	private static final String LAST_PATHOGEN_TEST = Captions.columnLastPathogenTest;

	@SuppressWarnings("unchecked")
	public HumanSampleGrid(SampleCriteria criteria) {
		super(SampleIndexDto.class);
		setSizeFull();

		ViewConfiguration viewConfiguration = ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class);
		setInEagerMode(viewConfiguration.isInEagerMode());

		if (isInEagerMode() && UiUtil.permitted(UserRight.PERFORM_BULK_OPERATIONS)) {
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

		Column<SampleIndexDto, String> lastPathogenTestColumn = addColumn(sample -> {
			PathogenTestType type = sample.getTypeOfLastTest();
			Float cqValue = sample.getLastTestCqValue();
			String text = null;
			if (type != null) {
				text = type.toString();
				if (cqValue != null) {
					text += " (" + cqValue + ")";
				}
			}
			return text;
		});
		lastPathogenTestColumn.setId(LAST_PATHOGEN_TEST);
		lastPathogenTestColumn.setSortable(false);

		Column<SampleIndexDto, String> deleteColumn = addColumn(entry -> {
			if (entry.getDeletionReason() != null) {
				return entry.getDeletionReason() + (entry.getOtherDeletionReason() != null ? ": " + entry.getOtherDeletionReason() : "");
			} else {
				return "-";
			}
		});
		deleteColumn.setId(DELETE_REASON_COLUMN);
		deleteColumn.setSortable(false);
		deleteColumn.setCaption(I18nProperties.getCaption(Captions.deletionReason));

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
			SampleIndexDto.ADDITIONAL_TESTING_STATUS,
			LAST_PATHOGEN_TEST,
			SampleIndexDto.PATHOGEN_TEST_COUNT,
			DELETE_REASON_COLUMN);

		((Column<SampleIndexDto, Date>) getColumn(SampleIndexDto.SHIPMENT_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<SampleIndexDto, Date>) getColumn(SampleIndexDto.RECEIVED_DATE)).setRenderer(new DateRenderer(DateFormatHelper.getDateFormat()));
		((Column<SampleIndexDto, Boolean>) getColumn(SampleIndexDto.SHIPPED)).setRenderer(new BooleanRenderer());
		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.RECEIVED)).setRenderer(new BooleanRenderer());
		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.LAB)).setMaximumWidth(200);
		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.ADDITIONAL_TESTING_STATUS)).setSortable(false);
		((Column<SampleIndexDto, Integer>) getColumn(SampleIndexDto.PATHOGEN_TEST_COUNT)).setSortable(false);

		((Column<SampleIndexDto, String>) getColumn(SampleIndexDto.UUID)).setRenderer(new UuidRenderer());
		addItemClickListener(
			new ShowDetailsListener<>(SampleIndexDto.UUID, e -> ControllerProvider.getSampleController().navigateToData(e.getUuid())));

		if (UiUtil.hasLaboratoryOrExternalLaboratoryJurisdictionLevel()) {
			removeColumn(SampleIndexDto.SHIPMENT_DATE);
		} else {
			removeColumn(SampleIndexDto.RECEIVED_DATE);
		}

		if (!UiUtil.permitted(UserRight.CASE_VIEW)) {
			removeColumn(SampleIndexDto.ASSOCIATED_CASE);
		}

		if (!UiUtil.permitted(UserRight.CONTACT_VIEW)) {
			removeColumn(SampleIndexDto.ASSOCIATED_CONTACT);
		}

		if (!UiUtil.permitted(UserRight.EVENT_VIEW)) {
			removeColumn(SampleIndexDto.ASSOCIATED_EVENT_PARTICIPANT);
		}

		if (!UiUtil.permitted(UserRight.ADDITIONAL_TEST_VIEW) || UiUtil.disabled(FeatureType.ADDITIONAL_TESTS)) {
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

		if (UiUtil.enabled(FeatureType.HIDE_JURISDICTION_FIELDS)) {
			getColumn(SampleIndexDto.DISTRICT).setHidden(true);
		}
	}

	private boolean shouldShowEpidNumber() {
		ConfigFacade configFacade = FacadeProvider.getConfigFacade();
		return !configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_GERMANY)
			&& !configFacade.isConfiguredCountry(CountryHelper.COUNTRY_CODE_SWITZERLAND);
	}

	@Override
	public void reload() {
		if (getSelectionModel().isUserSelectionAllowed()) {
			deselectAll();
		}

		if (ViewModelProviders.of(SamplesView.class).get(SamplesViewConfiguration.class).isInEagerMode()) {
			setEagerDataProvider();
		}

		getDataProvider().refreshAll();
	}

	public void setLazyDataProvider() {

		setLazyDataProvider(FacadeProvider.getSampleFacade()::getIndexList, FacadeProvider.getSampleFacade()::count);
	}

	public void setEagerDataProvider() {

		setEagerDataProvider(FacadeProvider.getSampleFacade()::getIndexList);
	}
}
