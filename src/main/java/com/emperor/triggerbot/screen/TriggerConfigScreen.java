package com.emperor.triggerbot.screen;

import com.emperor.triggerbot.client.TriggerBotProClient;
import com.emperor.triggerbot.config.TriggerConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public final class TriggerConfigScreen extends Screen {
    private final Screen parent;
    private int tab = 0;
    private final String[] tabs = {"GENEL", "HEDEFLER", "ZAMANLAMA", "GÖRÜNÜM"};
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
            addDrawableChild(ButtonWidget.builder(Text.literal(tabs[i]), b -> { tab = tabIndex; rebuild(); })
                    .dimensions(panelLeft + i * 105, 18, 100, 20).build());
        }

        switch (tab) {
            case 0 -> buildGeneral(panelLeft + 14, panelTop);
            case 1 -> buildTargets(panelLeft + 14, panelTop);
            case 2 -> buildTiming(panelLeft + 14, panelTop);
            case 3 -> buildVisual(panelLeft + 14, panelTop);
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("VARSAYILANLARA DÖN"), b -> {
            c.reset(); rebuild();
        }).dimensions(panelLeft, height - 32, 145, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("KAYDET & ÇIK"), b -> saveAndClose())
                .dimensions(panelLeft + 155, height - 32, 145, 22).build());
    }

    private void buildGeneral(int x, int y) {
        toggle(x, y, "TriggerBot", c.enabled, v -> c.enabled = v);
        toggle(x + 198, y, "Sadece saldırı tuşu", c.requireAttackKey, v -> c.requireAttackKey = v);
        toggle(x, y + 30, "Eşya kullanırken dur", c.pauseWhileUsingItem, v -> c.pauseWhileUsingItem = v);
        toggle(x + 198, y + 30, "Sadece kılıç / balta", c.weaponOnly, v -> c.weaponOnly = v);
        toggle(x, y + 60, "Saldırı bekleme süresi", c.attackOnlyIfCooldownReady, v -> c.attackOnlyIfCooldownReady = v);
        toggle(x + 198, y + 60, "Sprintte saldır", c.allowSprintAttack, v -> c.allowSprintAttack = v);
        toggle(x, y + 90, "Kritik vuruş modu", c.criticalOnly, v -> c.criticalOnly = v);
        toggle(x + 198, y + 90, "El sallama animasyonu", c.swingHand, v -> c.swingHand = v);

        addDrawableChild(ButtonWidget.builder(Text.literal("Preset: Yumuşak"), b -> { c.applyPreset(TriggerConfig.Preset.YUMUSAK); rebuild(); })
                .dimensions(x, y + 138, 140, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Preset: Dengeli"), b -> { c.applyPreset(TriggerConfig.Preset.DENGELI); rebuild(); })
                .dimensions(x + 150, y + 138, 140, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Preset: Hızlı"), b -> { c.applyPreset(TriggerConfig.Preset.HIZLI); rebuild(); })
                .dimensions(x, y + 168, 140, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Preset: Kritik"), b -> { c.applyPreset(TriggerConfig.Preset.KRITIK); rebuild(); })
                .dimensions(x + 150, y + 168, 140, 24).build());
    }

    private void buildTargets(int x, int y) {
        toggle(x, y, "Oyuncular", c.players, v -> c.players = v);
        toggle(x + 198, y, "Düşman yaratıklar", c.hostile, v -> c.hostile = v);
        toggle(x, y + 30, "Pasif yaratıklar", c.passive, v -> c.passive = v);
        toggle(x + 198, y + 30, "Nötr yaratıklar", c.neutral, v -> c.neutral = v);
        toggle(x, y + 60, "Yaratıcı oyuncuları yoksay", c.ignoreCreativePlayers, v -> c.ignoreCreativePlayers = v);
        toggle(x + 198, y + 60, "Görünmezleri yoksay", c.ignoreInvisible, v -> c.ignoreInvisible = v);
        toggle(x, y + 90, "Ölü hedefleri yoksay", c.ignoreDead, v -> c.ignoreDead = v);
        toggle(x + 198, y + 90, "Duvar arkasına vurma", c.requireLineOfSight, v -> c.requireLineOfSight = v);

        addDrawableChild(new DoubleSlider(x, y + 140, 396, "Maksimum menzil", c.maxRange, 1, 6, 0.1,
                value -> c.maxRange = value));
    }

    private void buildTiming(int x, int y) {
        addDrawableChild(new DoubleSlider(x, y, 396, "Minimum CPS", c.minCps, 1, 20, 0.5,
                value -> { c.minCps = value; if (c.maxCps < value) c.maxCps = value; }));
        addDrawableChild(new DoubleSlider(x, y + 38, 396, "Maksimum CPS", c.maxCps, 1, 20, 0.5,
                value -> c.maxCps = Math.max(c.minCps, value)));
        addDrawableChild(new IntSlider(x, y + 76, 396, "Minimum tepki gecikmesi", c.reactionMinMs, 0, 1000, 5,
                value -> { c.reactionMinMs = value; if (c.reactionMaxMs < value) c.reactionMaxMs = value; }));
        addDrawableChild(new IntSlider(x, y + 114, 396, "Maksimum tepki gecikmesi", c.reactionMaxMs, 0, 1500, 5,
                value -> c.reactionMaxMs = Math.max(c.reactionMinMs, value)));
        addDrawableChild(new IntSlider(x, y + 152, 396, "Rastgele aralık (jitter)", c.jitterMs, 0, 80, 1,
                value -> c.jitterMs = value));
        toggle(x, y + 200, "Tepki gecikmesini rastgeleleştir", c.randomizeReaction, v -> c.randomizeReaction = v);
    }

    private void buildVisual(int x, int y) {
        toggle(x, y, "HUD göster", c.hudEnabled, v -> c.hudEnabled = v);
        toggle(x + 198, y, "Hedef adını göster", c.hudShowTarget, v -> c.hudShowTarget = v);
        toggle(x, y + 30, "Menzil bilgisini göster", c.hudShowRange, v -> c.hudShowRange = v);

        addDrawableChild(ButtonWidget.builder(Text.literal("Sağ Shift = menü"), b -> {})
                .dimensions(x, y + 78, 190, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("G = aç / kapat"), b -> {})
                .dimensions(x + 204, y + 78, 190, 24).build());
    }

    private void toggle(int x, int y, String label, boolean value, java.util.function.Consumer<Boolean> setter) {
        ButtonWidget button = ButtonWidget.builder(Text.literal(label + ": " + (value ? "AÇIK" : "KAPALI")), b -> {
            boolean next = !valueFromButton(b, label);
            setter.accept(next);
            b.setMessage(Text.literal(label + ": " + (next ? "AÇIK" : "KAPALI")));
            c.save();
        }).dimensions(x, y, 190, 24).build();
        addDrawableChild(button);
    }

    private boolean valueFromButton(ButtonWidget button, String label) {
        String text = button.getMessage().getString();
        return text.endsWith("AÇIK");
    }

    private void saveAndClose() {
        c.clamp();
        c.save();
        close();
    }

    @Override public void close() {
        client.setScreen(parent);
    }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        renderBackground(ctx, mouseX, mouseY, delta);
        int left = width / 2 - 210;
        int right = width / 2 + 210;
        ctx.fill(left, 0, right, height, 0xB80C0F14);
        ctx.fill(left, 0, right, 2, 0xFF4ADE80);
        ctx.drawText(textRenderer, "TRIGGERBOT PRO", left + 14, 4, 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, "Tek oyunculu • v2.0", right - 110, 4, 0xFF9CA3AF, false);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private static final class DoubleSlider extends SliderWidget {
        private final String name;
        private final double min, max, step;
        private final java.util.function.DoubleConsumer setter;

        DoubleSlider(int x, int y, int width, String name, double value, double min, double max, double step,
                     java.util.function.DoubleConsumer setter) {
            super(x, y, width, 22, Text.literal(name), (value - min) / (max - min));
            this.name = name; this.min = min; this.max = max; this.step = step; this.setter = setter;
            updateMessage();
        }

        @Override protected void updateMessage() {
            double v = min + value * (max - min);
            v = Math.round(v / step) * step;
            setMessage(Text.literal(name + ": " + String.format(java.util.Locale.US, "%.1f", v)));
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
