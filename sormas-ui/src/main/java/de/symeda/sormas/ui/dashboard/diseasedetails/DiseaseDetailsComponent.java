package de.symeda.sormas.ui.dashboard.diseasedetails;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.i18n.Captions;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Strings;
import de.symeda.sormas.ui.dashboard.DashboardDataProvider;
import de.symeda.sormas.ui.utils.CssStyles;

public class DiseaseDetailsComponent extends CssLayout {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private DashboardDataProvider dashboardDataProvider;
    private static final String HTML_DIV_END = "</div>";


    public DiseaseDetailsComponent(DashboardDataProvider dashboardDataProvider) {
        this.dashboardDataProvider = dashboardDataProvider;
        addStyleName("disease-detail-card-display-top");
    }

    public void refresh(){
        addTopLayout(
                dashboardDataProvider.getDiseaseBurdenDetail().getDisease(),
                dashboardDataProvider.getDiseaseBurdenDetail().getCaseCount(),
                dashboardDataProvider.getDiseaseBurdenDetail().getOutbreakDistrictCount() > 0);

        addStatsLayout(
                dashboardDataProvider.getDiseaseBurdenDetail().getCaseDeathCount(),
                dashboardDataProvider.getDiseaseBurdenDetail().getCaseCount(),
                dashboardDataProvider.getDiseaseBurdenDetail().getOutbreakDistrict(),
                dashboardDataProvider.getDiseaseBurdenDetail().getLastReportedDistrictName(),
                dashboardDataProvider.getDiseaseBurdenDetail().getDisease());
    }

    private void addTopLayout(Disease disease, Long casesCount, boolean isOutbreak) {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(false);
        CssStyles.style(layout, CssStyles.getDiseaseColor(disease));
        layout.setHeight(200, Unit.PIXELS);
        layout.setWidth(250, Unit.PIXELS);

        HorizontalLayout nameAndOutbreakLayout = new HorizontalLayout();
        nameAndOutbreakLayout.setMargin(false);
        nameAndOutbreakLayout.setSpacing(false);
        nameAndOutbreakLayout.setHeight(90, Unit.PIXELS);
        nameAndOutbreakLayout.setWidth(200, Unit.PIXELS);

        HorizontalLayout nameLayout = new HorizontalLayout();
        nameLayout.setMargin(false);
        nameLayout.setSpacing(false);
        nameLayout.setHeight(50, Unit.PIXELS);
        nameLayout.setWidth(200, Unit.PIXELS);
        Label nameLabel = new Label(disease.toShortString());
        nameLabel.setSizeUndefined();
        nameLabel.setHeight(20, Unit.PIXELS);

        CssStyles.style(
                nameLabel,
                CssStyles.LABEL_WHITE,
                nameLabel.getValue().length() > 12 ? CssStyles.LABEL_SMALL : CssStyles.LABEL_WHITE,
                CssStyles.LABEL_LARGE,
                CssStyles.ALIGN_CENTER,
                CssStyles.LABEL_UPPERCASE);
        nameLayout.addComponent(nameLabel);
        nameLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_CENTER);
        nameAndOutbreakLayout.addComponent(nameLayout);
        nameAndOutbreakLayout.setExpandRatio(nameLayout, 1);

        if (isOutbreak) {
            HorizontalLayout outbreakLayout = new HorizontalLayout();
            outbreakLayout.setMargin(false);
            outbreakLayout.setSpacing(false);
            outbreakLayout.setHeight(15, Unit.PIXELS);
            outbreakLayout.setWidth(100, Unit.PIXELS);
            Label outbreakLabel = new Label(I18nProperties.getCaption(Captions.dashboardOutbreak).toUpperCase(), ContentMode.HTML);
            outbreakLabel.setStyleName("disease-detail-outbreak-display", true);

            outbreakLayout.addComponent(outbreakLabel);
            nameAndOutbreakLayout.addComponent(outbreakLayout);
        }

        layout.addComponent(nameAndOutbreakLayout);
        layout.setExpandRatio(nameAndOutbreakLayout, 1);

        HorizontalLayout countLayout = new HorizontalLayout();
        countLayout.setMargin(false);
        countLayout.setSpacing(false);
        CssStyles.style(countLayout, CssStyles.getDiseaseColor(disease));
        countLayout.setHeight(40, Unit.PIXELS);
        countLayout.setWidth(70, Unit.PIXELS);

        Label countLabel = new Label("", ContentMode.HTML);
        countLabel.setValue(
                "<div style=\"font-weight: normal; font-size: 60px; margin: -100px; text-align: center; color: white\">"
                        + casesCount.toString() + HTML_DIV_END);

        countLayout.addComponent(countLabel);
        countLayout.setComponentAlignment(countLabel, Alignment.MIDDLE_CENTER);

        layout.addComponent(countLayout);
        layout.setComponentAlignment(countLayout, Alignment.BOTTOM_CENTER);
        layout.setExpandRatio(countLayout, 0.65f);

        addComponent(layout);
    }

    private void addStatsLayout(Long fatalities, Long totalCase, String outbreakDistrict, String district, Disease disease) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(250, Unit.PIXELS);
        layout.setHeight(120, Unit.PIXELS);
        layout.setMargin(false);
        layout.setSpacing(false);

        CssStyles.style(layout, CssStyles.getDiseaseColor(disease), CssStyles.BACKGROUND_DARKER);
        float cfrPercent = calculateCfr(fatalities, totalCase);


        layout.addComponent(createDeathCfrItem(I18nProperties.getCaption(Captions.dashboardFatalities)+": ",
                fatalities.toString()+"", fatalities > 0,
                I18nProperties.getCaption(Captions.DiseaseBurden_caseFatalityRate)+": ",
                String.valueOf(cfrPercent)));


        HorizontalLayout statsItem = createStatsItem(
                I18nProperties.getCaption(Captions.dashboardLastReportedDistrict) + ": ",
                district.length() == 0 ? I18nProperties.getString(Strings.none) : district,
                false,
                district.length() > 10
        );

        CssStyles.style(statsItem, CssStyles.VSPACE_TOP_4, CssStyles.LABEL_WHITE);
        layout.addComponent(statsItem);

        statsItem = createStatsItem(
                I18nProperties.getCaption(Captions.DiseaseBurden_outbreakDistrictCount)+": ",
                outbreakDistrict.length() == 0 ?  I18nProperties.getString(Strings.none) : outbreakDistrict,
                false,
                outbreakDistrict.length() > 10
        );

        CssStyles.style(statsItem, CssStyles.VSPACE_4, CssStyles.LABEL_WHITE);
        layout.addComponent(statsItem);
        addComponent(layout);
    }

    private HorizontalLayout createDeathCfrItem(String fatalityLabel, String fatalityValue,
                                                boolean isCritical, String cfrLabel, String cfrValue ) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(false);
        layout.setSpacing(true);

        Label fatalityNameLabel = new Label("", ContentMode.HTML);
        CssStyles.style(fatalityNameLabel, CssStyles.LABEL_WHITE, CssStyles.LABEL_PRIMARY, isCritical ? CssStyles.LABEL_CRITICAL : "", CssStyles.HSPACE_LEFT_3);

        fatalityNameLabel.setValue("<div style='float: left; width: 60px; margin-top: 10px;'>" + fatalityLabel + HTML_DIV_END +
                "<div style='float: left; margin-top: 10px; margin-right: 60px;'>" + fatalityValue+ HTML_DIV_END);


        layout.addComponent(fatalityNameLabel);
        layout.setExpandRatio(fatalityNameLabel, 1);

        Label cfrNameLabel = new Label("", ContentMode.HTML);
        CssStyles.style(
                cfrNameLabel,
                CssStyles.LABEL_WHITE,
                CssStyles.LABEL_PRIMARY,
                cfrValue.length() > 10 ? CssStyles.LABEL_SMALL : CssStyles.LABEL_WHITE,
                isCritical ? CssStyles.LABEL_CRITICAL : "");

        cfrNameLabel.setValue("<div style='float: left; width: 50px; margin-top: 10px;'>" + cfrLabel + HTML_DIV_END +
                "<div style='float: left; margin-top: 10px;'>" + cfrValue + HTML_DIV_END);

        layout.addComponent(cfrNameLabel);

        return layout;

    }

    private HorizontalLayout createStatsItem(String label, String value, boolean isCritical, boolean singleColumn) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth(250, Unit.PIXELS);
        layout.setMargin(false);
        layout.setSpacing(false);

        Label nameLabel = new Label(label);
        CssStyles.style(
                nameLabel,
                CssStyles.LABEL_WHITE,
                CssStyles.LABEL_PRIMARY, isCritical ? CssStyles.LABEL_CRITICAL : "",
                CssStyles.HSPACE_LEFT_3,
                CssStyles.LABEL_IMPORTANT
        );
        layout.addComponent(nameLabel);

        if (!singleColumn) {
            layout.setExpandRatio(nameLabel, 1);
        }

        Label valueLabel = new Label(value);
        CssStyles.style(
                valueLabel,
                CssStyles.LABEL_WHITE,
                CssStyles.LABEL_PRIMARY,
                value.length() > 16 ? CssStyles.LABEL_SMALL : CssStyles.LABEL_WHITE,
                isCritical ? CssStyles.LABEL_CRITICAL : "",
                singleColumn ? CssStyles.HSPACE_LEFT_5 : CssStyles.ALIGN_CENTER);

        layout.addComponent(valueLabel);

        layout.setExpandRatio(valueLabel, singleColumn ? 1f : 0.65f);
        layout.setComponentAlignment(valueLabel, Alignment.MIDDLE_CENTER);
        return layout;
    }

    private float calculateCfr(long fatalities, long totalCaseCount){
        if (fatalities == 0 )
            return 0;
        return ((float) fatalities / totalCaseCount) * 100;
    }
}
