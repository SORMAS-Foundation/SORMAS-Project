/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.sormastosormas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;
import com.vaadin.ui.renderers.DateRenderer;

import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.Language;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.infrastructure.InfrastructureHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityHelper;
import de.symeda.sormas.api.infrastructure.facility.FacilityReferenceDto;
import de.symeda.sormas.api.infrastructure.pointofentry.PointOfEntryReferenceDto;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasCasePreview;
import de.symeda.sormas.api.sormastosormas.share.incoming.SormasToSormasPersonPreview;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.ui.ControllerProvider;
import de.symeda.sormas.ui.utils.CaptionRenderer;
import de.symeda.sormas.ui.utils.UuidRenderer;

public class CasePreviewGrid extends BasePreviewGrid<SormasToSormasCasePreview> {

	private static final long serialVersionUID = -7325193086495081297L;

	public CasePreviewGrid(List<SormasToSormasCasePreview> cases, boolean isPendingRequest) {
		super(
			SormasToSormasCasePreview.class,
			Captions.CaseData,
			uuid -> FacadeProvider.getCaseFacade().exists(uuid),
			uuid -> ControllerProvider.getCaseController().navigateToCase(uuid),
			isPendingRequest);

		setItems(cases);
	}

	@Override
	protected void buildGrid() {

		Language userLanguage = I18nProperties.getUserLanguage();

		removeColumn(SormasToSormasCasePreview.DISEASE);
		addComponentColumn(casePreview -> {
			String diseaseText = casePreview.getDisease().toString();
			if (!StringUtils.isEmpty(casePreview.getDiseaseDetails())) {
				diseaseText += " - " + casePreview.getDiseaseDetails();
			}

			if (casePreview.getDiseaseVariant() != null) {
				diseaseText += diseaseText + " - " + casePreview.getDiseaseVariant().getCaption();
			}

			return new Label(diseaseText);
		}).setId(SormasToSormasCasePreview.DISEASE);

		removeColumn(SormasToSormasCasePreview.HEALTH_FACILITY);
		addComponentColumn(casePreview -> {
			FacilityReferenceDto healthFacility = casePreview.getHealthFacility();
			if (healthFacility == null) {
				return null;
			}
			return new Label(
				FacilityHelper.buildFacilityString(healthFacility.getUuid(), healthFacility.getCaption(), casePreview.getHealthFacilityDetails()));
		}).setId(SormasToSormasCasePreview.HEALTH_FACILITY);

		removeColumn(SormasToSormasCasePreview.POINT_OF_ENTRY);
		addComponentColumn(casePreview -> {
			PointOfEntryReferenceDto pointOfEntry = casePreview.getPointOfEntry();
			if (pointOfEntry == null) {
				return null;
			}
			return new Label(
				InfrastructureHelper
					.buildPointOfEntryString(pointOfEntry.getUuid(), pointOfEntry.getCaption(), casePreview.getPointOfEntryDetails()));
		}).setId(SormasToSormasCasePreview.POINT_OF_ENTRY);

		List<String> columnConfig = new ArrayList<>();
		columnConfig.addAll(
			Arrays.asList(
				SormasToSormasCasePreview.UUID,
				SormasToSormasCasePreview.REPORT_DATE,
				SormasToSormasCasePreview.DISEASE,
				SormasToSormasCasePreview.CASE_CLASSIFICATION,
				SormasToSormasCasePreview.INVESTIGATION_STATUS,
				SormasToSormasCasePreview.OUTCOME));
		columnConfig.addAll(createPersonColumns(SormasToSormasCasePreview::getPerson));
		columnConfig.addAll(
			Arrays.asList(
				SormasToSormasCasePreview.REGION,
				SormasToSormasCasePreview.DISTRICT,
				SormasToSormasCasePreview.COMMUNITY,
				SormasToSormasCasePreview.HEALTH_FACILITY,
				SormasToSormasCasePreview.POINT_OF_ENTRY,
				SormasToSormasCasePreview.ONSET_DATE));
		setColumns(columnConfig);

		((Column<SormasToSormasCasePreview, String>) getColumn(SormasToSormasCasePreview.UUID)).setRenderer(new UuidRenderer());
		((Column<SormasToSormasCasePreview, Date>) getColumn(SormasToSormasCasePreview.REPORT_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));
		((Column<SormasToSormasCasePreview, Date>) getColumn(SormasToSormasCasePreview.ONSET_DATE))
			.setRenderer(new DateRenderer(DateHelper.getLocalDateTimeFormat(userLanguage)));

		getColumn(SormasToSormasCasePreview.REGION).setRenderer(new CaptionRenderer());
		getColumn(SormasToSormasCasePreview.DISTRICT).setRenderer(new CaptionRenderer());
		getColumn(SormasToSormasCasePreview.COMMUNITY).setRenderer(new CaptionRenderer());

		for (Column<?, ?> column : getColumns()) {
			column.setCaption(
				I18nProperties.findPrefixCaption(
					column.getId(),
					SormasToSormasCasePreview.I18N_PREFIX,
					SormasToSormasPersonPreview.I18N_PREFIX,
					SymptomsDto.I18N_PREFIX));
		}
	}
}
