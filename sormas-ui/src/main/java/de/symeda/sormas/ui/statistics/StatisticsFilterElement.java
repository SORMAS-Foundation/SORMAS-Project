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

	abstract public List<TokenizableValue> getSelectedValues();

	protected List<TokenizableValue> createTokens(Object ...values) {
		return createTokens(null, null, values);
	}
	
	protected List<TokenizableValue> createTokens(StatisticsCaseAttribute attribute, StatisticsCaseSubAttribute subAttribute, Object ...values) {
		List<TokenizableValue> result = new ArrayList<>(values.length);
		for (int i = 0; i < values.length; i++) {
			if (attribute != null || subAttribute != null) {
				result.add(new TokenizableValue(StatisticsHelper.formatAttributeValue(values[i], attribute, subAttribute), i));
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
