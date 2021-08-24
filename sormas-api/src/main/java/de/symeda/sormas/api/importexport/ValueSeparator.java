package de.symeda.sormas.api.importexport;

import de.symeda.sormas.api.FacadeProvider;

public enum ValueSeparator {

	DEFAULT,
	COMMA,
	SEMICOLON,
	TAB;

	public static char getSeparator(ValueSeparator value) {
		switch (value) {
		case DEFAULT:
			return FacadeProvider.getConfigFacade().getCsvSeparator();
		case COMMA:
			return ',';
		case SEMICOLON:
			return ';';
		case TAB:
			return '\t';
		default:
			throw new IllegalArgumentException();
		}
	}
}
