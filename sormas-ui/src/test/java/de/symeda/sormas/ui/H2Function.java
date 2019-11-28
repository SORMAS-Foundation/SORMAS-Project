package de.symeda.sormas.ui;

public class H2Function {

	public static float similarity(String a, String b) {
		return a.equalsIgnoreCase(b) ? 1 : 0;
	}
}
