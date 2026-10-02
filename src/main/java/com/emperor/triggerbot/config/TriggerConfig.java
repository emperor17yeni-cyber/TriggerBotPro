package com.emperor.triggerbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TriggerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private transient Path file;

    // Genel
    public boolean enabled = false;
    public boolean requireAttackKey = false;
    public boolean pauseWhileUsingItem = true;
    public boolean weaponOnly = false;
    public boolean attackOnlyIfCooldownReady = true;
    public boolean singleplayerOnly = true;

    // Hedef
    public boolean players = true;
    public boolean hostile = true;
    public boolean passive = false;
    public boolean neutral = false;
    public boolean ignoreCreativePlayers = true;
    public boolean ignoreDead = true;
    public boolean ignoreInvisible = true;
    public boolean requireLineOfSight = true;
    public double maxRange = 4.0;

    // Zamanlama
    public double minCps = 7.0;
    public double maxCps = 10.0;
    public int reactionMinMs = 45;
    public int reactionMaxMs = 90;
    public boolean randomizeReaction = true;
    public int jitterMs = 0;

    // Kritik / vuruş
    public boolean criticalOnly = false;
    public boolean allowSprintAttack = true;
    public boolean swingHand = true;

    // HUD
    public boolean hudEnabled = true;
    public boolean hudShowTarget = true;
    public boolean hudShowRange = true;

    public static TriggerConfig load(Path configDir) {
        TriggerConfig config = new TriggerConfig();
        config.file = configDir.resolve("triggerbotpro.json");
        try {
            Files.createDirectories(configDir);
            if (Files.exists(config.file)) {
                TriggerConfig loaded = GSON.fromJson(Files.readString(config.file), TriggerConfig.class);
                if (loaded != null) {
                    loaded.file = config.file;
                    loaded.clamp();
                    return loaded;
                }
            }
        } catch (Exception ignored) { }
        config.save();
        return config;
    }

    public void save() {
        if (file == null) return;
        try {
            clamp();
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(this));
        } catch (IOException ignored) { }
    }

    public void reset() {
        enabled = false;
        requireAttackKey = false;
        pauseWhileUsingItem = true;
        weaponOnly = false;
        attackOnlyIfCooldownReady = true;
        singleplayerOnly = true;
        players = true;
        hostile = true;
        passive = false;
        neutral = false;
        ignoreCreativePlayers = true;
        ignoreDead = true;
        ignoreInvisible = true;
        requireLineOfSight = true;
        maxRange = 4.0;
        minCps = 7.0;
        maxCps = 10.0;
        reactionMinMs = 45;
        reactionMaxMs = 90;
        randomizeReaction = true;
        jitterMs = 0;
        criticalOnly = false;
        allowSprintAttack = true;
        swingHand = true;
        hudEnabled = true;
        hudShowTarget = true;
        hudShowRange = true;
        clamp();
        save();
    }

    public void applyPreset(Preset preset) {
        switch (preset) {
            case YUMUSAK -> {
                maxRange = 3.8; minCps = 5.0; maxCps = 7.0;
                reactionMinMs = 90; reactionMaxMs = 150; jitterMs = 8;
                criticalOnly = false; weaponOnly = false;
            }
            case DENGELI -> {
                maxRange = 4.0; minCps = 7.0; maxCps = 10.0;
                reactionMinMs = 45; reactionMaxMs = 90; jitterMs = 4;
                criticalOnly = false; weaponOnly = false;
            }
            case HIZLI -> {
                maxRange = 4.5; minCps = 10.0; maxCps = 14.0;
                reactionMinMs = 20; reactionMaxMs = 50; jitterMs = 2;
                criticalOnly = false; weaponOnly = false;
            }
            case KRITIK -> {
                maxRange = 4.0; minCps = 6.0; maxCps = 9.0;
                reactionMinMs = 55; reactionMaxMs = 100; jitterMs = 6;
                criticalOnly = true; weaponOnly = true;
            }
        }
        clamp();
        save();
    }

    public void clamp() {
        maxRange = clamp(maxRange, 1.0, 6.0);
        minCps = clamp(minCps, 1.0, 20.0);
        maxCps = clamp(maxCps, minCps, 20.0);
        reactionMinMs = (int) clamp(reactionMinMs, 0, 1000);
        reactionMaxMs = (int) clamp(reactionMaxMs, reactionMinMs, 1500);
        jitterMs = (int) clamp(jitterMs, 0, 80);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public enum Preset {
        YUMUSAK("Yumuşak"),
        DENGELI("Dengeli"),
        HIZLI("Hızlı"),
        KRITIK("Kritik");

        private final String label;
        Preset(String label) { this.label = label; }
        public String label() { return label; }
    }
}
