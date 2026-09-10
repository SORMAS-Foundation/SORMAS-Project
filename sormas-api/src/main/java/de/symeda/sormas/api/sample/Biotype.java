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
package de.symeda.sormas.api.sample;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.utils.Diseases;

public enum Biotype {

	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_1,
	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_1A,
	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_1B,
	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_2,
	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_3,
	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_4,
	@Diseases({
		Disease.YERSINIOSIS })
	YERSINIOSIS_5,
	UNKNOWN;

	@Override
	public String toString() {
		return I18nProperties.getEnumCaption(this);
	}
}
