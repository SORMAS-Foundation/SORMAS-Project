package org.sormas.e2etests.steps.web.application.messages;

public enum MessagesTableViewHeaders {
  VISIBLE(""),
  UUID("UUID"),
  TYP("TYP"),
  DATUM_DER_MELDUNG("DATUM DER MELDUNG"),
  MELDER_NAME("MELDER NAME"),
  MELDER_POSTLEITZAHL("MELDER POSTLEITZAHL"),
  KRANKHEIT("KRANKHEIT"),
  VORNAME("VORNAME"),
  NACHNAME("NACHNAME"),
  GEBURTSDATUM("GEBURTSDATUM"),
  POSTLEITZAHL("POSTLEITZAHL"),
  STATUS("STATUS"),
  ZUGEWIESEN_AN("ZUGEWIESEN_AN"),
  EXTERNAL_MESSAGE_PROCESS(""),
  DOWNLOAD("");
  private final String columnHeader;

  MessagesTableViewHeaders(String columnHeader) {
    this.columnHeader = columnHeader;
  }

  @Override
  public String toString() {
    return this.columnHeader;
  }
}
