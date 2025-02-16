package com.guciowons;

import com.guciowons.processor.ClassProcessor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;

@Mojo(name = "dart-generator", defaultPhase = LifecyclePhase.COMPILE)
public class DartGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-dart")
    private File generatedDartDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            if (!generatedDartDirectory.exists()) {
                generatedDartDirectory.mkdirs();
            }

            URLClassLoader classLoader = new URLClassLoader(new URL[]{outputDirectory.toURI().toURL()});

            ClassProcessor classProcessor = new ClassProcessor(outputDirectory);

            Files.walk(Paths.get(outputDirectory.toURI()))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("DTO.class"))
                    .forEach(path -> classProcessor.processClassFromPath(path, classLoader));

            classProcessor.getClassDescriptions()
                    .forEach(classDescription -> {
                        System.out.println(classDescription.getClassName());
                        classDescription.getFields().forEach((name, type) -> System.out.println(name + ": " + type));
                    });

        } catch (IOException e) {
            System.out.println(e.getCause());
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new MojoExecutionException("Could not generate dart files", e);
        }
    }
}
