package de.symeda.sormas.ui.amcharts;

import com.vaadin.flow.component.JsonSerializable;
import elemental.json.Json;
import elemental.json.JsonObject;

import java.time.LocalDate;

public class XYChartData implements JsonSerializable {

    private LocalDate date;
    private String name;
    private int value;

    public XYChartData(LocalDate date, String name, int value) {
        this.date = date;
        this.name = name;
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public JsonObject toJson() {
        JsonObject obj = Json.createObject();
        if (getName() != null) {
            obj.put("name", getName());
        }
        obj.put("value", getValue());
        if (getDate() != null) {
            obj.put("date", getDate().toString());
        }
        return obj;
    }

    @Override
    public JsonSerializable readJson(JsonObject value) {
        return null;
    }
}
