package customreport.chartbuilder;

import customreport.data.TablePageRowObject;
import java.awt.*;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

@Slf4j
public abstract class ReportChartBuilder {

  public static final int width = 1300;
  public static final int height = 800;
  public static final String generateChartPath =
      "customReports/pagesMeasurements/images/BarChart.jpeg";

  public static void buildChartForData(List<TablePageRowObject> data) {
    try {
      DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
      for (TablePageRowObject entry : data) {
        String page = entry.getTestName();
        dataSet.addValue(Double.valueOf(entry.getCurrentTime()), page, page);
      }
      JFreeChart barChart =
          ChartFactory.createBarChart(
              "Page load statistics",
              "Page name",
              "Loading time",
              dataSet,
              PlotOrientation.VERTICAL,
              true,
              true,
              false);
      barChart.getPlot().setBackgroundPaint(Color.WHITE);
      File BarChart = new File(generateChartPath);
      ChartUtils.saveChartAsJPEG(BarChart, barChart, width, height);
    } catch (Exception e) {
      log.warn("Unable to generate results chart: {} ", e.getMessage());
    }
  }
}
