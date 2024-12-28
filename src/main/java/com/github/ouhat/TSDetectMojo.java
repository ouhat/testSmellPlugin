package com.github.ouhat;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Mojo(name = "detect", defaultPhase = LifecyclePhase.TEST)
public class TSDetectMojo extends AbstractMojo {

    @Parameter(property = "TsPath", required = true)
    public String TsPath;

    @Parameter(property = "TestPath", defaultValue = "src/test/java")
    public String TestPath;

    @Override
    public void execute() throws MojoExecutionException {
        getLog().info("Ejecutando tsDetect para identificar test smells...");

        try {
            // Construye el comando con el path de los tests proporcionado por el usuario
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", TsPath, "-d", TestPath);
            Process process = processBuilder.start();

            // Lee la salida del proceso
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                getLog().info(line); // Imprime el resultado en la salida estándar
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new MojoExecutionException("Error al ejecutar tsDetect, verifica el TsPath y TestPath.");
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException("Error al ejecutar tsDetect", e);
        }
    }
}
//v1