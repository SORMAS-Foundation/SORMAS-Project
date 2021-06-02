package org.sormas.e2etests.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  public static String getMatchedGroupByIndexFromAString(
      String string, String regex, int groupIndex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(string);
    if (matcher.find()) {
      return matcher.group(groupIndex);
    } else {
      return null;
    }
  }
}
