package de.leximon.fluidlogged;

import de.leximon.fluidlogged.network.forge.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.forge.ClientboundSectionFluidsUpdatePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

@Mod("fluidlogged")
public class FluidloggedForge {

    public static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
            Fluidlogged.id("channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );


    public FluidloggedForge() {
        registerPackets();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClient);
    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(){
        // Register the configuration GUI factory
//        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, returnTo) ->  new ConfigScreen(returnTo)));
    }

    private void registerPackets() {
        PACKET_CHANNEL.registerMessage(
                0, ClientboundFluidUpdatePacket.class,
                ClientboundFluidUpdatePacket::write, ClientboundFluidUpdatePacket::read,
                ClientboundFluidUpdatePacket::apply,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        PACKET_CHANNEL.registerMessage(
                1, ClientboundSectionFluidsUpdatePacket.class,
                ClientboundSectionFluidsUpdatePacket::write, ClientboundSectionFluidsUpdatePacket::read,
                ClientboundSectionFluidsUpdatePacket::apply,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }
}
