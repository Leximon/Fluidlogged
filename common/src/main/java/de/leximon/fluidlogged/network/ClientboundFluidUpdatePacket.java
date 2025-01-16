package de.leximon.fluidlogged.network;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public record ClientboundFluidUpdatePacket(
        BlockPos pos,
        FluidState state
) implements CustomPacketPayload {

    public static final ResourceLocation ID = Fluidlogged.id("fluid_update");
    public static final Type<ClientboundFluidUpdatePacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ClientboundFluidUpdatePacket> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC,
      ClientboundFluidUpdatePacket::pos,
      ByteBufCodecs.idMapper(Fluid.FLUID_STATE_REGISTRY),
      ClientboundFluidUpdatePacket::state,
      ClientboundFluidUpdatePacket::new);

    public static void apply(ClientboundFluidUpdatePacket pkt) {
        ClientPacketHandler.handleFluidUpdate(pkt.pos, pkt.state);
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
