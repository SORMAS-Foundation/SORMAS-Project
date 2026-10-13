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
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.BIONTECH_PFIZER);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_1_BIONTECH_PFIZER {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.BIONTECH_PFIZER);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_4_5_BIONTECH_PFIZER {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.BIONTECH_PFIZER);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_1273 {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.MODERNA);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_1_MODERNA {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.MODERNA);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	MRNA_BIVALENT_BA_4_5_MODERNA {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.MODERNA);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	VALNEVA {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.VALNEVA);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	NVX_COV_2373 {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.NOVAVAX);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	NUVAXOVID {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.NOVAVAX);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	OXFORD_ASTRA_ZENECA {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.ASTRA_ZENECA);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	AD26_COV2_S {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.JOHNSON_JOHNSON);
		}
	},

	@Diseases(value = {
		Disease.CORONAVIRUS })
	SANOFI_GSK {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.SANOFI_GSK);
		}
	},

	@Diseases(value = {
		Disease.CSM })
	MENABCWY {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.PFIZER);
		}
	},

	@Diseases(value = {
		Disease.MONKEYPOX })
	ACAM2000 {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.SANOFI_PASTEUR_BIOLOGICS);
		}
	},

	@Diseases(value = {
		Disease.MONKEYPOX })
	LC_16 {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.KM_BIOLOGICS);
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVENAR_13_PFIZER {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.PFIZER);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of("PCV13");
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	VAXNEUVANCE_MERCK {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.MERCK);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of("PCV15");
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PREVNAR_20_PFIZER {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.PFIZER);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of("PCV20");
		}
	},

	@Diseases(value = {
		Disease.INVASIVE_PNEUMOCOCCAL_INFECTION })
	PNEUMOVAX_23_MERCK {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.MERCK);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of("PPV23");
		}
	},

	@Diseases(value = {
		Disease.MONKEYPOX })
	MVA_BN {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.BAVARIAN_NORDIC);
		}
	},

	@Diseases(value = {
		Disease.DENGUE })
	DENGVAXIA {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.SANOFI_PASTEUR);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_vaccineType_liveAttenuated));
		}

		@Override
		public Optional<String> getInn() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_dengvaxia_inn));
		}

		@Override
		public Optional<String> getAtcCode() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_dengvaxia_atcCode));
		}

		@Override
		public Optional<String> getUniiCode() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_dengvaxia_uniiCode));
		}
	},

	@Diseases(value = {
		Disease.DENGUE })
	QDENGA {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.TAKEDA);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_vaccineType_liveAttenuated));
		}

		@Override
		public Optional<String> getInn() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_qdenga_inn));
		}

		@Override
		public Optional<String> getAtcCode() {
			return Optional.of("J07BF07");
		}

		@Override
		public Optional<String> getUniiCode() {
			return Optional.of("");
		}
	},

	@Diseases(value = {
		Disease.MALARIA })
	RTS_S_AS01 {

		@Override
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.GSK);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_vaccineType_subunit));
		}

		@Override
		public Optional<String> getInn() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_rts_s_as01_inn));
		}

		@Override
		public Optional<String> getAtcCode() {
			return Optional.of("J07BX03");
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
		public Optional<VaccineManufacturer> getManufacturer() {
			return Optional.of(VaccineManufacturer.OXFORD);
		}

		@Override
		public Optional<String> getVaccineType() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_vaccineType_subunit));
		}

		@Override
		public Optional<String> getInn() {
			return Optional.of(I18nProperties.getString(Strings.Vaccine_r21_matrix_m_inn));
		}

		@Override

		public Optional<String> getAtcCode() {
			return Optional.of("");
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

	public Optional<VaccineManufacturer> getManufacturer() {
		return Optional.empty();
	}

	@Nullable
	public Optional<String> getVaccineType() {
		return Optional.empty();
	}

	@Nullable
	public Optional<String> getInn() {
		return Optional.empty();
	}

	@Nullable
	public Optional<String> getAtcCode() {
		return Optional.empty();
	}

	public Optional<String> getUniiCode() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
