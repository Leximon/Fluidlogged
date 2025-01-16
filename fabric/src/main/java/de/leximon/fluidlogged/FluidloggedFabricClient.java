package de.leximon.fluidlogged;

import de.leximon.fluidlogged.network.fabric.FabricNetworkClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FluidloggedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FabricNetworkClient.register();
    }
}
