package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.i18n.I18nProperties;

public enum ValueSeparator {

	DEFAULT(null),
	COMMA(','),
	SEMICOLON(';'),
	TAB('\t');

	private final Character separator;

	ValueSeparator(Character separator) {
		this.separator = separator;
	}

	public static ValueSeparator get(char separator) {
		switch (separator) {
		case ',':
			return COMMA;
		case ';':
			return SEMICOLON;
		case '\t':
			return TAB;
		default:
			return null;
		}
	}

	private char getSeparator() {
		return separator;
	}

	public static char getSeparator(ValueSeparator value, char defaultSeparator) {
		return value == DEFAULT ? defaultSeparator : value.getSeparator();
	}

	public String getCaption(char defaultSeparator) {
		if (this == DEFAULT) {
			ValueSeparator matchingSeparator = get(defaultSeparator);
			return String.format(
				I18nProperties.getEnumCaption(this),
				matchingSeparator != null ? I18nProperties.getEnumCaption(matchingSeparator) : defaultSeparator);
		} else {
			return I18nProperties.getEnumCaption(this);
		}
	}
}
