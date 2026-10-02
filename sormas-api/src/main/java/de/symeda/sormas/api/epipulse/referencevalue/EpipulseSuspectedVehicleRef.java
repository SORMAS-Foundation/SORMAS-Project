/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2026 SORMAS Foundation gGmbH
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

package de.symeda.sormas.api.epipulse.referencevalue;

import de.symeda.sormas.api.exposure.InfectionSource;

/**
 * Enteric {@code SuspectedVehicle} mapping from {@code epidata.infectionsource}.
 *
 * <p>
 * This is mostly a direct rename of fine-grained {@link InfectionSource} values to EpiPulse
 * codes.
 *
 * <p>
 * Some values are intentionally unmapped: EpiPulse {@code ZOO} has no SORMAS source constant;
 * SORMAS coarse/unknown values (for example {@code FOOD}, {@code ANIMAL}, {@code OTHER},
 * {@code UNKNOWN}, {@code NOT_APPLICABLE}) cannot be safely represented by a fine-grained EpiPulse
 * code.
 */
public enum EpipulseSuspectedVehicleRef {

	BAKERY(InfectionSource.BAKERY_PRODUCTS),
	BAT(InfectionSource.BATS_CONTACT),
	BOVINEMEAT(InfectionSource.BOVINE_MEAT_PRODUCTS),
	BROILERMEAT(InfectionSource.BROILER_MEAT_PRODUCTS),
	CANNED(InfectionSource.CANNED_FOOD_PRODUCTS),
	CAT(InfectionSource.CATS_CONTACT),
	CEREAL(InfectionSource.CEREAL_PRODUCTS),
	CHEESE(InfectionSource.CHEESE),
	DAIRY(InfectionSource.NON_CHEESES_DAIRY_PRODUCTS),
	DOG(InfectionSource.DOGS_CONTACT),
	DRINKS(InfectionSource.DRINKS_BOTTLED_WATER),
	EGG(InfectionSource.EGG_PRODUCTS),
	EXOTICANIMAL(InfectionSource.EXOTIC_PET_CONTACT),
	FARMANIMAL(InfectionSource.FARM_ANIMAL_CONTACT),
	FISH(InfectionSource.FISH_PRODUCTS),
	FOX(InfectionSource.FOX_CONTACT),
	FRUIT(InfectionSource.FRUITS_AND_JUICES),
	GAMEMEAT(InfectionSource.GAME_MEAT_NOT_WILD_BOAR),
	HERBSPICES(InfectionSource.HERBS_AND_SPICES),
	MILK(InfectionSource.MILK),
	MIXEDMEAL(InfectionSource.BUFFET_MEALS),
	MIXEDMEAT(InfectionSource.MIXED_MEAT_PRODUCTS),
	OTHERFOOD(InfectionSource.OTHER_FOODS),
	OTHERMEAT(InfectionSource.OTHER_MEAT_PRODUCTS),
	OTHERPETS(InfectionSource.OTHER_PET_CONTACT),
	OTHERPOULTRY(InfectionSource.UNSPECIFIED_POULTRY),
	OTHERWILDANIMAL(InfectionSource.OTHER_WILD_ANIMAL_CONTACT),
	PIGMEAT(InfectionSource.PORK_PRODUCTS),
	SHEEPMEAT(InfectionSource.SHEEP_MEAT_PRODUCTS),
	SHELLFISH(InfectionSource.SHELLFISH_PRODUCTS),
	SPROUTS(InfectionSource.Sprouts),
	SWEETSCHOC(InfectionSource.confectionery),
	TAPWATER(InfectionSource.TAP_AND_WELL_WATER),
	TURKEYMEAT(InfectionSource.TURKEY_MEAT_PRODUCTS),
	VEGETABLE(InfectionSource.JUICE_AND_VEGETABLE_PRODUCTS),
	WILDBOARMEAT(InfectionSource.WILD_BOAR_MEAT_PRODUCTS);

	private final InfectionSource infectionSource;

	EpipulseSuspectedVehicleRef(InfectionSource infectionSource) {
		this.infectionSource = infectionSource;
	}

	public InfectionSource getInfectionSource() {
		return infectionSource;
	}

	/** Returns EpiPulse code for mapped vehicle, otherwise {@code null}. */
	public static String codeFor(InfectionSource infectionSource) {

		if (infectionSource == null) {
			return null;
		}

		for (EpipulseSuspectedVehicleRef ref : values()) {
			if (ref.infectionSource == infectionSource) {
				return ref.name();
			}
		}

		return null;
	}
}
