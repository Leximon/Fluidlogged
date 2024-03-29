package de.leximon.fluidlogged;

import de.leximon.fluidlogged.network.fabric.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.fabric.ClientboundSectionFluidsUpdatePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FluidloggedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ClientboundFluidUpdatePacket.PACKET_TYPE, ClientboundFluidUpdatePacket::apply);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSectionFluidsUpdatePacket.PACKET_TYPE, ClientboundSectionFluidsUpdatePacket::apply);
    }
}
