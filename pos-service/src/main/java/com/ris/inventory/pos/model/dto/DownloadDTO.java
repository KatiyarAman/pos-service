package com.ris.inventory.pos.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ris.inventory.pos.controller.converter.JSONArrayConverter;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class DownloadDTO {

    private List<String> columns = new ArrayList<>();

    private List<String> actualColumns = new ArrayList<>();

    @JsonSerialize(converter = JSONArrayConverter.class)
    private JSONArray data;

    public List<String> getActualColumns() {
        return actualColumns;
    }

    public void setActualColumns(List<String> actualColumns) {
        this.actualColumns = actualColumns;
    }

    @JsonIgnore
    public boolean isDataDownloadable() {
        return (!this.columns.isEmpty() & this.data.length() != 0);
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }
}
