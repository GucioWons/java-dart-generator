package com.guciowons;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ClassProcessor {
    private final File outputDirectory;
    private final TypeMapper typeMapper;

    private final Map<String, String> typesMap = new HashMap<>();

    public ClassProcessor(File outputDirectory) {
        this.outputDirectory = outputDirectory;
        this.typeMapper = new TypeMapper();
    }

    public void processClassFromPath(Path classPath, URLClassLoader classLoader) {
        try {
            String className = outputDirectory.toPath().relativize(classPath)
                    .toString()
                    .replace(File.separator, ".")
                    .replace(".class", "");

            Class<?> clazz = Class.forName(className, false, classLoader);
            processClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void processClass(Class<?> clazz) {
        System.out.println(clazz.getSimpleName() + ":");
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().isEnum()) {
                System.out.println(field.getType().getSimpleName() + ": " + field.getName());
                typesMap.put(field.getType().getSimpleName(), field.getType().getSimpleName());
            } else {
                String fieldType = typeMapper.map(field.getType().getSimpleName(), typesMap)
                        .orElseGet(() -> processTwojaStara(field));
                System.out.println(fieldType + ": " + field.getName());
                typesMap.put(field.getType().getSimpleName(), fieldType);
            }
        }
    }

    private String processTwojaStara(Field field) {
        processClass(field.getType());
        return field.getType().getSimpleName();
    }
}
