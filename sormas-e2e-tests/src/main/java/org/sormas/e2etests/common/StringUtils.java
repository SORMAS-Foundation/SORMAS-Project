package org.sormas.e2etests.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  public static String getMatchedGroupByIndexFromAString(
      String string, String regex, int groupIndex) {
    String returnedMatch = null;
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(string);
    if (matcher.find()) {
      returnedMatch = matcher.group(groupIndex);
    }
    return returnedMatch;
  }
}
