package com.containizerapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;

@Service
public class ProjectTypeDetector {

    public enum ProjectType {
        STATIC_WEB,
        PYTHON,
        NODE
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProjectType detect(File extractedDir) {
        File projectRoot = resolveProjectRoot(extractedDir);

        if (containsPackageJsonWithStart(projectRoot)) {
            return ProjectType.NODE;
        }

        if (containsPythonFile(projectRoot)) {
            return ProjectType.PYTHON;
        }

        if (containsHtmlFile(projectRoot)) {
            return ProjectType.STATIC_WEB;
        }

        throw new IllegalArgumentException("Unsupported project type");
    }

    // PUBLIC because controller uses it
    public File resolveProjectRoot(File dir) {
        File[] files = dir.listFiles();
        if (files != null && files.length == 1 && files[0].isDirectory()) {
            return files[0];
        }
        return dir;
    }

    private boolean containsPackageJsonWithStart(File dir) {
        File pkg = new File(dir, "package.json");
        if (!pkg.exists() || !pkg.isFile()) {
            return false;
        }

        try (FileReader reader = new FileReader(pkg)) {
            JsonNode root = objectMapper.readTree(reader);
            JsonNode scripts = root.get("scripts");
            if (scripts == null || !scripts.has("start")) {
                throw new IllegalArgumentException("Node project missing required 'start' script in package.json");
            }
            return true;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to parse package.json: " + ex.getMessage(), ex);
        }
    }

    private boolean containsPythonFile(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return false;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".py")) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHtmlFile(File dir) {
        File[] files = dir.listFiles();
        if (files == null) return false;

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".html")) {
                return true;
            }
        }
        return false;
    }
}
