package com.containizerapp.controller;

import com.containizerapp.model.ContainerRunOptions;
import com.containizerapp.service.*;
import com.containizerapp.service.ProjectTypeDetector.ProjectType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String BASE_DIR =
            System.getProperty("user.dir") + File.separator + "containizer-data";

    private static final String UPLOAD_DIR = BASE_DIR + File.separator + "uploads";
    private static final String EXTRACT_DIR = BASE_DIR + File.separator + "extracted";

    private final ZipExtractionService zipExtractionService;
    private final ProjectTypeDetector projectTypeDetector;
    private final DockerfileTemplateResolver dockerfileTemplateResolver;
    private final DockerImageBuildService dockerImageBuildService;
    private final DockerContainerRunService dockerContainerRunService;

    public UploadController(
            ZipExtractionService zipExtractionService,
            ProjectTypeDetector projectTypeDetector,
            DockerfileTemplateResolver dockerfileTemplateResolver,
            DockerImageBuildService dockerImageBuildService,
            DockerContainerRunService dockerContainerRunService
    ) {
        this.zipExtractionService = zipExtractionService;
        this.projectTypeDetector = projectTypeDetector;
        this.dockerfileTemplateResolver = dockerfileTemplateResolver;
        this.dockerImageBuildService = dockerImageBuildService;
        this.dockerContainerRunService = dockerContainerRunService;
    }

    @PostMapping
    public ResponseEntity<String> uploadZip(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "cpu", required = false) String cpu,
            @RequestParam(value = "memory", required = false) String memory,
            @RequestParam(value = "volume", required = false) String volume
    ) {

        String originalFilename = file.getOriginalFilename();
        System.out.println("/api/upload called. filename=" + originalFilename + ", size=" + file.getSize());

        if (file.isEmpty() || originalFilename == null || !originalFilename.endsWith(".zip")) {
            return ResponseEntity.badRequest().body("Only ZIP files are allowed");
        }

        String requestId = UUID.randomUUID().toString();

        try {
            new File(UPLOAD_DIR).mkdirs();
            new File(EXTRACT_DIR).mkdirs();

            // 1. Save ZIP
            File zipFile = new File(UPLOAD_DIR, requestId + ".zip");
            file.transferTo(zipFile);

            // 2. Extract ZIP
            File rawExtractDir = new File(EXTRACT_DIR, requestId);
            zipExtractionService.extractZip(zipFile, rawExtractDir);

            // 3. Resolve project root
            File projectRoot = projectTypeDetector.resolveProjectRoot(rawExtractDir);

            // 4. Detect project type
            ProjectType projectType = projectTypeDetector.detect(projectRoot);

            // 5. Copy Dockerfile
            File dockerfile = new File(projectRoot, "Dockerfile");
            try (InputStream templateStream = dockerfileTemplateResolver.resolve(projectType)) {
                Files.copy(templateStream, dockerfile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            // 6-8. Build and run Docker container asynchronously
            new Thread(() -> {
                try {
                    String imageTag = dockerImageBuildService.buildImage(projectRoot);
                    
                    ContainerRunOptions opts = new ContainerRunOptions();
                    opts.cpu = cpu;
                    opts.memory = memory;
                    opts.volume = volume;
                    
                    int port = dockerContainerRunService.runContainer(imageTag, projectType, opts);
                    System.out.println("Container started for request " + requestId + " on port " + port);
                } catch (Exception e) {
                    System.err.println("Failed to build/run container for request " + requestId + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            return ResponseEntity.ok(
                    "Upload accepted. Container is being built for request: " + requestId + "\nCheck docker ps to see running containers."
            );

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Failed to process project: " + e.getMessage());
        }
    }
}
