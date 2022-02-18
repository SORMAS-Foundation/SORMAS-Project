package org.sormas.e2etests.helpers.strings;

import java.text.Normalizer;

public abstract class ASCIIHelper {

  private static String asciiToLatinRegex = "\\P{InBasic_Latin}";

  public static String convertASCIIToLatin(String string) {
    return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll(asciiToLatinRegex, "");
  }
}
