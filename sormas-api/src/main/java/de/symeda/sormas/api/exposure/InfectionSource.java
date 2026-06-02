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
package de.symeda.sormas.api.exposure;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum InfectionSource {

	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	FOOD,
	@Diseases({
		Disease.GIARDIASIS,
		Disease.CRYPTOSPORIDIOSIS })
	ANIMAL,

	@Diseases({
		Disease.SHIGELLOSIS })
	BAKERY_PRODUCTS, // Bakery products
	@Diseases({
		Disease.SHIGELLOSIS })
	BATS_CONTACT, //Contact with bat(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	BOVINE_MEAT_PRODUCTS, // Bovine meat and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	BROILER_MEAT_PRODUCTS, // Broiler meat (Gallus gallus) and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	CANNED_FOOD_PRODUCTS, // Canned food products
	@Diseases({
		Disease.SHIGELLOSIS })
	CATS_CONTACT, // Contact with cat(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	CEREAL_PRODUCTS, // Cereal products including rice and seeds/pulses (nuts, almonds)
	@Diseases({
		Disease.SHIGELLOSIS })
	CHEESE, // Cheese
	@Diseases({
		Disease.SHIGELLOSIS })
	NON_CHEESES_DAIRY_PRODUCTS, // Dairy products (other than cheeses) 
	@Diseases({
		Disease.SHIGELLOSIS })
	DOGS_CONTACT, 	// Contact with dog(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	DRINKS_BOTTLED_WATER, // Drinks, including bottled water
	@Diseases({
		Disease.SHIGELLOSIS })
	EGG_PRODUCTS, // Egg_products
	@Diseases({
		Disease.SHIGELLOSIS })
	EXOTIC_PET_CONTACT, // Contact with exotic pet(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	FARM_ANIMAL_CONTACT, // Contact with farm animal(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	FISH_PRODUCTS, // Fish and fish products
	@Diseases({
		Disease.SHIGELLOSIS })
	FOX_CONTACT,	// Contact with fox(es)
	@Diseases({
		Disease.SHIGELLOSIS })
	FRUITS_AND_JUICES, // Fruit, berries and juices and other products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	GAME_MEAT_NOT_WILD_BOAR, // Meat from game and products thereof except wild boar
	@Diseases({
		Disease.SHIGELLOSIS })
	HERBS_AND_SPICES, // Herbs and spices
	@Diseases({
		Disease.SHIGELLOSIS })
	MILK, // Milk
	@Diseases({
		Disease.SHIGELLOSIS })
	BUFFET_MEALS, // Mixed or buffet meals
	@Diseases({
		Disease.SHIGELLOSIS })
	MIXED_MEAT_PRODUCTS, // Mixed meat and products thereof

	// Not applicable
	@Diseases({
		Disease.SHIGELLOSIS })
	OTHER_FOODS, // Other foods
	@Diseases({
		Disease.SHIGELLOSIS })
	OTHER_MEAT_PRODUCTS, // Meat from other animals and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	OTHER_PET_CONTACT, // Contact with other pet(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	UNSPECIFIED_POULTRY, // Other or unspecified poultry meat and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	OTHER_WILD_ANIMAL_CONTACT, // Contact with other wild animal(s)
	@Diseases({
		Disease.SHIGELLOSIS })
	PORK_PRODUCTS, // Pig meat and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	SHEEP_MEAT_PRODUCTS, // Sheep meat and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	SHELLFISH_PRODUCTS, 	// Crustaceans, shellfish, molluscs and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	Sprouts, // Sprouts
	@Diseases({
		Disease.SHIGELLOSIS })
	confectionery, // Sweets and chocolate
	@Diseases({
		Disease.SHIGELLOSIS })
	TAP_AND_WELL_WATER, // Tap water including well-water
	@Diseases({
		Disease.SHIGELLOSIS })
	TURKEY_MEAT_PRODUCTS, // Turkey meat and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	JUICE_AND_VEGETABLE_PRODUCTS, // Vegetables and juices and other products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	WILD_BOAR_MEAT_PRODUCTS, // Meat from wild boar and products thereof
	@Diseases({
		Disease.SHIGELLOSIS })
	UNKNOWN,

	NOT_APPLICABLE,
	OTHER;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
