package com.emperor.triggerbot.screen;

import com.emperor.triggerbot.client.TriggerBotProClient;
import com.emperor.triggerbot.config.TriggerConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class TriggerConfigScreen extends Screen {
    private final Screen parent;
    private int page = 0;
    private int row = 0;
    private int presetIndex = 1;

    public TriggerConfigScreen(Screen parent) { super(Text.literal("TriggerBot Pro")); this.parent = parent; }

    @Override protected void init() { rebuild(); }

    private void rebuild() {
        clearChildren(); row = 0;
        if (page == 0) buildMain(); else if (page == 1) buildTargets(); else buildTiming();
        addDrawableChild(ButtonWidget.builder(Text.literal("← Back"), b -> { if (page == 0) close(); else { page--; rebuild(); } }).dimensions(width/2-150, height-35, 90, 22).build());
        if (page < 2) addDrawableChild(ButtonWidget.builder(Text.literal("Next →"), b -> { page++; rebuild(); }).dimensions(width/2+60, height-35, 90, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Save"), b -> { TriggerBotProClient.CONFIG.save(); close(); }).dimensions(width/2-45, height-35, 90, 22).build());
    }

    private int y() { return 55 + row++ * 28; }
    private void addToggle(String name, java.util.function.BooleanSupplier get, java.util.function.Consumer<Boolean> set) {
        int y=y(); addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + (get.getAsBoolean()?"ON":"OFF")), b -> { set.accept(!get.getAsBoolean()); rebuild(); }).dimensions(width/2-155,y,310,22).build());
    }
    private void addCycle(String name, String[] values, java.util.function.IntSupplier get, java.util.function.IntConsumer set) {
        int y=y(); addDrawableChild(ButtonWidget.builder(Text.literal(name + ": " + values[get.getAsInt()]), b -> { set.accept((get.getAsInt()+1)%values.length); rebuild(); }).dimensions(width/2-155,y,310,22).build());
    }

    private void buildMain() {
        TriggerConfig c=TriggerBotProClient.CONFIG;
        addToggle("Enabled", ()->c.enabled, v->c.enabled=v);
        addToggle("Require attack key", ()->c.requireAttackKey, v->c.requireAttackKey=v);
        addToggle("Line of sight", ()->c.requireLineOfSight, v->c.requireLineOfSight=v);
        addToggle("Critical-only", ()->c.criticalOnly, v->c.criticalOnly=v);
        addToggle("Ignore invisible", ()->c.ignoreInvisible, v->c.ignoreInvisible=v);
        addCycle("Preset", new String[]{"Legit","Balanced","Fast","Crits"}, ()->presetIndex, v->{ presetIndex=v; String[] p={"Legit","Balanced","Fast","Crits"}; c.resetTo(p[v]); });
    }

    private void buildTargets() {
        TriggerConfig c=TriggerBotProClient.CONFIG;
        addToggle("Players", ()->c.players, v->c.players=v);
        addToggle("Hostile mobs", ()->c.hostile, v->c.hostile=v);
        addToggle("Passive mobs", ()->c.passive, v->c.passive=v);
        addToggle("Neutral mobs", ()->c.neutral, v->c.neutral=v);
        addToggle("Ignore dead", ()->c.ignoreDead, v->c.ignoreDead=v);
        addToggle("Ignore creative players", ()->c.ignoreCreativePlayers, v->c.ignoreCreativePlayers=v);
        addRange("Range", c.maxRange, 2.0, 6.0, v->c.maxRange=v);
    }

    private void buildTiming() {
        TriggerConfig c=TriggerBotProClient.CONFIG;
        addRange("Min CPS", c.minCps, 1, 20, v->c.minCps=Math.min(v,c.maxCps));
        addRange("Max CPS", c.maxCps, 1, 20, v->c.maxCps=Math.max(v,c.minCps));
        addToggle("Random reaction", ()->c.randomizeReaction, v->c.randomizeReaction=v);
        addRangeInt("Min reaction (ms)", c.reactionMinMs, 0, 200, v->c.reactionMinMs=Math.min(v,c.reactionMaxMs));
        addRangeInt("Max reaction (ms)", c.reactionMaxMs, 0, 200, v->c.reactionMaxMs=Math.max(v,c.reactionMinMs));
        addToggle("Cooldown check", ()->c.attackOnlyIfCooldownReady, v->c.attackOnlyIfCooldownReady=v);
        addToggle("Swing hand", ()->c.swingHand, v->c.swingHand=v);
    }

    private void addRange(String name,double value,double min,double max,java.util.function.DoubleConsumer set){
        int y=y(); addDrawableChild(ButtonWidget.builder(Text.literal(String.format("%s: %.1f",name,value)), b->{ double step=(max-min)/10.0; double n=value+step; if(n>max)n=min; set.accept(Math.round(n*10)/10.0); rebuild(); }).dimensions(width/2-155,y,310,22).build());
    }
    private void addRangeInt(String name,int value,int min,int max,java.util.function.IntConsumer set){
        int y=y(); addDrawableChild(ButtonWidget.builder(Text.literal(name+": "+value), b->{ int n=value+10; if(n>max)n=min; set.accept(n); rebuild(); }).dimensions(width/2-155,y,310,22).build());
    }

    @Override public void render(DrawContext ctx,int mouseX,int mouseY,float delta){
        renderBackground(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal("TriggerBot Pro"), width/2, 18, 0xFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, Text.literal(page==0?"General":page==1?"Targets":"Timing"), width/2, 36, 0xAAAAAA);
        super.render(ctx,mouseX,mouseY,delta);
    }

    @Override public void close(){ TriggerBotProClient.CONFIG.save(); client.setScreen(parent); }
}
