package com.guciowons;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClassDescription {
    private final String className;
    private final Map<String, String> fields = new LinkedHashMap<>();

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
}
