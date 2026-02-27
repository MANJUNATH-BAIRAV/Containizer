package com.containizerapp.service;

import com.containizerapp.model.ContainerRunOptions;
import com.containizerapp.service.ProjectTypeDetector.ProjectType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DockerContainerRunService {

    private static final String NAMED_VOLUME = "containizer-data";
    private static final String CONTAINER_VOLUME_PATH = "/data";

    public int runContainer(String imageTag, ProjectType projectType, ContainerRunOptions opts)
            throws IOException, InterruptedException {

        int containerPort;
        if (projectType == ProjectType.PYTHON) {
            containerPort = 5000;
        } else if (projectType == ProjectType.NODE) {
            containerPort = 3000;
        } else {
            containerPort = 80;
        }

        int hostPort = findFreePort();
        String containerName = "containizer-" + UUID.randomUUID().toString().substring(0, 8);

        // If no bind mount provided → ensure named volume exists
        boolean useNamedVolume = (opts == null || !opts.hasVolume());
        if (useNamedVolume) {
            ProcessBuilder volPb = new ProcessBuilder("docker", "volume", "create", NAMED_VOLUME);
            Process volProcess = volPb.start();
            volProcess.waitFor(); // no need to check exit code, create is idempotent
        }

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
        }

        if (useNamedVolume) {
            cmd.add("-v");
            cmd.add(NAMED_VOLUME + ":" + CONTAINER_VOLUME_PATH);
        } else {
            cmd.add("-v");
            cmd.add(opts.volume);
        }

        cmd.add(imageTag);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.inheritIO();

        Process process = pb.start();
        int exit = process.waitFor();
        if (exit != 0) {
            throw new RuntimeException("Docker failed to start container");
        }

        return hostPort;
    }

    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
