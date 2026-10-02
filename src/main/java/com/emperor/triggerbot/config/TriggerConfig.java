package com.emperor.triggerbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TriggerConfig {
    public boolean enabled = true;
    public boolean requireAttackKey = false;
    public boolean players = true;
    public boolean hostile = true;
    public boolean passive = false;
    public boolean neutral = false;
    public boolean requireLineOfSight = true;
    public boolean onlyWhenLookingAtTarget = true;
    public boolean criticalOnly = false;
    public boolean ignoreInvisible = true;
    public boolean ignoreDead = true;
    public boolean ignoreCreativePlayers = true;
    public double maxRange = 4.5;
    public double minCps = 7.0;
    public double maxCps = 10.0;
    public int reactionMinMs = 0;
    public int reactionMaxMs = 35;
    public boolean randomizeReaction = true;
    public boolean swingHand = true;
    public boolean attackOnlyIfCooldownReady = true;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path;

    public static TriggerConfig load(Path configDir) {
        path = configDir.resolve("triggerbotpro.json");
        try {
            Files.createDirectories(configDir);
            if (Files.exists(path)) {
                TriggerConfig loaded = GSON.fromJson(Files.readString(path), TriggerConfig.class);
                if (loaded != null) return loaded;
            }
        } catch (Exception ignored) { }
        TriggerConfig fresh = new TriggerConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        if (path == null) return;
        try { Files.writeString(path, GSON.toJson(this)); }
        catch (IOException ignored) { }
    }

    public void resetTo(String preset) {
        TriggerConfig p = new TriggerConfig();
        switch (preset) {
            case "Legit" -> {
                p.maxRange = 3.8; p.minCps = 5.5; p.maxCps = 7.0; p.reactionMinMs = 20; p.reactionMaxMs = 70;
                p.randomizeReaction = true; p.criticalOnly = false; p.players = true; p.hostile = true; p.passive = false; p.neutral = false;
            }
            case "Balanced" -> {
                p.maxRange = 4.5; p.minCps = 7.0; p.maxCps = 10.0; p.reactionMinMs = 0; p.reactionMaxMs = 35;
                p.randomizeReaction = true; p.criticalOnly = false; p.players = true; p.hostile = true;
            }
            case "Fast" -> {
                p.maxRange = 5.0; p.minCps = 9.0; p.maxCps = 13.0; p.reactionMinMs = 0; p.reactionMaxMs = 10;
                p.randomizeReaction = false; p.criticalOnly = false; p.players = true; p.hostile = true;
            }
            case "Crits" -> {
                p.maxRange = 4.2; p.minCps = 6.0; p.maxCps = 8.0; p.reactionMinMs = 0; p.reactionMaxMs = 25;
                p.criticalOnly = true; p.players = true; p.hostile = true;
            }
            default -> { }
        }
        copyFrom(p);
        save();
    }

    private void copyFrom(TriggerConfig p) {
        enabled=p.enabled; requireAttackKey=p.requireAttackKey; players=p.players; hostile=p.hostile; passive=p.passive; neutral=p.neutral;
        requireLineOfSight=p.requireLineOfSight; onlyWhenLookingAtTarget=p.onlyWhenLookingAtTarget; criticalOnly=p.criticalOnly;
        ignoreInvisible=p.ignoreInvisible; ignoreDead=p.ignoreDead; ignoreCreativePlayers=p.ignoreCreativePlayers; maxRange=p.maxRange;
        minCps=p.minCps; maxCps=p.maxCps; reactionMinMs=p.reactionMinMs; reactionMaxMs=p.reactionMaxMs;
        randomizeReaction=p.randomizeReaction; swingHand=p.swingHand; attackOnlyIfCooldownReady=p.attackOnlyIfCooldownReady;
    }
}
