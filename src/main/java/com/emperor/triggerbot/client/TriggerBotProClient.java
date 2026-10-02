package com.emperor.triggerbot.client;

import com.emperor.triggerbot.config.TriggerConfig;
import com.emperor.triggerbot.screen.TriggerConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public final class TriggerBotProClient implements ClientModInitializer {
    public static TriggerConfig CONFIG;
    private static KeyBinding openKey;
    private static long reactionAt;
    private static long nextAttackAt;
    private static boolean pending;

    @Override public void onInitializeClient() {
        CONFIG = TriggerConfig.load(MinecraftClient.getInstance().runDirectory.toPath().resolve("config"));
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.triggerbotpro.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "TriggerBot Pro"));
        ClientTickEvents.END_CLIENT_TICK.register(TriggerBotProClient::tick);
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> renderHud(drawContext));
    }

    private static void tick(MinecraftClient client) {
        while (openKey.wasPressed()) client.setScreen(new TriggerConfigScreen(client.currentScreen));
        if (client.player == null || client.world == null || client.interactionManager == null) return;
        if (!client.isInSingleplayer() || client.getServer() == null) { pending = false; return; }
        if (!CONFIG.enabled || (CONFIG.requireAttackKey && !client.options.attackKey.isPressed())) { pending = false; return; }
        if (client.currentScreen != null) { pending = false; return; }

        Entity target = getCrosshairTarget(client);
        if (!validTarget(client, target)) { pending = false; return; }
        if (CONFIG.attackOnlyIfCooldownReady && client.player.getAttackCooldownProgress(0.0f) < 1.0f) return;
        if (CONFIG.criticalOnly && !isCriticalWindow(client.player)) return;

        long now = System.nanoTime() / 1_000_000L;
        if (!pending) {
            int delay = CONFIG.randomizeReaction
                    ? ThreadLocalRandom.current().nextInt(Math.max(0, CONFIG.reactionMinMs), Math.max(CONFIG.reactionMinMs + 1, CONFIG.reactionMaxMs + 1))
                    : CONFIG.reactionMinMs;
            reactionAt = now + delay;
            pending = true;
        }
        if (now < reactionAt || now < nextAttackAt) return;

        client.interactionManager.attackEntity(client.player, target);
        if (CONFIG.swingHand) client.player.swingHand(Hand.MAIN_HAND);
        double cps = Math.max(1.0, Math.min(CONFIG.maxCps, CONFIG.minCps));
        if (CONFIG.maxCps > CONFIG.minCps) cps = ThreadLocalRandom.current().nextDouble(CONFIG.minCps, CONFIG.maxCps);
        nextAttackAt = now + (long)(1000.0 / cps);
        pending = false;
    }

    private static Entity getCrosshairTarget(MinecraftClient client) {
        if (client.crosshairTarget instanceof EntityHitResult hit) return hit.getEntity();
        return null;
    }

    private static boolean validTarget(MinecraftClient client, Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        PlayerEntity self = client.player;
        if (entity == self) return false;
        if (CONFIG.ignoreDead && !living.isAlive()) return false;
        if (CONFIG.ignoreInvisible && living.isInvisible()) return false;
        if (self.distanceTo(entity) > CONFIG.maxRange) return false;
        if (CONFIG.requireLineOfSight && !self.canSee(entity)) return false;
        if (entity instanceof PlayerEntity p) {
            if (!CONFIG.players) return false;
            if (CONFIG.ignoreCreativePlayers && p.isCreative()) return false;
            return true;
        }
        if (entity instanceof HostileEntity) return CONFIG.hostile;
        if (entity instanceof PassiveEntity) return CONFIG.passive;
        if (entity instanceof MobEntity) return CONFIG.neutral;
        return false;
    }

    private static boolean isCriticalWindow(PlayerEntity p) {
        return p.fallDistance > 0.0f && !p.isOnGround() && !p.isClimbing() && !p.isTouchingWater() && !p.hasVehicle();
    }

    private static void renderHud(net.minecraft.client.gui.DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen != null || !client.isInSingleplayer()) return;
        String status = CONFIG.enabled ? "TriggerBot Pro: ON" : "TriggerBot Pro: OFF";
        ctx.drawText(client.textRenderer, status, 8, 8, CONFIG.enabled ? 0x55FF55 : 0xFF5555, true);
    }
}
