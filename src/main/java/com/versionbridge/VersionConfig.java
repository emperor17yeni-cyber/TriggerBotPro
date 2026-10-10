package com.versionbridge;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VersionConfig {

    private static final String CONFIG_DIR = "config/versionbridge";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.txt";
    private static String targetVersion = null;

    public static void load() {
        try {
            Path configPath = Paths.get(CONFIG_FILE);
            if (Files.exists(configPath)) {
                String content = Files.readString(configPath).trim();
                if (!content.isEmpty() && !content.equals("null")) {
                    targetVersion = content;
                }
            }
        } catch (IOException e) {
            System.err.println("[VersionBridge] Failed to load config: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Path configDir = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }
            Path configPath = Paths.get(CONFIG_FILE);
            Files.writeString(configPath, targetVersion != null ? targetVersion : "");
        } catch (IOException e) {
            System.err.println("[VersionBridge] Failed to save config: " + e.getMessage());
        }
    }

    public static String getTargetVersion() {
        return targetVersion;
    }

    public static void setTargetVersion(String version) {
        targetVersion = version;
    }
}
