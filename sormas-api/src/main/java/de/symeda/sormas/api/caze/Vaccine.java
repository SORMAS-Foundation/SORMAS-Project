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

package de.symeda.sormas.api.caze;

import javax.annotation.Nullable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum Vaccine {

	@Diseases(value = {
		Disease.CORONAVIRUS })
	COMIRNATY(VaccineManufacturer.BIONTECH_PFIZER),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_1_BIONTECH_PFIZER(VaccineManufacturer.BIONTECH_PFIZER),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_4_5_BIONTECH_PFIZER(VaccineManufacturer.BIONTECH_PFIZER),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_1273(VaccineManufacturer.MODERNA),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_1_MODERNA(VaccineManufacturer.MODERNA),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_4_5_MODERNA(VaccineManufacturer.MODERNA),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	VALNEVA(VaccineManufacturer.VALNEVA),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	NVX_COV_2373(VaccineManufacturer.NOVAVAX),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	NUVAXOVID(VaccineManufacturer.NOVAVAX),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	OXFORD_ASTRA_ZENECA(VaccineManufacturer.ASTRA_ZENECA),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	AD26_COV2_S(VaccineManufacturer.JOHNSON_JOHNSON),
	@Diseases(value = {
		Disease.CORONAVIRUS })
	SANOFI_GSK(VaccineManufacturer.SANOFI_GSK),
	@Diseases(value = {
		Disease.CSM })
	MenABCWY(VaccineManufacturer.PFIZER),
	@Diseases(value = {
		Disease.MONKEYPOX })
	ACAM2000(VaccineManufacturer.SANOFI_PASTEUR_BIOLOGICS),
	@Diseases(value = {
		Disease.MONKEYPOX })
	LC_16(VaccineManufacturer.KM_BIOLOGICS),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVENAR_13_PFIZER(VaccineManufacturer.PFIZER, "PCV13"),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	VAXNEUVANCE_MERCK(VaccineManufacturer.MERCK, "PCV15"),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVNAR_20_PFIZER(VaccineManufacturer.PFIZER, "PCV20"),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PNEUMOVAX_23_MERCK(VaccineManufacturer.MERCK, "PPV23"),
	@Diseases(value = {
		Disease.MONKEYPOX })
	MVA_BN(VaccineManufacturer.BAVARIAN_NORDIC),
	@Diseases(value = {
		Disease.DENGUE })
	DENGVAXIA(VaccineManufacturer.SANOFI_PASTEUR,
		"Live attenuated",
		"Chimeric yellow fever dengue tetravalent vaccine (live, attenuated)",
		"J07BF06",
		"39V92P2S5S"),
	@Diseases(value = {
		Disease.DENGUE })
	QDENGA(VaccineManufacturer.TAKEDA, "Live attenuated", "Live attenuated tetravalent dengue vaccine", "J07BF07", "N/A (not yet assigned)"),
	@Diseases(value = {
		Disease.MALARIA })
	RTS_S_AS01(VaccineManufacturer.GSK, "Subunit", "Mosquirix (recombinant protein)", "J07BX03", "7C2S4M6X1H"),
	@Diseases(value = {
		Disease.MALARIA })
	R21_MATRIX_M(VaccineManufacturer.OXFORD, "Subunit", "Recombinant protein vaccine", "N/A (not yet assigned)", "N/A (not yet assigned)"),
	UNKNOWN,
	OTHER;

	@Nullable
	private VaccineManufacturer manufacturer;
	@Nullable
	private String vaccineType;
	@Nullable
	private String inn;
	@Nullable
	private String atcCode;
	@Nullable
	private String uniiCode;

	Vaccine() {
	}

	Vaccine(VaccineManufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	/**
	 * This constructor is used for vaccines that have a specific type (e.g. "PCV13") and its manufacturers.
	 *
	 * @param manufacturer
	 *            the manufacturer of the vaccine
	 * @param vaccineType
	 *            the type of the vaccine (e.g. "PCV13")
	 */
	Vaccine(@Nullable VaccineManufacturer manufacturer, @Nullable String vaccineType) {
		this.manufacturer = manufacturer;
		this.vaccineType = vaccineType;
	}

	/**
	 * This constructor is used for vaccines that have a specific type (e.g. "PCV13") and its manufacturers along with inn, atcCode and
	 * uniiCode.
	 * 
	 * @param manufacturer
	 * @param vaccineType
	 * @param inn
	 * @param atcCode
	 * @param uniiCode
	 */
	Vaccine(
		@Nullable VaccineManufacturer manufacturer,
		@Nullable String vaccineType,
		@Nullable String inn,
		@Nullable String atcCode,
		@Nullable String uniiCode) {
		this.manufacturer = manufacturer;
		this.vaccineType = vaccineType;
		this.inn = inn;
		this.atcCode = atcCode;
		this.uniiCode = uniiCode;
	}

	public VaccineManufacturer getManufacturer() {
		return manufacturer;
	}

	@Nullable
	public String getVaccineType() {
		return vaccineType;
	}

	@Nullable
	public String getInn() {
		return inn;
	}

	@Nullable
	public String getAtcCode() {
		return atcCode;
	}

	@Nullable
	public String getUniiCode() {
		return uniiCode;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
