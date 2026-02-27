package com.containizerapp.service;

import com.containizerapp.model.ContainerRunOptions;
import com.containizerapp.service.ProjectTypeDetector.ProjectType;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContainerService {

    private final String uploadPath = "uploads/";

    public String processZip(File zipFile, ContainerRunOptions opts) throws Exception {
        File extractedDir = extractZip(zipFile);

        ProjectType type = new ProjectTypeDetector().detect(extractedDir);

        generateDockerfile(type, extractedDir);

        String imageName = "containizer-" + UUID.randomUUID();
        runCommand("docker build -t " + imageName + " " + extractedDir.getAbsolutePath());

        String containerName = "c-" + UUID.randomUUID();

        int containerPort = resolveContainerPort(type);
        int hostPort = findFreePort();

        runContainer(imageName, containerName, containerPort, hostPort, opts);

        return "Running at: http://localhost:" + hostPort;
    }

    private File extractZip(File zipFile) throws IOException {
        String id = UUID.randomUUID().toString();
        File destDir = new File(uploadPath + id);
        destDir.mkdirs();

        try (java.util.zip.ZipInputStream zis = new java.util.zip.ZipInputStream(new FileInputStream(zipFile))) {
            java.util.zip.ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    Files.copy(zis, newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
        return destDir;
    }

    private void generateDockerfile(ProjectType type, File extractedDir) throws Exception {
        File dockerfile = new File(extractedDir, "Dockerfile");

        switch (type) {
            case PYTHON:
                String entrypoint = resolvePythonEntrypoint(extractedDir);
                generatePythonDockerfile(dockerfile, entrypoint);
                break;

            case STATIC_WEB:
                copyTemplate("Dockerfile.static", dockerfile);
                break;

            case NODE:
                copyTemplate("Dockerfile.node", dockerfile);
                break;

            default:
                throw new Exception("Unsupported project type.");
        }
    }

    private String resolvePythonEntrypoint(File dir) throws Exception {
        List<String> pyFiles = Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(f -> f.isFile() && f.getName().endsWith(".py"))
                .map(File::getName)
                .sorted()
                .collect(Collectors.toList());

        if (pyFiles.isEmpty()) {
            throw new Exception("Python project must contain at least one .py file");
        }

        return pyFiles.get(0);
    }

    private void generatePythonDockerfile(File dockerfile, String entrypoint) throws IOException {
        String content =
                "FROM python:3.10-slim\n" +
                "WORKDIR /app\n" +
                "COPY . .\n" +
                "RUN pip install --no-cache-dir -r requirements.txt || true\n" +
                "CMD [\"python3\", \"" + entrypoint + "\"]\n";

        try (FileWriter fw = new FileWriter(dockerfile)) {
            fw.write(content);
        }
    }

    private void copyTemplate(String templateName, File target) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("templates/" + templateName);
        if (is == null) throw new FileNotFoundException("Template not found: " + templateName);
        Files.copy(is, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private int resolveContainerPort(ProjectType type) {
        switch (type) {
            case PYTHON:
                return 5000;
            case NODE:
                return 3000;
            case STATIC_WEB:
                return 80;
            default:
                return 80;
        }
    }

    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private void runContainer(String imageName,
                              String containerName,
                              int containerPort,
                              int hostPort,
                              ContainerRunOptions opts) throws Exception {

        List<String> cmd = new ArrayList<>();
        cmd.add("docker");
        cmd.add("run");
        cmd.add("-d");
        cmd.add("--name");
        cmd.add(containerName);
        cmd.add("-p");
        cmd.add(hostPort + ":" + containerPort);

        if (opts != null) {
            if (opts.hasCpu()) {
                cmd.add("--cpus=" + opts.cpu);
            }
            if (opts.hasMemory()) {
                cmd.add("--memory=" + opts.memory);
            }
            if (opts.hasVolume()) {
                cmd.add("-v");
                cmd.add(opts.volume);
            }
        }

        cmd.add(imageName);

        runCommand(String.join(" ", cmd));
    }

    private void runCommand(String command) throws Exception {
        System.out.println("Running: " + command);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
    }
}
