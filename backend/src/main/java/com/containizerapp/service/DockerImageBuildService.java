package com.containizerapp.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class DockerImageBuildService {

    public String buildImage(File projectDir) throws IOException, InterruptedException {

        String imageTag = "containizer-" + UUID.randomUUID().toString().substring(0, 8);

        ProcessBuilder processBuilder = new ProcessBuilder(
                "docker", "build",
                "-t", imageTag,
                "."
        );

        processBuilder.directory(projectDir);
        processBuilder.inheritIO();

        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Docker image build failed");
        }

        return imageTag;
    }
}
