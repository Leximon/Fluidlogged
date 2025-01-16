package de.leximon.fluidlogged.network.fabric;

import de.leximon.fluidlogged.network.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.ClientboundSectionFluidsUpdatePacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class FabricNetwork {

    public static void register() {
        // Register packet types
        PayloadTypeRegistry.playS2C().register(ClientboundFluidUpdatePacket.PACKET_TYPE, ClientboundFluidUpdatePacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundSectionFluidsUpdatePacket.PACKET_TYPE, ClientboundSectionFluidsUpdatePacket.STREAM_CODEC);
    }
}
