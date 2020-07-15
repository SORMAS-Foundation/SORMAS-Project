/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.ui.utils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import com.vaadin.v7.ui.ComboBox;

import de.symeda.sormas.api.ReferenceDto;

public class SormasComboBox extends ComboBox {

	private static final long serialVersionUID = 27751677500982303L;

	PlaceholderReferenceDto placeholder = null;

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		AtomicBoolean retVal = new AtomicBoolean(false);
		List<?> idsToRemove =
			items.getItemIds().stream().filter(i -> !PlaceholderReferenceDto.class.isAssignableFrom(i.getClass())).collect(Collectors.toList());
		idsToRemove.forEach(i -> {
			if (removeItem(i)) {
				retVal.set(true);
			}
		});

		return retVal.get();
	}

	public void setPlaceholder(String placeholderText) {
		if (placeholder == null) {
			placeholder = new PlaceholderReferenceDto(placeholderText);
			addItems(placeholder);

			setNullSelectionAllowed(true);
			setNullSelectionItemId(placeholder);
		} else {
			placeholder.setCaption(placeholderText);
		}
	}

	public static class PlaceholderReferenceDto extends ReferenceDto {

		private static final long serialVersionUID = -5675799622797725127L;

		public PlaceholderReferenceDto(String caption) {
			super(null, caption);
		}
	}
}
