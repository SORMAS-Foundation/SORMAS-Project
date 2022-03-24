package org.sormas.e2etests.steps.web.application.facilities;

public enum FacilityTableColumsHeadres {
  NAME("NAME"),
  FACILITY_TYPE("FACILITY TYPE"),
  REGION("REGION"),
  DISTRICT("DISTRICT"),
  COMMUNITY("COMMUNITY"),
  CITY("CITY"),
  LATITUDE("LATITUDE"),
  LONGITUDE("LONGITUDE"),
  EXTERNAL_ID("EXTERNAL ID");

  private final String columnHeader;

  FacilityTableColumsHeadres(String columnHeader) {
    this.columnHeader = columnHeader;
  }

  @Override
  public String toString() {
    return this.columnHeader;
  }
}
