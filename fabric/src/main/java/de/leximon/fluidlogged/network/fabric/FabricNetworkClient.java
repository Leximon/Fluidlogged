package de.leximon.fluidlogged.network.fabric;

import de.leximon.fluidlogged.network.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.ClientboundSectionFluidsUpdatePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FabricNetworkClient {
    
    public static void register() {
        // Register receivers
        ClientPlayNetworking.registerGlobalReceiver(ClientboundFluidUpdatePacket.PACKET_TYPE, (payload, context) -> ClientboundFluidUpdatePacket.apply(payload));
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSectionFluidsUpdatePacket.PACKET_TYPE, ((payload, context) -> ClientboundSectionFluidsUpdatePacket.apply(payload)));
    }
}
