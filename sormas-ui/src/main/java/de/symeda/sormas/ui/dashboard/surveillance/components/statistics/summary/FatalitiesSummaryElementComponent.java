package de.symeda.sormas.ui.dashboard.surveillance.components.statistics.summary;

import java.util.List;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import de.symeda.sormas.api.dashboard.DashboardCaseDto;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.utils.CssStyles;

public class FatalitiesSummaryElementComponent extends HorizontalLayout {

	private final DiseaseSummaryElementComponent caseFatalityRateValue;
	private final DiseaseSummaryElementComponent caseFatalityCountValue;
	private final Label caseFatalityCountGrowth;

	public FatalitiesSummaryElementComponent() {
		setMargin(false);
		setSpacing(false);

		caseFatalityRateValue = new DiseaseSummaryElementComponent(Strings.headingCaseFatalityRate, "00.0%");
		addComponent(caseFatalityRateValue);

		addComponent(new DiseaseSummaryElementComponent(" ", " "));

		caseFatalityCountValue = new DiseaseSummaryElementComponent(Strings.headingFatalities, "0");

		caseFatalityCountGrowth = new Label("", ContentMode.HTML);
		CssStyles.style(caseFatalityCountGrowth, CssStyles.VSPACE_TOP_5);
		caseFatalityCountValue.addComponent(caseFatalityCountGrowth);

		addComponent(caseFatalityCountValue);
		setExpandRatio(caseFatalityCountValue, 1);
		setComponentAlignment(caseFatalityCountValue, Alignment.MIDDLE_RIGHT);
	}

	public void update(List<DashboardCaseDto> newCases, List<DashboardCaseDto> previousCases) {
		int casesCount = newCases.size();
		long fatalCasesCount = newCases.stream().filter(DashboardCaseDto::wasFatal).count();

		long previousFatalCasesCount = previousCases.stream().filter(DashboardCaseDto::wasFatal).count();
		long fatalCasesGrowth = fatalCasesCount - previousFatalCasesCount;
		float fatalityRate = 100 * ((float) fatalCasesCount / (float) (casesCount == 0 ? 1 : casesCount));
		fatalityRate = Math.round(fatalityRate * 100) / 100f;

		caseFatalityRateValue.updateTotalLabel(fatalityRate + "%");
		caseFatalityCountValue.updateTotalLabel(Long.toString(fatalCasesCount));

		String chevronType;
		String criticalLevel;

		if (fatalCasesGrowth > 0) {
			chevronType = VaadinIcons.CHEVRON_UP.getHtml();
			criticalLevel = CssStyles.LABEL_CRITICAL;
		} else if (fatalCasesGrowth < 0) {
			chevronType = VaadinIcons.CHEVRON_DOWN.getHtml();
			criticalLevel = CssStyles.LABEL_POSITIVE;
		} else {
			chevronType = VaadinIcons.CHEVRON_RIGHT.getHtml();
			criticalLevel = CssStyles.LABEL_IMPORTANT;
		}

		caseFatalityCountGrowth.setValue(
			"<div class=\"v-label v-widget " + criticalLevel + " v-label-" + criticalLevel
				+ " align-center v-label-align-center bold v-label-bold v-has-width\" " + "	  style=\"margin-top: 4px;margin-left: 5px;\">"
				+ "		<span class=\"v-icon\" style=\"font-family: VaadinIcons;\">" + chevronType + "		</span>" + "</div>");
	}
}
