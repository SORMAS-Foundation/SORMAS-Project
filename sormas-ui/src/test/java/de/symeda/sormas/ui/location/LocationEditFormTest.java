/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.ui.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.feature.FeatureConfigurationIndexDto;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.infrastructure.InfrastructureDtoWithDefault;
import de.symeda.sormas.api.infrastructure.InfrastructureFacade;
import de.symeda.sormas.api.infrastructure.country.CountryReferenceDto;
import de.symeda.sormas.api.infrastructure.facility.FacilityType;
import de.symeda.sormas.api.infrastructure.facility.FacilityTypeGroup;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.fieldaccess.UiFieldAccessCheckers;
import de.symeda.sormas.api.utils.fieldvisibility.FieldVisibilityCheckers;
import de.symeda.sormas.backend.TestDataCreator.RDCF;
import de.symeda.sormas.ui.AbstractUiBeanTest;

class LocationEditFormTest extends AbstractUiBeanTest {

	// the server country of the test setup is "nigeria"
	private static final String HOME_COUNTRY_ISO_CODE = "NGA";
	private static final String FOREIGN_COUNTRY_ISO_CODE = "DJI";

	private RDCF defaultRdcf;

	@Override
	public void init() {
		super.init();

		defaultRdcf = creator.createRDCF();
		markAsDefault(getRegionFacade(), defaultRdcf.region.getUuid());
		markAsDefault(getDistrictFacade(), defaultRdcf.district.getUuid());
		markAsDefault(getCommunityFacade(), defaultRdcf.community.getUuid());
		creator.createCountry("Nigeria", HOME_COUNTRY_ISO_CODE, "566");
		creator.createCountry("Djibouti", FOREIGN_COUNTRY_ISO_CODE, "262");

		FeatureConfigurationIndexDto featureConfiguration =
			new FeatureConfigurationIndexDto(DataHelper.createUuid(), null, null, null, null, null, true, null);
		getFeatureConfigurationFacade().saveFeatureConfiguration(featureConfiguration, FeatureType.HIDE_JURISDICTION_FIELDS);
	}

	@Test
	void shouldOfferFacilitiesOfDefaultDistrictWhenJurisdictionFieldsAreHidden() {
		LocationEditForm form = createForm();

		selectHospitalFacilityType(form);

		assertFalse(form.getField(LocationDto.DISTRICT).isVisible());
		assertEquals(defaultRdcf.district, form.getField(LocationDto.DISTRICT).getValue());
		assertTrue(getFacilityField(form).getItemIds().contains(defaultRdcf.facility));
	}

	@Test
	void shouldKeepFacilitiesWhenHomeCountryIsSelectedAndJurisdictionFieldsAreHidden() {
		LocationEditForm form = createForm();
		selectHospitalFacilityType(form);

		selectCountry(form, HOME_COUNTRY_ISO_CODE);

		assertEquals(defaultRdcf.region, form.getField(LocationDto.REGION).getValue());
		assertEquals(defaultRdcf.district, form.getField(LocationDto.DISTRICT).getValue());
		assertEquals(defaultRdcf.community, form.getField(LocationDto.COMMUNITY).getValue());
		assertTrue(getFacilityField(form).getItemIds().contains(defaultRdcf.facility));
		assertNull(getFacilityField(form).getComponentError());
	}

	@Test
	void shouldRestoreFacilitiesWhenForeignCountryIsRemovedAndJurisdictionFieldsAreHidden() {
		LocationEditForm form = createForm();
		selectHospitalFacilityType(form);

		selectCountry(form, FOREIGN_COUNTRY_ISO_CODE);
		form.getField(LocationDto.COUNTRY).setValue(null);

		assertEquals(defaultRdcf.district, form.getField(LocationDto.DISTRICT).getValue());
		assertTrue(getFacilityField(form).getItemIds().contains(defaultRdcf.facility));
	}

	@Test
	void shouldNotAskForDistrictOfForeignCountryWhenJurisdictionFieldsAreHidden() {
		LocationEditForm form = createForm();

		selectCountry(form, FOREIGN_COUNTRY_ISO_CODE);
		selectHospitalFacilityType(form);

		// facilities belong to the districts of the server country, so there are none to offer abroad
		assertNull(form.getField(LocationDto.DISTRICT).getValue());
		assertTrue(getFacilityField(form).getItemIds().isEmpty());
		// the district can not be chosen while it is hidden, so the hint asking for it must not be shown
		assertNull(getFacilityField(form).getComponentError());
	}

	private static <DTO extends InfrastructureDtoWithDefault> void markAsDefault(InfrastructureFacade<DTO, ?, ?, ?> facade, String uuid) {
		DTO infrastructure = facade.getByUuid(uuid);
		infrastructure.setDefaultInfrastructure(true);
		facade.save(infrastructure);
	}

	private static LocationEditForm createForm() {
		LocationEditForm form = new LocationEditForm(FieldVisibilityCheckers.getNoop(), UiFieldAccessCheckers.getNoop());
		form.setValue(LocationDto.build());
		return form;
	}

	private static void selectHospitalFacilityType(LocationEditForm form) {
		form.getFacilityTypeGroup().setValue(FacilityTypeGroup.MEDICAL_FACILITY);
		form.getFacilityType().setValue(FacilityType.HOSPITAL);
	}

	private static void selectCountry(LocationEditForm form, String isoCode) {
		ComboBox countryField = (ComboBox) form.getField(LocationDto.COUNTRY);
		CountryReferenceDto country = countryField.getItemIds()
			.stream()
			.map(CountryReferenceDto.class::cast)
			.filter(c -> isoCode.equals(c.getIsoCode()))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Country not offered: " + isoCode));
		countryField.setValue(country);
	}

	private static ComboBox getFacilityField(LocationEditForm form) {
		return (ComboBox) form.getField(LocationDto.FACILITY);
	}
}
