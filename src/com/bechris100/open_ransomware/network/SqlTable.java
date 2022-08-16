package com.bechris100.open_ransomware.network;

import java.util.List;
import java.util.Map;

public class SqlTable {

    private final List<Map<String, Object>> data;

    protected SqlTable(List<Map<String, Object>> data) {
        this.data = data;
    }

}
