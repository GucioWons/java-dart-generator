package com.guciowons;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassDescription {
    private final String className;
    private final Map<String, String> fields = new LinkedHashMap<>();
    private final List<String> enumValues = new ArrayList<>();

    public ClassDescription(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void addField(String fieldName, String fieldType) {
        fields.put(fieldName, fieldType);
    }

    public List<String> getEnumValues() {
        return enumValues;
    }

    public void addEnumValue(String value) {
        enumValues.add(value);
    }
}
