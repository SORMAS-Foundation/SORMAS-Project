package org.sormas.e2etests.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

public class DataOperations {

  public String getPartialUuidFromAssociatedLink(String associatedLink) {

    return StringUtils.left(associatedLink, 6);
  }

  public LocalDateTime getLocalDateTimeFromColumns(String date) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy h:mm a");
    return LocalDateTime.parse(date, formatter);
  }
}
