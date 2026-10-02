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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public final class TriggerBotProClient implements ClientModInitializer {
    public static TriggerConfig CONFIG;
    private static KeyBinding openKey;
    private static KeyBinding toggleKey;
    private static long reactionAt;
    private static long nextAttackAt;
    private static boolean pending;
    private static Entity currentTarget;
    private static int hitCount;

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        CONFIG = TriggerConfig.load(client.runDirectory.toPath().resolve("config"));

        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.triggerbotpro.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT,
                "key.categories.triggerbotpro"));
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.triggerbotpro.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G,
                "key.categories.triggerbotpro"));

        ClientTickEvents.END_CLIENT_TICK.register(TriggerBotProClient::tick);
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(drawContext));
    }

    private static void tick(MinecraftClient client) {
        while (openKey.wasPressed()) client.setScreen(new TriggerConfigScreen(client.currentScreen));
        while (toggleKey.wasPressed()) toggle();

        currentTarget = null;
        if (client.player == null || client.world == null || client.interactionManager == null) return;

        if (!CONFIG.enabled || (CONFIG.requireAttackKey && !client.options.attackKey.isPressed())) { resetState(); return; }
        if (client.currentScreen != null) { resetState(); return; }
        if (CONFIG.pauseWhileUsingItem && client.player.isUsingItem()) { resetState(); return; }
        if (!CONFIG.allowSprintAttack && client.player.isSprinting()) { resetState(); return; }
        if (CONFIG.weaponOnly && !isWeapon(client.player)) { resetState(); return; }

        Entity target = client.crosshairTarget instanceof EntityHitResult hit ? hit.getEntity() : null;
        if (!validTarget(client, target)) { resetState(); return; }
        currentTarget = target;

        if (CONFIG.attackOnlyIfCooldownReady && client.player.getAttackCooldownProgress(0.0f) < 1.0f) return;
        if (CONFIG.criticalOnly && !isCriticalWindow(client.player)) return;

        long now = System.currentTimeMillis();
        if (!pending) {
            int delay = randomReaction();
            reactionAt = now + delay;
            pending = true;
        }
        if (now < reactionAt || now < nextAttackAt) return;

        client.interactionManager.attackEntity(client.player, target);
        if (CONFIG.swingHand) client.player.swingHand(Hand.MAIN_HAND);
        hitCount++;

        double cps = ThreadLocalRandom.current().nextDouble(CONFIG.minCps, CONFIG.maxCps + 0.0001);
        long interval = Math.max(50L, (long) (1000.0 / cps));
        if (CONFIG.jitterMs > 0) {
            interval += ThreadLocalRandom.current().nextLong(-CONFIG.jitterMs, CONFIG.jitterMs + 1);
            interval = Math.max(50L, interval);
        }
        nextAttackAt = now + interval;
        pending = false;
    }

    private static int randomReaction() {
        if (!CONFIG.randomizeReaction || CONFIG.reactionMaxMs <= CONFIG.reactionMinMs) return CONFIG.reactionMinMs;
        return ThreadLocalRandom.current().nextInt(CONFIG.reactionMinMs, CONFIG.reactionMaxMs + 1);
    }

    private static boolean isWeapon(PlayerEntity player) {
        var item = player.getMainHandStack().getItem();
        return item instanceof SwordItem || item instanceof AxeItem;
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
            return !CONFIG.ignoreCreativePlayers || !p.isCreative();
        }
        if (entity instanceof HostileEntity) return CONFIG.hostile;
        if (entity instanceof PassiveEntity) return CONFIG.passive;
        if (entity instanceof MobEntity) return CONFIG.neutral;
        return false;
    }

    private static boolean isCriticalWindow(PlayerEntity player) {
        return player.fallDistance > 0.0f
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.hasVehicle();
    }

    private static void resetState() {
        pending = false;
        reactionAt = 0;
    }

    private static void toggle() {
        CONFIG.enabled = !CONFIG.enabled;
        resetState();
        CONFIG.save();
    }

    public static Entity getCurrentTarget() { return currentTarget; }
    public static int getHitCount() { return hitCount; }

    private static void renderHud(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!CONFIG.hudEnabled || client.player == null || client.currentScreen != null) return;

        int x = 8;
        int y = 8;
        int width = 190;
        int height = (CONFIG.hudShowTarget ? 56 : 24);
        ctx.fill(x, y, x + width, y + height, 0xCC101419);
        ctx.fill(x, y, x + 3, y + height, CONFIG.enabled ? 0xFF4ADE80 : 0xFFEF4444);
        ctx.drawText(client.textRenderer, "TriggerBot Pro", x + 10, y + 5, 0xFFFFFFFF, true);
        ctx.drawText(client.textRenderer, CONFIG.enabled ? "AKTİF" : "PASİF", x + 118, y + 5,
                CONFIG.enabled ? 0xFF4ADE80 : 0xFFEF4444, true);

        if (CONFIG.hudShowTarget) {
            String target = currentTarget == null ? "Hedef: —" : "Hedef: " + currentTarget.getDisplayName().getString();
            ctx.drawText(client.textRenderer, target, x + 10, y + 20, 0xFFD1D5DB, false);
            if (CONFIG.hudShowRange) {
                String distance = currentTarget == null ? "Mesafe: —" : String.format(java.util.Locale.US, "Mesafe: %.1fm", client.player.distanceTo(currentTarget));
                ctx.drawText(client.textRenderer, distance, x + 10, y + 35, 0xFF9CA3AF, false);
            }
            ctx.drawText(client.textRenderer, "Vuruş: " + hitCount, x + 106, y + 35, 0xFF9CA3AF, false);
        }
    }
}
