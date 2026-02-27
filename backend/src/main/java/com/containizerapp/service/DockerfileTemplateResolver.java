package com.containizerapp.service;

import com.containizerapp.service.ProjectTypeDetector.ProjectType;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DockerfileTemplateResolver {

    public InputStream resolve(ProjectType projectType) {

        String templatePath;

        if (projectType == ProjectType.PYTHON) {
            templatePath = "/templates/Dockerfile.python";
        } else if (projectType == ProjectType.STATIC_WEB) {
            templatePath = "/templates/Dockerfile.static";
        } else if (projectType == ProjectType.NODE) {
            templatePath = "/templates/Dockerfile.node";
        } else {
            throw new IllegalArgumentException("Unsupported project type: " + projectType);
        }

        InputStream templateStream = getClass().getResourceAsStream(templatePath);

        if (templateStream == null) {
            throw new IllegalStateException("Dockerfile template not found: " + templatePath);
        }

        return templateStream;
    }
}
