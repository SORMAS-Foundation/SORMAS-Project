/*package de.symeda.sormas.ui.amcharts;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The main view contains a button and a click listener.
 */
// @NpmPackage(value = "@amcharts/amcharts4", version = "4.7.10")

/*
@Route("")
@PWA(name = "Project Base for Vaadin", shortName = "Project Base")
public class MainView extends VerticalLayout {

    public MainView() {
        H1 title = new H1("XY Chart");
        add(title);

        final XYChart xyChart = new XYChart();
        add(xyChart);

        // let data = [];
        // let visits = 10;
        // for (let i = 1; i < 366; i++) {
        //     visits += Math.round((Math.random() < 0.5 ? 1 : -1) * Math.random() * 10);
        //     data.push({ date: new Date(2018, 0, i), name: "name" + i, value: visits });
        // }
        generateData(xyChart);

       
    }

    private void generateData(XYChart xyChart) {
        List<XYChartData> datas = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i < 366; i++) {
            int visits = random.nextInt(100) - 50;
            LocalDate firstDate = LocalDate.of(2018, 1, 1);
            datas.add(new XYChartData(firstDate.plusDays(i), "name "+ i, visits));
        }

        xyChart.setData(datas);
    }


}*/
