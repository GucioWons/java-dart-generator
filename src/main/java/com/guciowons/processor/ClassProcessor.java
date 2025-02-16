package com.guciowons.processor;

import com.guciowons.ClassDescription;
import com.guciowons.TypeMapper;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class ClassProcessor {
    private final File outputDirectory;
    private final TypeMapper typeMapper;

    private final Map<String, String> typesMap = new HashMap<>();

    private final List<ClassDescription> classDescriptions = new ArrayList<>();

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
            classDescriptions.add(processClass(clazz));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassDescription processClass(Class<?> clazz) {
        ClassDescription classDescription = new ClassDescription(clazz.getSimpleName());
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().isEnum()) {
                classDescription.addField(field.getName(), field.getType().getSimpleName());
            } else if (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType())) {
                if(field.getGenericType() instanceof ParameterizedType parametrizedMapType){
                    Type[] typeArguments = parametrizedMapType.getActualTypeArguments();
                    classDescription.addField(field.getName(), "List<" + typeArguments[0].getClass().getSimpleName() + ">");
                }
            } else if (Map.class.isAssignableFrom(field.getType())) {
                if(field.getGenericType() instanceof ParameterizedType parametrizedMapType){
                    Type[] typeArguments = parametrizedMapType.getActualTypeArguments();
                    classDescription.addField(field.getName(), "Map<" + typeArguments[0].getClass().getSimpleName() + ", " + typeArguments[1].getClass().getSimpleName() + ">");
                }
            } else {
                String fieldType = typeMapper.map(field.getType().getSimpleName(), typesMap)
                        .orElseGet(() -> processNextClass(field));
                classDescription.addField(field.getName(), fieldType);
                typesMap.put(field.getType().getSimpleName(), fieldType);
            }
        }
        return classDescription;
    }

    private String processNextClass(Field field) {
        processClass(field.getType());
        return field.getType().getSimpleName();
    }

    public List<ClassDescription> getClassDescriptions() {
        return classDescriptions;
    }
}
