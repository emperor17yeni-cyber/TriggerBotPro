package com.versionbridge;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class VersionBridgeModInitializer implements ModInitializer {

    public static final String MOD_ID = "versionbridge";

    @Override
    public void onInitialize() {
        VersionConfig.load();
        registerCommands();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(net.minecraft.server.command.CommandManager.literal("version")
                .executes(ctx -> versionCommand(ctx.getSource()))
                .then(net.minecraft.server.command.CommandManager.literal("list")
                    .executes(ctx -> versionListCommand(ctx.getSource())))
                .then(net.minecraft.server.command.CommandManager.literal("set")
                    .then(net.minecraft.server.command.CommandManager.argument("version", StringArgumentType.string())
                        .executes(ctx -> versionSetCommand(ctx.getSource(), StringArgumentType.getString(ctx, "version")))))
                .then(net.minecraft.server.command.CommandManager.literal("off")
                    .executes(ctx -> versionOffCommand(ctx.getSource())))
            );
        });
    }

    private int versionCommand(ServerCommandSource source) {
        String current = VersionConfig.getTargetVersion();
        source.sendFeedback(
            () -> Text.literal("§6VersionBridge: §r§nCurrent running version: §b1.21"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§6Target version: §r" + (current != null ? current : "§cNone (disabled)")),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§7Use /version list to see supported versions."),
            false
        );
        return Command.SINGLE_SUCCESS;
    }

    private int versionListCommand(ServerCommandSource source) {
        source.sendFeedback(
            () -> Text.literal("§6Supported target versions:"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§b• 1.21§r (native, recommended)"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§b• 1.21.1§r (partial protocol support)"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§b• 1.21.4§r (partial protocol support)"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§b• 1.21.11§r (partial protocol support)"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§b• 1.26.3§r (partial protocol support)"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§6§l⚠ Limitations:"),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§7Version selection only applies to server connection attempts."),
            false
        );
        source.sendFeedback(
            () -> Text.literal("§7Full protocol translation is not implemented. Use /version off for 1.21 servers."),
            false
        );
        return Command.SINGLE_SUCCESS;
    }

    private int versionSetCommand(ServerCommandSource source, String version) {
        if (version.equals("1.21") || version.equals("1.21.1") || version.equals("1.21.4") || 
            version.equals("1.21.11") || version.equals("1.26.3")) {
            VersionConfig.setTargetVersion(version);
            VersionConfig.save();
            source.sendFeedback(
                () -> Text.literal("§6VersionBridge: §aTarget version set to §b" + version),
                true
            );
            source.sendFeedback(
                () -> Text.literal("§7Note: Reconnect to a server for changes to take effect."),
                false
            );
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendError(Text.literal("§6VersionBridge: §cUnsupported version '" + version + "'. Use /version list."));
            return 0;
        }
    }

    private int versionOffCommand(ServerCommandSource source) {
        VersionConfig.setTargetVersion(null);
        VersionConfig.save();
        source.sendFeedback(
            () -> Text.literal("§6VersionBridge: §aVersion selection disabled. Running native 1.21."),
            true
        );
        return Command.SINGLE_SUCCESS;
    }
}
