/*package de.symeda.sormas.ui.amcharts;



import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.function.SerializableConsumer;
import elemental.json.Json;
import elemental.json.JsonArray;

import java.util.List;

@JavaScript("https://www.amcharts.com/lib/4/core.js")
@JavaScript("https://www.amcharts.com/lib/4/charts.js")
@JavaScript("https://www.amcharts.com/lib/4/plugins/forceDirected.js")
@JavaScript("https://www.amcharts.com/lib/4/themes/animated.js")
@JavaScript("./src/xychart-connector.js")
public class XYChart {//extends Div {

    public XYChart() {
        setWidthFull();
        setHeight("400px");
        initConnector(getElement());
    }


    private void initConnector(Element layout) {
        runBeforeClientResponse(ui -> ui.getPage().executeJs(
                "window.Vaadin.Flow.amChartsConnector.initLazy($0)",
                getElement()));
    }

    private void runBeforeClientResponse(SerializableConsumer<UI> command) {
        getElement().getNode().runWhenAttached(ui -> ui
                .beforeClientResponse(this, context -> command.accept(ui)));
    }


    public void setData(List<XYChartData> datas) {
        JsonArray array = Json.createArray();
        for (int i = 0; i < datas.size(); i++) {
            array.set(i, datas.get(i).toJson());
        }
        runBeforeClientResponse(ui -> getElement()
                .callJsFunction("$connector.setData", array));
    }
}*/
