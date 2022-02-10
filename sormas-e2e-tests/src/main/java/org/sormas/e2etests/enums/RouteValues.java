package org.sormas.e2etests.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum RouteValues {
  ROUTE_ORAL("Oral"),
  ROUTE_IV("IV"),
  ROUTE_RECTAL("Rectal"),
  ROUTE_TOPICAL("Topical"),
  ROUTE_OTHER("Other");

  private final String routeType;

  RouteValues(String routeType) {

    this.routeType = routeType;
  }

  /** Returns values used for UI tests */
  public static String getRandomRouteValues() {
    Random random = new Random();
    return String.valueOf(RouteValues.values()[random.nextInt(values().length)].routeType);
  }

  /** Returns values used for UI tests */
  public static String getRandomRouteValuesWithoutOther() {
    Random random = new Random();
    List<RouteValues> tValues =
        Arrays.stream(RouteValues.values())
            .filter(x -> !StringUtils.equals(x.getRouteType(), "Other"))
            .collect(Collectors.toList());
    return tValues.get(random.nextInt(tValues.size())).routeType;
  }
}
