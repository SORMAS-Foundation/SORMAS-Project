package de.symeda.sormas.ui.statistics;

import java.util.List;

import com.explicatis.ext_token_field.Tokenizable;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public abstract class StatisticsFilterElement extends HorizontalLayout {

	abstract List<TokenizableValue> getSelectedValues();

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
