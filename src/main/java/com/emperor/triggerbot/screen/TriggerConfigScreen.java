package com.emperor.triggerbot.screen;

import com.emperor.triggerbot.client.TriggerBotProClient;
import com.emperor.triggerbot.config.TriggerConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class TriggerConfigScreen extends Screen {
    private static final int TAB_GENERAL = 0, TAB_TARGETS = 1, TAB_TIMING = 2, TAB_CRIT = 3, TAB_PROFILES = 4, TAB_VISUAL = 5;

    private final Screen parent;
    private int tab = TAB_PROFILES;
    private final String[] tabs = {"GENEL", "HEDEFLER", "ZAMANLAMA", "KRİTİK", "PROFİLLER", "GÖRÜNÜM"};
    private TriggerConfig c;

    public TriggerConfigScreen(Screen parent) {
        super(Text.literal("TriggerBot Pro"));
        this.parent = parent;
    }

    @Override protected void init() {
        c = TriggerBotProClient.CONFIG;
        rebuild();
    }

    private void rebuild() {
        clearChildren();
        int cx = width / 2;
        int panelLeft = cx - 210;
        int panelTop = 42;
        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            ButtonWidget tabButton = ButtonWidget.builder(Text.literal(tabs[i]), b -> { tab = tabIndex; rebuild(); })
                    .dimensions(panelLeft + i * 70, 18, 68, 20).build();
            tabButton.active = tab != i;
            addDrawableChild(tabButton);
        }

        switch (tab) {
            case TAB_GENERAL -> buildGeneral(panelLeft + 14, panelTop);
            case TAB_TARGETS -> buildTargets(panelLeft + 14, panelTop);
            case TAB_TIMING -> buildTiming(panelLeft + 14, panelTop);
            case TAB_CRIT -> buildCritical(panelLeft + 14, panelTop);
            case TAB_PROFILES -> buildProfiles(panelLeft + 14, panelTop);
            case TAB_VISUAL -> buildVisual(panelLeft + 14, panelTop);
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("VARSAYILANLARA DÖN"), b -> {
            c.reset(); rebuild();
        }).dimensions(panelLeft, height - 32, 145, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("KAYDET & ÇIK"), b -> saveAndClose())
                .dimensions(panelLeft + 155, height - 32, 145, 22).build());
    }

    private void buildGeneral(int x, int y) {
        plainToggle(x, y, "TriggerBot", c.enabled, v -> c.enabled = v);
        toggle(x + 198, y, "Sadece saldırı tuşu", c.requireAttackKey, v -> c.requireAttackKey = v);
        toggle(x, y + 30, "Eşya kullanırken dur", c.pauseWhileUsingItem, v -> c.pauseWhileUsingItem = v);
        toggle(x + 198, y + 30, "Sadece kılıç / balta", c.weaponOnly, v -> c.weaponOnly = v);
        toggle(x, y + 60, "Saldırı bekleme süresi", c.attackOnlyIfCooldownReady, v -> c.attackOnlyIfCooldownReady = v);
        toggle(x + 198, y + 60, "Sprintte saldır", c.allowSprintAttack, v -> c.allowSprintAttack = v);
        toggle(x, y + 90, "El sallama animasyonu", c.swingHand, v -> c.swingHand = v);
    }

    private void buildTargets(int x, int y) {
        toggle(x, y, "Oyuncular", c.players, v -> c.players = v);
        toggle(x + 198, y, "Düşman yaratıklar", c.hostile, v -> c.hostile = v);
        toggle(x, y + 30, "Pasif yaratıklar", c.passive, v -> c.passive = v);
        toggle(x + 198, y + 30, "Nötr yaratıklar", c.neutral, v -> c.neutral = v);
        plainToggle(x, y + 60, "Yaratıcı oyuncuları yoksay", c.ignoreCreativePlayers, v -> c.ignoreCreativePlayers = v);
        plainToggle(x + 198, y + 60, "Görünmezleri yoksay", c.ignoreInvisible, v -> c.ignoreInvisible = v);
        plainToggle(x, y + 90, "Ölü hedefleri yoksay", c.ignoreDead, v -> c.ignoreDead = v);
        plainToggle(x + 198, y + 90, "Duvar arkasına vurma", c.requireLineOfSight, v -> c.requireLineOfSight = v);

        addDrawableChild(new DoubleSlider(x, y + 140, 396, "Maksimum menzil", c.maxRange, 1, 6, 0.1, 1,
                value -> { c.maxRange = value; c.markCustom(); }));
    }

    private void buildTiming(int x, int y) {
        addDrawableChild(new DoubleSlider(x, y, 396, "Minimum CPS", c.minCps, 1, 20, 0.5, 1,
                value -> { c.minCps = value; if (c.maxCps < value) c.maxCps = value; c.markCustom(); }));
        addDrawableChild(new DoubleSlider(x, y + 34, 396, "Maksimum CPS", c.maxCps, 1, 20, 0.5, 1,
                value -> { c.maxCps = Math.max(c.minCps, value); c.markCustom(); }));
        addDrawableChild(new IntSlider(x, y + 68, 396, "Minimum tepki gecikmesi", c.reactionMinMs, 0, 1000, 5,
                value -> { c.reactionMinMs = value; if (c.reactionMaxMs < value) c.reactionMaxMs = value; c.markCustom(); }));
        addDrawableChild(new IntSlider(x, y + 102, 396, "Maksimum tepki gecikmesi", c.reactionMaxMs, 0, 1500, 5,
                value -> { c.reactionMaxMs = Math.max(c.reactionMinMs, value); c.markCustom(); }));
        addDrawableChild(new IntSlider(x, y + 136, 396, "Rastgele aralık (jitter)", c.jitterMs, 0, 80, 1,
                value -> { c.jitterMs = value; c.markCustom(); }));
        addDrawableChild(new DoubleSlider(x, y + 170, 396, "Vuruş için bekleme doluluğu", c.cooldownThreshold, 0.90, 1.0, 0.01, 2,
                value -> { c.cooldownThreshold = value; c.markCustom(); }));
        toggle(x, y + 204, "Tepki gecikmesini rastgeleleştir", c.randomizeReaction, v -> c.randomizeReaction = v);
    }

    private void buildCritical(int x, int y) {
        toggle(x, y, "Kritik vuruş modu", c.criticalOnly, v -> c.criticalOnly = v);
        toggle(x + 198, y, "Anında vur (gecikmesiz)", c.critInstant, v -> c.critInstant = v);
        toggle(x, y + 30, "Sadece kılıç / balta", c.weaponOnly, v -> c.weaponOnly = v);
    }

    private void buildProfiles(int x, int y) {
        TriggerConfig.Preset[] presets = TriggerConfig.Preset.values();
        TriggerConfig.Preset active = c.currentPreset();
        for (int i = 0; i < presets.length; i++) {
            final TriggerConfig.Preset preset = presets[i];
            boolean isActive = preset == active;
            Text label = isActive
                    ? Text.literal("» " + preset.label()).formatted(Formatting.GREEN, Formatting.BOLD)
                    : Text.literal(preset.label());
            ButtonWidget button = ButtonWidget.builder(label, b -> { c.applyPreset(preset); rebuild(); })
                    .dimensions(x + (i % 2) * 198, y + (i / 2) * 30, 190, 24)
                    .tooltip(Tooltip.of(Text.literal(preset.description())))
                    .build();
            addDrawableChild(button);
        }
    }

    private void buildVisual(int x, int y) {
        plainToggle(x, y, "HUD göster", c.hudEnabled, v -> c.hudEnabled = v);
        plainToggle(x + 198, y, "Hedef adını göster", c.hudShowTarget, v -> c.hudShowTarget = v);
        plainToggle(x, y + 30, "Menzil bilgisini göster", c.hudShowRange, v -> c.hudShowRange = v);

        addDrawableChild(ButtonWidget.builder(Text.literal("Sağ Shift = menü"), b -> {})
                .dimensions(x, y + 78, 190, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("G = aç / kapat"), b -> {})
                .dimensions(x + 204, y + 78, 190, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("H = sıradaki profil"), b -> {})
                .dimensions(x, y + 108, 190, 24).build());
    }

    /** Profili etkileyen ayar: değişince HUD'da "Özel" yazar. */
    private void toggle(int x, int y, String label, boolean value, java.util.function.Consumer<Boolean> setter) {
        addToggle(x, y, label, value, setter, true);
    }

    /** Profilden bağımsız ayar (TriggerBot aç/kapat, hedef filtreleri, HUD). */
    private void plainToggle(int x, int y, String label, boolean value, java.util.function.Consumer<Boolean> setter) {
        addToggle(x, y, label, value, setter, false);
    }

    private void addToggle(int x, int y, String label, boolean value, java.util.function.Consumer<Boolean> setter, boolean affectsProfile) {
        ButtonWidget button = ButtonWidget.builder(Text.literal(label + ": " + (value ? "AÇIK" : "KAPALI")), b -> {
            boolean next = !valueFromButton(b);
            setter.accept(next);
            b.setMessage(Text.literal(label + ": " + (next ? "AÇIK" : "KAPALI")));
            if (affectsProfile) c.markCustom();
            c.save();
        }).dimensions(x, y, 190, 24).build();
        addDrawableChild(button);
    }

    private boolean valueFromButton(ButtonWidget button) {
        return button.getMessage().getString().endsWith("AÇIK");
    }

    private void saveAndClose() {
        c.clamp();
        c.save();
        close();
    }

    @Override public void close() {
        client.setScreen(parent);
    }

    /** ESC ile çıksan bile slider değişiklikleri kaybolmasın. */
    @Override public void removed() {
        if (c != null) c.save();
    }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int left = width / 2 - 210;
        int right = width / 2 + 210;
        ctx.fill(left, 0, right, height, 0xB80C0F14);
        ctx.fill(left, 0, right, 2, 0xFF4ADE80);
        ctx.drawText(textRenderer, "TRIGGERBOT PRO", left + 14, 4, 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, "Tek oyunculu • v2.1", right - 110, 4, 0xFF9CA3AF, false);

        int x = left + 14;
        int y = 42;
        if (tab == TAB_PROFILES) {
            TriggerConfig.Preset active = c.currentPreset();
            ctx.drawText(textRenderer, "Aktif profil: " + c.presetLabel(), x, y + 128, 0xFF4ADE80, true);
            String desc = active == null ? "Ayarları elle değiştirdin. Bir profile tıklayınca hepsi tek seferde uygulanır."
                    : active.description();
            ctx.drawText(textRenderer, desc, x, y + 142, 0xFFD1D5DB, false);
            ctx.drawText(textRenderer, "İpucu: H tuşu oyun içinde sıradaki profile geçer.", x, y + 160, 0xFF9CA3AF, false);
        } else if (tab == TAB_CRIT) {
            int ty = y + 66;
            ctx.drawText(textRenderer, "Kritik vuruş için şunlar gerekir:", x, ty, 0xFF4ADE80, true);
            ctx.drawText(textRenderer, "• Zıpla (boşluk) ve düşerken vur — bot tam o anı bekler", x, ty + 14, 0xFFD1D5DB, false);
            ctx.drawText(textRenderer, "• Koşma (sprint) kapalı olmalı, koşarken kritik olmaz", x, ty + 28, 0xFFD1D5DB, false);
            ctx.drawText(textRenderer, "• Yerde, suda, merdivende, körlükte olmamalısın", x, ty + 42, 0xFFD1D5DB, false);
            ctx.drawText(textRenderer, "• Silah bekleme çubuğu dolu olmalı (en az %95)", x, ty + 56, 0xFFD1D5DB, false);
            ctx.drawText(textRenderer, "Anında vur AÇIK: pencere açılan ilk tick'te vurur.", x, ty + 76, 0xFF9CA3AF, false);
            ctx.drawText(textRenderer, "KAPALI: tepki gecikmesi uygulanır (daha doğal, biraz geç).", x, ty + 90, 0xFF9CA3AF, false);
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    private static final class DoubleSlider extends SliderWidget {
        private final String name;
        private final double min, max, step;
        private final String fmt;
        private final java.util.function.DoubleConsumer setter;

        DoubleSlider(int x, int y, int width, String name, double value, double min, double max, double step, int decimals,
                     java.util.function.DoubleConsumer setter) {
            super(x, y, width, 22, Text.literal(name), (value - min) / (max - min));
            this.name = name; this.min = min; this.max = max; this.step = step; this.setter = setter;
            this.fmt = "%." + decimals + "f";
            updateMessage();
        }

        @Override protected void updateMessage() {
            double v = min + value * (max - min);
            v = Math.round(v / step) * step;
            setMessage(Text.literal(name + ": " + String.format(java.util.Locale.US, fmt, v)));
        }

        @Override protected void applyValue() {
            double v = min + value * (max - min);
            v = Math.round(v / step) * step;
            value = (v - min) / (max - min);
            setter.accept(v);
        }
    }

    private static final class IntSlider extends SliderWidget {
        private final String name;
        private final int min, max, step;
        private final java.util.function.IntConsumer setter;

        IntSlider(int x, int y, int width, String name, int value, int min, int max, int step,
                  java.util.function.IntConsumer setter) {
            super(x, y, width, 22, Text.literal(name), (value - min) / (double) (max - min));
            this.name = name; this.min = min; this.max = max; this.step = step; this.setter = setter;
            updateMessage();
        }

        @Override protected void updateMessage() {
            int v = min + (int) Math.round(value * (max - min));
            v = Math.round(v / (float) step) * step;
            setMessage(Text.literal(name + ": " + v + " ms"));
        }

        @Override protected void applyValue() {
            int v = min + (int) Math.round(value * (max - min));
            v = Math.round(v / (float) step) * step;
            value = (v - min) / (double) (max - min);
            setter.accept(v);
        }
    }
}
