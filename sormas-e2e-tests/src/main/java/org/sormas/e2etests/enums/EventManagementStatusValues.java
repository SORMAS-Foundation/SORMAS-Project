package org.sormas.e2etests.enums;

import lombok.Getter;

@Getter
public enum EventManagementStatusValues {
  ONGOING("ONGOING");
  private final String value;

  EventManagementStatusValues(String value) {
    this.value = value;
  }
}
