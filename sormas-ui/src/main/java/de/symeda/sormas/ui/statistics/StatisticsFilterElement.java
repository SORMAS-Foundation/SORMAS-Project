/*******************************************************************************
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.symeda.sormas.ui.statistics;

import java.util.ArrayList;
import java.util.List;

import com.explicatis.ext_token_field.Tokenizable;
import com.vaadin.ui.HorizontalLayout;

import de.symeda.sormas.api.statistics.StatisticsCaseAttribute;
import de.symeda.sormas.api.statistics.StatisticsCaseSubAttribute;
import de.symeda.sormas.api.statistics.StatisticsHelper;

@SuppressWarnings("serial")
public abstract class StatisticsFilterElement extends HorizontalLayout {

	protected StatisticsFilterElement() {
		setMargin(false);
		setSpacing(false);
	}
	
	abstract public List<TokenizableValue> getSelectedValues();

	protected List<TokenizableValue> createTokens(Object ...values) {
		return createTokens(null, null, values);
	}
	
	protected List<TokenizableValue> createTokens(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute, Object ...values) {
		List<TokenizableValue> result = new ArrayList<>(values.length);
		for (int i = 0; i < values.length; i++) {
			if (attribute != null || subAttribute != null) {
				result.add(new TokenizableValue(StatisticsHelper.buildGroupingKey(values[i], attribute, subAttribute), i));
			} else {
				result.add(new TokenizableValue(values[i], i));
			}
		}

		return result;
	}
	
	public static class TokenizableValue implements Tokenizable {

		private final Object value;
		private final String stringRepresentation;
		private final long id;

		public TokenizableValue(Object value, long id) {
			this.value = value;
			stringRepresentation = null;
			this.id = id;
		}

		public TokenizableValue(Object value, String stringRepresentation, long id) {
			this.value = value;
			this.stringRepresentation = stringRepresentation;
			this.id = id;
		}

		public Object getValue() {
			return value;
		}

		@Override
		public String getStringValue() {
			if (stringRepresentation != null) {
				return stringRepresentation;
			} else {
				return value.toString();
			}
		}

		@Override
		public long getIdentifier() {
			return id;
		}

		@Override
		public String toString() {
			return getStringValue();
		}

	}
	
}
