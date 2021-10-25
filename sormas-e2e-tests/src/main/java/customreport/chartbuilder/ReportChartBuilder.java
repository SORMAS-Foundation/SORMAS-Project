package customreport.chartbuilder;

import customreport.data.TableRowObject;
import java.awt.*;
import java.io.File;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

@Slf4j
public abstract class ReportChartBuilder {

  @SneakyThrows
  public static void buildChartForData(List<TableRowObject> data) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (TableRowObject entry : data) {
      String page = entry.getTestName().replace("page", "").trim();
      dataset.addValue(Double.valueOf(entry.getCurrentTime()), page, page);
    }
    JFreeChart barChart =
        ChartFactory.createBarChart(
            "Page load statistics",
            "Page",
            "Time",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false);
    barChart.getPlot().setBackgroundPaint(Color.WHITE);
    int width = 1300;
    int height = 1000;
    File BarChart = new File("customReports/images/BarChart.jpeg");
    ChartUtils.saveChartAsJPEG(BarChart, barChart, width, height);
  }
}
