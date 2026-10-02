package com.emperor.triggerbot.client;

import com.emperor.triggerbot.config.TriggerConfig;
import com.emperor.triggerbot.screen.TriggerConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.SwordItem;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ThreadLocalRandom;

public final class TriggerBotProClient implements ClientModInitializer {
    public static TriggerConfig CONFIG;
    private static KeyBinding openKey;
    private static KeyBinding toggleKey;
    private static KeyBinding cycleKey;
    private static long reactionAt;
    private static long nextAttackAt;
    private static boolean pending;
    private static Entity currentTarget;
    private static Entity lastTarget;
    private static int hitCount;
    private static boolean critWindowOpen;
    private static boolean singleplayerLocked;

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
        cycleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.triggerbotpro.cycle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H,
                "key.categories.triggerbotpro"));

        ClientTickEvents.END_CLIENT_TICK.register(TriggerBotProClient::tick);
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> renderHud(drawContext));
    }

    private static void tick(MinecraftClient client) {
        while (openKey.wasPressed()) client.setScreen(new TriggerConfigScreen(client.currentScreen));
        while (toggleKey.wasPressed()) toggle();
        while (cycleKey.wasPressed()) cyclePreset(client);

        currentTarget = null;
        critWindowOpen = false;
        singleplayerLocked = false;
        if (client.player == null || client.world == null || client.interactionManager == null) return;

        // Sadece tek oyunculu dünyada çalışır (LAN'a açılmış dünya da kilitli sayılır).
        if (!client.isInSingleplayer()) { singleplayerLocked = true; resetState(); return; }

        if (!CONFIG.enabled || (CONFIG.requireAttackKey && !client.options.attackKey.isPressed())) { resetState(); return; }
        if (client.currentScreen != null) { resetState(); return; }
        if (CONFIG.pauseWhileUsingItem && client.player.isUsingItem()) { resetState(); return; }
        if (!CONFIG.allowSprintAttack && client.player.isSprinting()) { resetState(); return; }
        if (CONFIG.weaponOnly && !isWeapon(client.player)) { resetState(); return; }

        Entity target = client.crosshairTarget instanceof EntityHitResult hit ? hit.getEntity() : null;
        if (!validTarget(client, target)) { lastTarget = null; resetState(); return; }
        if (target != lastTarget) { lastTarget = target; resetState(); } // yeni hedef: tepki süresi baştan
        currentTarget = target;

        // Kritik modu: pencere dışındayken hiç vurma, pencere açılınca vur.
        boolean critMode = CONFIG.criticalOnly;
        if (critMode) {
            critWindowOpen = isCriticalWindow(client.player);
            if (!critWindowOpen) { pending = false; return; }
        }

        // Kritik vuruş için Minecraft bekleme çubuğunun >%90 olmasını ister; güvenli tarafta kal.
        double needed = CONFIG.cooldownThreshold;
        if (critMode) needed = Math.max(needed, 0.95);
        if ((CONFIG.attackOnlyIfCooldownReady || critMode) && client.player.getAttackCooldownProgress(0.0f) < needed) return;

        long now = System.currentTimeMillis();
        boolean instant = critMode && CONFIG.critInstant;
        if (!instant) {
            if (!pending) {
                reactionAt = now + randomReaction();
                pending = true;
            }
            if (now < reactionAt || now < nextAttackAt) return;
        }

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

    /**
     * Minecraft'ın kritik vuruş koşulları: düşüyor olmak (zıpladıktan sonra tepeyi geçmiş),
     * yerde/tırmanırken/suda/araçta/körlükte olmamak ve KOŞMAMAK. Koşarken vurursan kritik olmaz.
     */
    private static boolean isCriticalWindow(PlayerEntity player) {
        return player.fallDistance > 0.0f
                && player.getVelocity().y < 0.0
                && !player.isOnGround()
                && !player.isClimbing()
                && !player.isTouchingWater()
                && !player.isSprinting()
                && !player.hasVehicle()
                && !player.hasStatusEffect(StatusEffects.BLINDNESS)
                && !player.getAbilities().flying;
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

    private static void cyclePreset(MinecraftClient client) {
        TriggerConfig.Preset[] all = TriggerConfig.Preset.values();
        TriggerConfig.Preset current = CONFIG.currentPreset();
        TriggerConfig.Preset next = all[current == null ? 0 : (current.ordinal() + 1) % all.length];
        CONFIG.applyPreset(next);
        resetState();
        if (client.inGameHud != null) {
            client.inGameHud.setOverlayMessage(Text.literal("TriggerBot Pro • Profil: " + next.label()), false);
        }
    }

    public static Entity getCurrentTarget() { return currentTarget; }
    public static int getHitCount() { return hitCount; }

    private static void renderHud(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (!CONFIG.hudEnabled || client.player == null || client.currentScreen != null) return;

        int x = 8;
        int y = 8;
        int width = 190;
        int height = (CONFIG.hudShowTarget ? 66 : 24);
        int stateColor = singleplayerLocked ? 0xFFF59E0B : (CONFIG.enabled ? 0xFF4ADE80 : 0xFFEF4444);
        String state = singleplayerLocked ? "KİLİTLİ" : (CONFIG.enabled ? "AKTİF" : "PASİF");
        ctx.fill(x, y, x + width, y + height, 0xCC101419);
        ctx.fill(x, y, x + 3, y + height, stateColor);
        ctx.drawText(client.textRenderer, "TriggerBot Pro", x + 10, y + 5, 0xFFFFFFFF, true);
        ctx.drawText(client.textRenderer, state, x + 118, y + 5, stateColor, true);

        if (CONFIG.hudShowTarget) {
            String target = currentTarget == null ? "Hedef: —" : "Hedef: " + currentTarget.getDisplayName().getString();
            ctx.drawText(client.textRenderer, target, x + 10, y + 20, 0xFFD1D5DB, false);
            if (CONFIG.hudShowRange) {
                String distance = currentTarget == null ? "Mesafe: —" : String.format(java.util.Locale.US, "Mesafe: %.1fm", client.player.distanceTo(currentTarget));
                ctx.drawText(client.textRenderer, distance, x + 10, y + 35, 0xFF9CA3AF, false);
            }
            ctx.drawText(client.textRenderer, "Vuruş: " + hitCount, x + 106, y + 35, 0xFF9CA3AF, false);

            String profile = singleplayerLocked ? "Sadece tek oyunculuda çalışır" : "Profil: " + CONFIG.presetLabel();
            ctx.drawText(client.textRenderer, profile, x + 10, y + 50, 0xFF9CA3AF, false);
            if (CONFIG.criticalOnly && !singleplayerLocked) {
                ctx.drawText(client.textRenderer, critWindowOpen ? "KRİTİK" : "bekle", x + 134, y + 50,
                        critWindowOpen ? 0xFF4ADE80 : 0xFF6B7280, true);
            }
        }
    }
}
