package de.symeda.sormas.ui.utils;

import java.util.Date;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.visit.VisitResult;

public class FollowUpUtils {
  private FollowUpUtils() {
    // hidden constructor
  }

  public static HorizontalLayout createFollowUpLegend() {
    HorizontalLayout legendLayout = new HorizontalLayout();
    legendLayout.setSpacing(false);
    CssStyles.style(legendLayout, CssStyles.VSPACE_TOP_4);

    Label notSymptomaticColor = new Label("");
    styleLegendEntry(notSymptomaticColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_NOT_SYMPTOMATIC, true);
    legendLayout.addComponent(notSymptomaticColor);

    Label notSymptomaticLabel = new Label(VisitResult.NOT_SYMPTOMATIC.toString());
    legendLayout.addComponent(notSymptomaticLabel);

    Label symptomaticColor = new Label("");
    styleLegendEntry(symptomaticColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_SYMPTOMATIC, false);
    legendLayout.addComponent(symptomaticColor);

    Label symptomaticLabel = new Label(VisitResult.SYMPTOMATIC.toString());
    legendLayout.addComponent(symptomaticLabel);

    Label unavailableColor = new Label("");
    styleLegendEntry(unavailableColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_UNAVAILABLE, false);
    legendLayout.addComponent(unavailableColor);

    Label unavailableLabel = new Label(VisitResult.UNAVAILABLE.toString());
    legendLayout.addComponent(unavailableLabel);

    Label uncooperativeColor = new Label("");
    styleLegendEntry(uncooperativeColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_UNCOOPERATIVE, false);
    legendLayout.addComponent(uncooperativeColor);

    Label uncooperativeLabel = new Label(VisitResult.UNCOOPERATIVE.toString());
    legendLayout.addComponent(uncooperativeLabel);

    Label notPerformedColor = new Label("");
    styleLegendEntry(notPerformedColor, CssStyles.LABEL_BACKGROUND_FOLLOW_UP_NOT_PERFORMED, false);
    legendLayout.addComponent(notPerformedColor);

    Label notPerformedLabel = new Label(VisitResult.NOT_PERFORMED.toString());
    legendLayout.addComponent(notPerformedLabel);

    return legendLayout;
  }

  private static void styleLegendEntry(Label label, String style, boolean first) {
    label.setHeight(18, Sizeable.Unit.PIXELS);
    label.setWidth(12, Sizeable.Unit.PIXELS);
    CssStyles.style(label, style, CssStyles.HSPACE_RIGHT_4);

    if (!first) {
      CssStyles.style(label, CssStyles.HSPACE_LEFT_3);
    }
  }

  public static String getVisitResultDescription(VisitResult result, Date date, Date contactDate, Date followUpUntil) {

    if (!DateHelper.isBetween(date, DateHelper.getStartOfDay(contactDate), DateHelper.getEndOfDay(followUpUntil))) {
      return "";
    }
    return result.toString();
  }

  public static String getVisitResultCssStyle(VisitResult result, Date date, Date contactDate, Date followUpUntil) {

    if (!DateHelper.isBetween(date, DateHelper.getStartOfDay(contactDate), DateHelper.getEndOfDay(followUpUntil))) {
      return "";
    }

    switch (result) {
      case NOT_SYMPTOMATIC:
        return CssStyles.GRID_CELL_NOT_SYMPTOMATIC;
      case SYMPTOMATIC:
        return CssStyles.GRID_CELL_SYMPTOMATIC;
      case NOT_PERFORMED:
        return CssStyles.GRID_CELL_NOT_PERFORMED;
      case UNAVAILABLE:
        return CssStyles.GRID_CELL_UNAVAILABLE;
      case UNCOOPERATIVE:
        return CssStyles.GRID_CELL_UNCOOPERATIVE;
      default:
        throw new IndexOutOfBoundsException(DataHelper.toStringNullable(result));
    }
  }
}
