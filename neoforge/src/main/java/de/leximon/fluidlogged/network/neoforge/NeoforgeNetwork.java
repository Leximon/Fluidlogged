package de.leximon.fluidlogged.network.neoforge;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.network.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.ClientboundSectionFluidsUpdatePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Fluidlogged.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class NeoforgeNetwork {
    @SubscribeEvent
    public static void registerPacketPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        
        registrar.playToClient(
          ClientboundFluidUpdatePacket.PACKET_TYPE,
          ClientboundFluidUpdatePacket.STREAM_CODEC,
          (packet, context) -> ClientboundFluidUpdatePacket.apply(packet)
        );
        
        registrar.playToClient(
          ClientboundSectionFluidsUpdatePacket.PACKET_TYPE,
          ClientboundSectionFluidsUpdatePacket.STREAM_CODEC,
          (packet, context) -> ClientboundSectionFluidsUpdatePacket.apply(packet)
        );
    }
}
