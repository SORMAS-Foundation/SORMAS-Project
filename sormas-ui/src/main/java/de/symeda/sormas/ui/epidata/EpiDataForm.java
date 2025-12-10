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
package de.symeda.sormas.ui.epidata;

import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_3;
import static de.symeda.sormas.ui.utils.CssStyles.VSPACE_TOP_3;
import static de.symeda.sormas.ui.utils.LayoutUtil.divsCss;
import static de.symeda.sormas.ui.utils.LayoutUtil.fluidRowLocs;
import static de.symeda.sormas.ui.utils.LayoutUtil.h3;
import static de.symeda.sormas.ui.utils.LayoutUtil.loc;
import static de.symeda.sormas.ui.utils.LayoutUtil.locCss;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

import de.symeda.sormas.api.CountryHelper;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.contact.ContactReferenceDto;
import de.symeda.sormas.api.epidata.ClusterType;
import de.symeda.sormas.api.epidata.EpiDataDto;
import de.symeda.sormas.api.exposure.InfectionSource;
import de.symeda.sormas.api.exposure.ModeOfTransmission;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.utils.YesNoUnknown;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.ui.ActivityAsCase.ActivityAsCaseField;
import de.symeda.sormas.ui.exposure.ExposuresField;
import de.symeda.sormas.ui.utils.AbstractEditForm;
import de.symeda.sormas.ui.utils.FieldAccessHelper;
import de.symeda.sormas.ui.utils.FieldHelper;
import de.symeda.sormas.ui.utils.NullableOptionGroup;
import de.symeda.sormas.ui.utils.components.MultilineLabel;

public class EpiDataForm extends AbstractEditForm<EpiDataDto> {

	private static final long serialVersionUID = 1L;

	private static final String LOC_EXPOSURE_INVESTIGATION_HEADING = "locExposureInvestigationHeading";
	private static final String LOC_CONCLUSION_HEADING = "locConclusionHeading";
	private static final String LOC_CLUSTER_TYPE_HEADING = "locClusterTypeHeading";
	private static final String LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING = "locActivityAsCaseInvestigationHeading";
	private static final String LOC_SOURCE_CASE_CONTACTS_HEADING = "locSourceCaseContactsHeading";
	private static final String LOC_EPI_DATA_FIELDS_HINT = "locEpiDataFieldsHint";

	//@formatter:off
	private static final String MAIN_HTML_LAYOUT = 
			loc(LOC_EXPOSURE_INVESTIGATION_HEADING) +
			loc(EpiDataDto.EXPOSURE_DETAILS_KNOWN) +
			loc(EpiDataDto.EXPOSURES) +
			loc(LOC_CONCLUSION_HEADING) +
			fluidRowLocs(6,EpiDataDto.CASE_IMPORTED_STATUS,6,"") +
			fluidRowLocs(6, EpiDataDto.IMPORTED_CASE, 6, EpiDataDto.COUNTRY)+
			fluidRowLocs(EpiDataDto.MODE_OF_TRANSMISSION, EpiDataDto.MODE_OF_TRANSMISSION_TYPE) +
			fluidRowLocs(EpiDataDto.INFECTION_SOURCE, EpiDataDto.INFECTION_SOURCE_TEXT) +
			loc(LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING) +
			loc(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN)+
			loc(EpiDataDto.ACTIVITIES_AS_CASE) +
			loc(LOC_CLUSTER_TYPE_HEADING)+
			fluidRowLocs(3, EpiDataDto.CLUSTER_RELATED,5,EpiDataDto.CLUSTER_TYPE,4,EpiDataDto.CLUSTER_TYPE_TEXT) +
			locCss(VSPACE_TOP_3, LOC_EPI_DATA_FIELDS_HINT) +
			loc(EpiDataDto.HIGH_TRANSMISSION_RISK_AREA) +
			loc(EpiDataDto.LARGE_OUTBREAKS_AREA) + 
			loc(EpiDataDto.AREA_INFECTED_ANIMALS);
	
	private static final String SOURCE_CONTACTS_HTML_LAYOUT =
			locCss(VSPACE_TOP_3, LOC_SOURCE_CASE_CONTACTS_HEADING) +
			loc(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN);
	//@formatter:on

	private final Disease disease;
	private final Class<? extends EntityDto> parentClass;
	private final Consumer<Boolean> sourceContactsToggleCallback;
	private final boolean isPseudonymized;

	public EpiDataForm(
		Disease disease,
		Class<? extends EntityDto> parentClass,
		boolean isPseudonymized,
		boolean inJurisdiction,
		Consumer<Boolean> sourceContactsToggleCallback,
		boolean isEditAllowed) {
		super(
			EpiDataDto.class,
			EpiDataDto.I18N_PREFIX,
			false,
			FieldVisibilityCheckers.withDisease(disease).andWithCountry(FacadeProvider.getConfigFacade().getCountryLocale()),
			FieldAccessHelper.getFieldAccessCheckers(inJurisdiction, isPseudonymized),
			isEditAllowed);
		this.disease = disease;
		this.parentClass = parentClass;
		this.sourceContactsToggleCallback = sourceContactsToggleCallback;
		this.isPseudonymized = isPseudonymized;
		addFields();
	}

	@Override
	protected void addFields() {
		if (disease == null) {
			return;
		}

		addHeadingsAndInfoTexts();

		NullableOptionGroup ogExposureDetailsKnown = addField(EpiDataDto.EXPOSURE_DETAILS_KNOWN, NullableOptionGroup.class);
		ExposuresField exposuresField = addField(
			EpiDataDto.EXPOSURES,
			new ExposuresField(
				disease,
				FieldVisibilityCheckers.withDisease(disease)
					.add(new CountryFieldVisibilityChecker(FacadeProvider.getConfigFacade().getCountryLocale())),
				UiFieldAccessCheckers.getDefault(false, FacadeProvider.getConfigFacade().getCountryLocale()),
				true));

		exposuresField.setEpiDataParentClass(parentClass);
		exposuresField.setWidthFull();
		exposuresField.setPseudonymized(isPseudonymized);

		if (parentClass == CaseDataDto.class) {
			addActivityAsCaseFields();
		}

		addField(EpiDataDto.HIGH_TRANSMISSION_RISK_AREA, NullableOptionGroup.class);
		addField(EpiDataDto.LARGE_OUTBREAKS_AREA, NullableOptionGroup.class);
		addField(EpiDataDto.AREA_INFECTED_ANIMALS, NullableOptionGroup.class);
		NullableOptionGroup ogContactWithSourceCaseKnown = addField(EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN, NullableOptionGroup.class);

		if (sourceContactsToggleCallback != null) {
			ogContactWithSourceCaseKnown.addValueChangeListener(e -> {
				YesNoUnknown sourceContactsKnown = (YesNoUnknown) FieldHelper.getNullableSourceFieldValue((Field) e.getProperty());
				sourceContactsToggleCallback.accept(YesNoUnknown.YES == sourceContactsKnown);
			});
		}

		addField(EpiDataDto.CASE_IMPORTED_STATUS);
		addField(EpiDataDto.CLUSTER_TYPE);
		addField(EpiDataDto.CLUSTER_RELATED);

		addField(EpiDataDto.MODE_OF_TRANSMISSION);
		addField(EpiDataDto.MODE_OF_TRANSMISSION_TYPE);
		addField(EpiDataDto.INFECTION_SOURCE);
		addField(EpiDataDto.INFECTION_SOURCE_TEXT);
		addField(EpiDataDto.IMPORTED_CASE, NullableOptionGroup.class);
		List<CountryReferenceDto> countries = FacadeProvider.getCountryFacade().getAllActiveAsReference();
		ComboBox country = addInfrastructureField(EpiDataDto.COUNTRY);
		country.addItems(countries);

		TextField clusterTypeTF = addField(EpiDataDto.CLUSTER_TYPE_TEXT);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), EpiDataDto.CLUSTER_TYPE, EpiDataDto.CLUSTER_RELATED, Collections.singletonList(Boolean.TRUE), true);
		FieldHelper.setVisibleWhen(getField(EpiDataDto.CLUSTER_TYPE), Arrays.asList(clusterTypeTF), Arrays.asList(ClusterType.OTHER), true);
		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.EXPOSURES,
			EpiDataDto.EXPOSURE_DETAILS_KNOWN,
			Collections.singletonList(YesNoUnknown.YES),
			true);
		FieldHelper
			.setVisibleWhen(getFieldGroup(), EpiDataDto.MODE_OF_TRANSMISSION_TYPE, EpiDataDto.MODE_OF_TRANSMISSION, ModeOfTransmission.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.INFECTION_SOURCE_TEXT, EpiDataDto.INFECTION_SOURCE, InfectionSource.OTHER, true);
		FieldHelper.setVisibleWhen(getFieldGroup(), EpiDataDto.COUNTRY, EpiDataDto.IMPORTED_CASE, YesNoUnknown.YES, true);
		initializeVisibilitiesAndAllowedVisibilities();
		initializeAccessAndAllowedAccesses();

		exposuresField.addValueChangeListener(e -> {
			ogExposureDetailsKnown.setEnabled(CollectionUtils.isEmpty(exposuresField.getValue()));
		});
	}

	private void addActivityAsCaseFields() {

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingActivityAsCase))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoActivityAsCaseInvestigation)),
				ContentMode.HTML),
			LOC_ACTIVITY_AS_CASE_INVESTIGATION_HEADING);

		NullableOptionGroup ogActivityAsCaseDetailsKnown = addField(EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN, NullableOptionGroup.class);
		ActivityAsCaseField activityAsCaseField = addField(EpiDataDto.ACTIVITIES_AS_CASE, ActivityAsCaseField.class);
		activityAsCaseField.setWidthFull();
		activityAsCaseField.setPseudonymized(isPseudonymized);

		FieldHelper.setVisibleWhen(
			getFieldGroup(),
			EpiDataDto.ACTIVITIES_AS_CASE,
			EpiDataDto.ACTIVITY_AS_CASE_DETAILS_KNOWN,
			Collections.singletonList(YesNoUnknown.YES),
			true);

		activityAsCaseField.addValueChangeListener(e -> {
			ogActivityAsCaseDetailsKnown.setEnabled(CollectionUtils.isEmpty(activityAsCaseField.getValue()));
		});
	}

	private void addHeadingsAndInfoTexts() {
		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingExposureInvestigation))
					+ divsCss(
						VSPACE_3,
						I18nProperties.getString(
							parentClass == ContactDto.class ? Strings.infoExposureInvestigationContacts : Strings.infoExposureInvestigation),
						disease == Disease.GIARDIASIS ? I18nProperties.getString(Strings.giardiaInfoExposureInvestigation) : StringUtils.EMPTY),
				ContentMode.HTML),
			LOC_EXPOSURE_INVESTIGATION_HEADING);

		getContent().addComponent(
			new MultilineLabel(divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEpiDataFieldsHint)), ContentMode.HTML),
			LOC_EPI_DATA_FIELDS_HINT);

		if (isConfiguredServer(CountryHelper.COUNTRY_CODE_LUXEMBOURG) && Disease.MEASLES == disease) {
			getContent().addComponent(
				new MultilineLabel(h3(I18nProperties.getString(Strings.headingClusterType)) + divsCss(VSPACE_3), ContentMode.HTML),
				LOC_CLUSTER_TYPE_HEADING);
		}
		// Conclusion heading should be visible for all countries Giardiasis & Cryptosporidiosis specific fields
		getContent().addComponent(
			new MultilineLabel(h3(I18nProperties.getString(Strings.headingEpiConclusion)) + divsCss(VSPACE_3), ContentMode.HTML),
			LOC_CONCLUSION_HEADING);
		getContent().getComponent(LOC_CONCLUSION_HEADING).setVisible(Arrays.asList(Disease.CRYPTOSPORIDIOSIS, Disease.GIARDIASIS).contains(disease));

		getContent().addComponent(
			new MultilineLabel(
				h3(I18nProperties.getString(Strings.headingEpiDataSourceCaseContacts))
					+ divsCss(VSPACE_3, I18nProperties.getString(Strings.infoEpiDataSourceCaseContacts)),
				ContentMode.HTML),
			LOC_SOURCE_CASE_CONTACTS_HEADING);
	}

	public void disableContactWithSourceCaseKnownField() {
		setEnabled(false, EpiDataDto.CONTACT_WITH_SOURCE_CASE_KNOWN);
	}

	public void setGetSourceContactsCallback(Supplier<List<ContactReferenceDto>> callback) {
		((ExposuresField) getField(EpiDataDto.EXPOSURES)).setGetSourceContactsCallback(callback);
	}

	@Override
	protected String createHtmlLayout() {
		return parentClass == CaseDataDto.class ? MAIN_HTML_LAYOUT + SOURCE_CONTACTS_HTML_LAYOUT : MAIN_HTML_LAYOUT;
	}
}
