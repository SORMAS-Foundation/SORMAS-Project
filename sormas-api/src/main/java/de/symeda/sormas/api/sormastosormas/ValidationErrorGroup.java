/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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
 */

package de.symeda.sormas.api.sormastosormas;

import de.symeda.sormas.api.i18n.I18nProperties;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class ValidationErrorGroup implements SormasToSormasI18nMessage, Serializable {

		private static final long serialVersionUID = 2075921523238507571L;

		private final String i18nTag;

		private final String uuid;

		public ValidationErrorGroup(String i18nTag) {
			this.i18nTag = i18nTag;
			this.uuid = null;
		}

		public ValidationErrorGroup(String i18nTag, String uuid) {
			this.i18nTag = i18nTag;
			this.uuid = uuid;
		}

		@Override
		public String getI18nTag() {
			return i18nTag;
		}

		@Override
		public Object[] getArgs() {
			return new Object[] {
				uuid };
		}

		@Override
		public String getHumanMessage() {
			if (StringUtils.isNotBlank(uuid)) {
				return String.format("%s %s", I18nProperties.getCaption(i18nTag), uuid);
			} else {
				return I18nProperties.getCaption(i18nTag);
			}
		}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ValidationErrorGroup that = (ValidationErrorGroup) o;
		return Objects.equals(i18nTag, that.i18nTag) && Objects.equals(uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(i18nTag, uuid);
	}
}