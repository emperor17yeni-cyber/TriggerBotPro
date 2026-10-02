package com.emperor.triggerbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class TriggerConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CUSTOM = "OZEL";
    private transient Path file;

    // Genel
    public boolean enabled = false;
    public boolean requireAttackKey = false;
    public boolean pauseWhileUsingItem = true;
    public boolean weaponOnly = false;
    public boolean attackOnlyIfCooldownReady = true;
    /** Vuruş için gereken bekleme doluluğu (1.0 = tam dolu, 0.9 = %90). */
    public double cooldownThreshold = 1.0;

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
    /** Kritik modunda tepki gecikmesini ve CPS beklemesini atlar: pencere açılınca aynı tick'te vurur. */
    public boolean critInstant = true;
    public boolean allowSprintAttack = true;
    public boolean swingHand = true;

    // HUD
    public boolean hudEnabled = true;
    public boolean hudShowTarget = true;
    public boolean hudShowRange = true;

    // Aktif profil (Preset adı, elle değişiklik yapılırsa "OZEL")
    public String activePreset = Preset.DENGELI.name();

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

    /** Davranış ve zamanlama ayarlarını nötr başlangıca çeker (enabled / HUD'a dokunmaz). */
    private void applyBase() {
        requireAttackKey = false;
        pauseWhileUsingItem = true;
        weaponOnly = false;
        attackOnlyIfCooldownReady = true;
        cooldownThreshold = 1.0;
        players = true;
        hostile = true;
        passive = false;
        neutral = false;
        maxRange = 4.0;
        minCps = 7.0;
        maxCps = 10.0;
        reactionMinMs = 45;
        reactionMaxMs = 90;
        randomizeReaction = true;
        jitterMs = 0;
        criticalOnly = false;
        critInstant = true;
        allowSprintAttack = true;
        swingHand = true;
    }

    public void reset() {
        enabled = false;
        applyBase();
        jitterMs = 4;
        ignoreCreativePlayers = true;
        ignoreDead = true;
        ignoreInvisible = true;
        requireLineOfSight = true;
        hudEnabled = true;
        hudShowTarget = true;
        hudShowRange = true;
        activePreset = Preset.DENGELI.name();
        clamp();
        save();
    }

    public void applyPreset(Preset preset) {
        applyBase();
        preset.tweak.accept(this);
        activePreset = preset.name();
        clamp();
        save();
    }

    /** Kullanıcı bir ayarı elle değiştirdiğinde çağrılır; HUD'da "Özel" yazar. */
    public void markCustom() {
        activePreset = CUSTOM;
    }

    /** Aktif profil; elle değiştirilmişse null. */
    public Preset currentPreset() {
        try {
            return Preset.valueOf(activePreset);
        } catch (Exception e) {
            return null;
        }
    }

    public String presetLabel() {
        Preset p = currentPreset();
        return p == null ? "Özel" : p.label();
    }

    public void clamp() {
        maxRange = clamp(maxRange, 1.0, 6.0);
        minCps = clamp(minCps, 1.0, 20.0);
        maxCps = clamp(maxCps, minCps, 20.0);
        reactionMinMs = (int) clamp(reactionMinMs, 0, 1000);
        reactionMaxMs = (int) clamp(reactionMaxMs, reactionMinMs, 1500);
        jitterMs = (int) clamp(jitterMs, 0, 80);
        cooldownThreshold = clamp(cooldownThreshold, 0.90, 1.0);
        if (activePreset == null) activePreset = CUSTOM;
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public enum Preset {
        YUMUSAK("Yumuşak", "Rahat tempo, insansı gecikme.", c -> {
            c.maxRange = 3.6; c.minCps = 4.0; c.maxCps = 6.0;
            c.reactionMinMs = 110; c.reactionMaxMs = 180; c.jitterMs = 10;
        }),
        DENGELI("Dengeli", "Günlük kullanım için varsayılan ayar.", c -> {
            c.maxRange = 4.0; c.minCps = 7.0; c.maxCps = 10.0;
            c.reactionMinMs = 45; c.reactionMaxMs = 90; c.jitterMs = 4;
        }),
        HIZLI("Hızlı", "Yüksek tempo. Bekleme %90 dolunca vurur (hasar biraz düşer).", c -> {
            c.maxRange = 4.5; c.minCps = 10.0; c.maxCps = 14.0;
            c.reactionMinMs = 20; c.reactionMaxMs = 50; c.jitterMs = 2;
            c.cooldownThreshold = 0.90;
        }),
        KRITIK("Kritik", "Sadece düşerken, pencere açıldığı ilk tick'te vurur.", c -> {
            c.maxRange = 4.0; c.minCps = 6.0; c.maxCps = 9.0;
            c.reactionMinMs = 55; c.reactionMaxMs = 100; c.jitterMs = 6;
            c.criticalOnly = true; c.critInstant = true; c.weaponOnly = true;
        }),
        KRITIK_DOGAL("Kritik Doğal", "Kritik pencerede kısa insansı gecikmeyle vurur.", c -> {
            c.maxRange = 3.8; c.minCps = 6.0; c.maxCps = 9.0;
            c.reactionMinMs = 30; c.reactionMaxMs = 70; c.jitterMs = 6;
            c.criticalOnly = true; c.critInstant = false; c.weaponOnly = true;
        }),
        SAVASCI("Savaşçı", "Kılıç/balta ile, saldırı tuşu basılıyken oyuncu ve düşmanlara.", c -> {
            c.maxRange = 3.0; c.minCps = 8.0; c.maxCps = 11.0;
            c.reactionMinMs = 35; c.reactionMaxMs = 70; c.jitterMs = 5;
            c.requireAttackKey = true; c.weaponOnly = true;
        }),
        MOB_AVCISI("Mob Avcısı", "Sadece düşman mobları keser, oyunculara dokunmaz.", c -> {
            c.maxRange = 4.5; c.minCps = 10.0; c.maxCps = 14.0;
            c.reactionMinMs = 15; c.reactionMaxMs = 40; c.jitterMs = 0;
            c.cooldownThreshold = 0.95;
            c.players = false; c.hostile = true; c.passive = false; c.neutral = false;
        }),
        HASSAS("Hassas Yardım", "Sen saldırı tuşuna basarken en doğru anda tetikler.", c -> {
            c.maxRange = 3.0; c.minCps = 5.0; c.maxCps = 8.0;
            c.reactionMinMs = 120; c.reactionMaxMs = 220; c.jitterMs = 12;
            c.requireAttackKey = true;
        });

        private final String label;
        private final String description;
        private final Consumer<TriggerConfig> tweak;

        Preset(String label, String description, Consumer<TriggerConfig> tweak) {
            this.label = label;
            this.description = description;
            this.tweak = tweak;
        }

        public String label() { return label; }
        public String description() { return description; }
    }
}
