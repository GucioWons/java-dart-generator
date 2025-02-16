package com.guciowons;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class ClassProcessor {
    private final File outputDirectory;

    public ClassProcessor(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void processClass(Path classPath, URLClassLoader classLoader) {
        try {
            String className = outputDirectory.toPath().relativize(classPath)
                    .toString()
                    .replace(File.separator, ".")
                    .replace(".class", "");

            Class<?> clazz = null;

                clazz = Class.forName(className, false, classLoader);


            System.out.println(className + ":");
            for (Field field : clazz.getDeclaredFields()) {
                System.out.println(field.getType() + ": " + field.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
