package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.FacadeProvider;

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

	public static char getSeparator(ValueSeparator value) {
		return value == DEFAULT ? FacadeProvider.getConfigFacade().getCsvSeparator() : value.getSeparator();
	}
}
