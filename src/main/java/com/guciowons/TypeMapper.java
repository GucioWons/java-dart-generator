package com.guciowons;

import java.util.Optional;

public class TypeMapper {
    public Optional<String> map(String typeName) {
        return Optional.ofNullable(
                switch (typeName) {
                    case "String" -> "String";
                    case "int", "Integer", "long", "Long" -> "int";
                    case "double", "Double" -> "double";
                    case "boolean", "Boolean" -> "bool";
                    default -> null;
                });
    }
}
