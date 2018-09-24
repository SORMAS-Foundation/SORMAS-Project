package de.symeda.sormas.backend.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @see PasswordHelper
 */
public class PasswordHelperTest {
 
	private static final int LENGTH = 20;
 
	private static final String[] FORBIDDEN = {
			"0",
			"1",
			"O",
			"I",
			"V",
			"l",
			"v" };
 
	@Test
	public void testCreatePass() {
 
		for (int i = 0; i < 100; i++) {
 
			String password = PasswordHelper.createPass(LENGTH);
			assertEquals("Unerwartete Passwortlänge", LENGTH, password.length());
			for (int j = 0; j < FORBIDDEN.length; j++) {
				assertFalse("Unerlaubtes Zeichen " + FORBIDDEN[j] + " enthalten: " + password, password.contains(FORBIDDEN[j]));
			}
		}
	}
}