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

import static de.symeda.sormas.api.caze.Vaccine.VaccineCodes.Builder;

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
	PREVENAR_13_PFIZER(VaccineManufacturer.PFIZER, new Builder().withVaccineType("PCV13").build()),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	VAXNEUVANCE_MERCK(VaccineManufacturer.MERCK, new Builder().withVaccineType("PCV15").build()),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVNAR_20_PFIZER(VaccineManufacturer.PFIZER, new Builder().withVaccineType("PCV20").build()),
	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PNEUMOVAX_23_MERCK(VaccineManufacturer.MERCK, new Builder().withVaccineType("PPV23").build()),
	@Diseases(value = {
		Disease.MONKEYPOX })
	MVA_BN(VaccineManufacturer.BAVARIAN_NORDIC),
	@Diseases(value = {
		Disease.DENGUE })
	DENGVAXIA(VaccineManufacturer.SANOFI_PASTEUR,
		new Builder().withVaccineType("Live attenuated")
			.withInn("Chimeric yellow fever dengue tetravalent vaccine (live, attenuated)")
			.withAtcCode("J07BF06")
			.withUniiCode("39V92P2S5S")
			.build()),
	@Diseases(value = {
		Disease.DENGUE })
	QDENGA(VaccineManufacturer.TAKEDA,
		new VaccineCodes.Builder().withVaccineType("Live attenuated")
			.withInn("Live attenuated tetravalent dengue vaccine")
			.withAtcCode("J07BF07")
			.withUniiCode("N/A (not yet assigned)")
			.build()),
	@Diseases(value = {
		Disease.MALARIA })
	RTS_S_AS01(VaccineManufacturer.GSK,
		new Builder().withVaccineType("Subunit")
			.withInn("Mosquirix (recombinant protein)")
			.withAtcCode("J07BX03")
			.withUniiCode("7C2S4M6X1H")
			.build()),
	@Diseases(value = {
		Disease.MALARIA })
	R21_MATRIX_M(VaccineManufacturer.OXFORD,
		new Builder().withVaccineType("Subunit")
			.withInn("Recombinant protein vaccine")
			.withAtcCode("N/A (not yet assigned)")
			.withUniiCode("N/A (not yet assigned)")
			.build()),
	UNKNOWN,
	OTHER;

	@Nullable
	private VaccineManufacturer manufacturer;
	@Nullable
	private VaccineCodes vaccineCodes;

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
	 *            the type of the vaccine (e.g. "PCV13")
	 */
	Vaccine(@Nullable VaccineManufacturer manufacturer, @Nullable VaccineCodes vaccineCodes) {
		this.manufacturer = manufacturer;
		this.vaccineCodes = vaccineCodes;
	}

	public VaccineManufacturer getManufacturer() {
		return manufacturer;
	}

	public VaccineCodes getVaccineCodes() {
		return vaccineCodes;
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}

	public static class VaccineCodes {

		private final String inn;
		private final String atcCode;
		private final String uniiCode;
		private final String vaccineType;

		public VaccineCodes(Builder builder) {
			this.inn = builder.inn;
			this.atcCode = builder.atcCode;
			this.uniiCode = builder.uniiCode;
			this.vaccineType = builder.vaccineType;
		}

		static final class Builder {

			private String inn;
			private String atcCode;
			private String uniiCode;
			private String vaccineType;

			public Builder withInn(String inn) {
				this.inn = inn;
				return this;
			}

			public Builder withAtcCode(String atcCode) {
				this.atcCode = atcCode;
				return this;
			}

			public Builder withUniiCode(String uniiCode) {
				this.uniiCode = uniiCode;
				return this;
			}

			public Builder withVaccineType(String vaccineType) {
				this.vaccineType = vaccineType;
				return this;
			}

			public VaccineCodes build() {
				return new VaccineCodes(this);
			}

		}

		public String getInn() {
			return inn;
		}

		public String getAtcCode() {
			return atcCode;
		}

		public String getUniiCode() {
			return uniiCode;
		}

		public String getVaccineType() {
			return vaccineType;
		}
	}

}
