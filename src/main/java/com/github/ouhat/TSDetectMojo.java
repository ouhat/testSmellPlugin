package com.github.ouhat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mojo(name = "detect", defaultPhase = LifecyclePhase.TEST)
public class TSDetectMojo extends AbstractMojo {

    @Parameter(property = "TsPath", required = true)
    private String TsPath;

    @Parameter(property = "CsvOutputPath", defaultValue = "${project.build.directory}/testSmellsInput.csv")
    private String CsvOutputPath;

    @Parameter(property = "TestPaths", required = true)
    private String[] TestPaths;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Ejecutando tsDetect para identificar test smells...");

        try {
            // Crear el archivo CSV con las rutas proporcionadas
            Path csvPath = Paths.get(CsvOutputPath);
            Files.createDirectories(csvPath.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
                for (String testPath : TestPaths) {
                    // Escribir cada ruta de prueba en el formato requerido
                    writer.write(String.format("myApp,%s,%s%n", testPath, "")); // No se especifica archivo de producción
                }
            }
            getLog().info("Archivo CSV creado en: " + CsvOutputPath);

            // Ejecutar el jar con el archivo CSV como entrada
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", TsPath, csvPath.toString());
            Process process = processBuilder.start();

            // Leer e imprimir la salida del proceso
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    getLog().info(line);
                }
            }

            // Comprobar el código de salida
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new MojoExecutionException("Error al ejecutar tsDetect. Código de salida: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Error al ejecutar tsDetect", e);
        }
    }
}
