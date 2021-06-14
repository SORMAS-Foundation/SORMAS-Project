package org.sormas.e2etests.steps.web.application.tasks;

public enum ColumnHeaders {
  CREATED_BY("CREATED BY"),
  PRIORITY("PRIORITY"),
  ASSIGNED_TO("ASSIGNED TO"),
  COMMENTS_ON_TASK("COMMENTS ON TASK"),
  ASSOCIATED_LINK("ASSOCIATED LINK"),
  TASK_STATUS("TASK STATUS"),
  REGION("REGION"),
  DISTRICT("DISTRICT"),
  TASK_CONTEXT("TASK CONTEXT"),
  DUE_DATE("DUE DATE"),
  SUGGESTED_START("SUGGESTED START"),
  TASK_TYPE("TASK TYPE"),
  COMMENTS_ON_EXECUTION("COMMENTS ON EXECUTION");

  private final String columnHeader;

  ColumnHeaders(String columnHeader) {
    this.columnHeader = columnHeader;
  }

  @Override
  public String toString() {
    return this.columnHeader;
  }
}
