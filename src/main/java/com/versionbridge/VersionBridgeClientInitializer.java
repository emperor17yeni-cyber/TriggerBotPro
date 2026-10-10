package com.versionbridge;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class VersionBridgeClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VersionConfig.load();
        registerClientEvents();
    }

    private void registerClientEvents() {
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            VersionConfig.save();
        });
    }
}
