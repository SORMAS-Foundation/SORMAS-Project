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

import java.util.Optional;

import javax.annotation.Nullable;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.api.utils.Diseases;


/**
 * This enum contains all vaccines that are currently used in SORMAS.
 * If you want to add a new vaccine, please add it to the enum and add the corresponding translations to the
 * corresponding properties file.
 * 
 * <b>Note:</b> INN, UNII and ATC code are hardcoded because they are fixed for given vaccine and do not require translations.
 * 
 * <b>Note:</b> For codes that are not available return a Optional.of(""), Optional.empty() should only be used if codes are unknown.
 * 
 */
public enum Vaccine {

	@Diseases(value = {
		Disease.CORONAVIRUS })
	COMIRNATY {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.BIONTECH_PFIZER;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_1_BIONTECH_PFIZER {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.BIONTECH_PFIZER;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_4_5_BIONTECH_PFIZER {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.BIONTECH_PFIZER;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_1273 {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.MODERNA;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_1_MODERNA {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.MODERNA;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_4_5_MODERNA {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.MODERNA;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	VALNEVA {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.VALNEVA;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	NVX_COV_2373 {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.NOVAVAX;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	NUVAXOVID {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.NOVAVAX;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	OXFORD_ASTRA_ZENECA {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.ASTRA_ZENECA;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	AD26_COV2_S {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.JOHNSON_JOHNSON;
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	SANOFI_GSK {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.SANOFI_GSK;
		}
	},

	@Diseases(value = {
		Disease.CSM })
	MENABCWY {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.PFIZER;
		}
	},

	@Diseases(value = {
		Disease.MONKEYPOX })
	ACAM2000 {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.SANOFI_PASTEUR_BIOLOGICS;
		}
	},

	@Diseases(value = {
		Disease.MONKEYPOX })
	LC_16 {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.KM_BIOLOGICS;
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVENAR_13_PFIZER {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.PFIZER;
		}

		@Override
		public String getVaccineType() {
			return "PCV13";
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	VAXNEUVANCE_MERCK {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.MERCK;
		}

		@Override
		public String getVaccineType() {
			return "PCV15";
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVNAR_20_PFIZER {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.PFIZER;
		}

		@Override
		public String getVaccineType() {
			return "PCV20";
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PNEUMOVAX_23_MERCK {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.MERCK;
		}

		@Override
		public String getVaccineType() {
			return "PPV23";
		}
	},

	@Diseases(value = {
		Disease.MONKEYPOX })
	MVA_BN {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.BAVARIAN_NORDIC;
		}
	},

	@Diseases(value = {
		Disease.DENGUE })
	DENGVAXIA {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.SANOFI_PASTEUR;
		}

		@Override
		public String getVaccineType() {
			return I18nProperties.getString(Strings.Vaccine_vaccineType_liveAttenuated);
		}

		@Override
		public String getInn() {
			return "";
		}

		@Override
		public String getAtcCode() {
			return "";
		}

		@Override public Optional<String> getUniiCode() {
			return Optional.of("");
		}
	},

	@Diseases(value = {
		Disease.DENGUE })
	QDENGA {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.TAKEDA;
		}

		@Override
		public String getVaccineType() {
			return "Live attenuated";
		}

		@Override
		public String getInn() {
			return "Live attenuated tetravalent dengue vaccine";
		}

		@Override
		public String getAtcCode() {
			return "J07BF07";
		}

		@Override public Optional<String> getUniiCode() {
			return Optional.of("");
		}
	},

	@Diseases(value = {
		Disease.MALARIA })
	RTS_S_AS01 {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.GSK;
		}

		@Override
		public String getVaccineType() {
			return I18nProperties.getString(Strings.Vaccine_vaccineType_subunit);
		}

		@Override
		public String getInn() {
			return "Mosquirix (recombinant protein)";
		}

		@Override
		public String getAtcCode() {
			return "J07BX03";
		}

		@Override
		public Optional<String> getUniiCode() {
			return Optional.of("7C2S4M6X1H");
		}
	},

	@Diseases(value = {
		Disease.MALARIA })
	R21_MATRIX_M {

		@Override
		public VaccineManufacturer getManufacturer() {
			return VaccineManufacturer.OXFORD;
		}

		@Override
		public String getVaccineType() {
			return "Subunit";
		}

		@Override
		public String getInn() {
			return "Recombinant protein vaccine";
		}

		@Override

		public String getAtcCode() {
			return "N/A (not yet assigned)";
		}

		@Override
		public Optional<String> getUniiCode() {
			return Optional.of("");
		}
	},

	UNKNOWN,
	OTHER;

	Vaccine() {
	}

	public VaccineManufacturer getManufacturer() {
		return null;
	}

	@Nullable
	public String getVaccineType() {
		return null;
	}

	@Nullable
	public String getInn() {
		return null;
	}

	@Nullable
	public String getAtcCode() {
		return null;
	}


	public Optional<String> getUniiCode() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
