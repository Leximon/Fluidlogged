package de.leximon.fluidlogged.network.fabric;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.network.ClientPacketHandler;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record ClientboundFluidUpdatePacket(
        BlockPos pos,
        FluidState state
) implements FabricPacket {

    public static final ResourceLocation ID = Fluidlogged.id("fluid_update");
    public static final PacketType<ClientboundFluidUpdatePacket> PACKET_TYPE = PacketType.create(ID, ClientboundFluidUpdatePacket::read);



    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    public void apply(LocalPlayer localPlayer, PacketSender packetSender) {
        ClientPacketHandler.handleFluidUpdate(this.pos, this.state);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeId(Fluid.FLUID_STATE_REGISTRY, this.state);
    }

    public static ClientboundFluidUpdatePacket read(FriendlyByteBuf buf) {
        return new ClientboundFluidUpdatePacket(
                buf.readBlockPos(),
                buf.readById(Fluid.FLUID_STATE_REGISTRY)
        );
    }
}
