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

    private final Set<String> processedClasses = new HashSet<>();

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
            processClass(clazz);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void processClass(Class<?> clazz) {
        ClassDescription classDescription = new ClassDescription(clazz.getSimpleName());
        processedClasses.add(clazz.getSimpleName());
        if (clazz.isEnum()) {
            Arrays.stream(clazz.getEnumConstants())
                    .map(Object::toString)
                    .forEach(classDescription::addEnumValue);
        } else {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType().isArray() || Collection.class.isAssignableFrom(field.getType())) {
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
                    String fieldType = typeMapper.map(field.getType().getSimpleName())
                            .orElseGet(() -> processNextClass(field));
                    classDescription.addField(field.getName(), fieldType);
                }
            }
        }
        classDescriptions.add(classDescription);
    }

    private String processNextClass(Field field) {
        if(!processedClasses.contains(field.getType().getSimpleName())) {
            processClass(field.getType());
        }
        return field.getType().getSimpleName();
    }

    public List<ClassDescription> getClassDescriptions() {
        return classDescriptions;
    }
}
