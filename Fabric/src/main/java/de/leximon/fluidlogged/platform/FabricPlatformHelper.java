package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.network.fabric.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.fabric.ClientboundSectionFluidsUpdatePacket;
import de.leximon.fluidlogged.platform.services.IPlatformHelper;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public void broadcastFluidUpdatePacket(List<ServerPlayer> players, BlockPos pos, FluidState state) {
        ClientboundFluidUpdatePacket packet = new ClientboundFluidUpdatePacket(pos, state);
        for (ServerPlayer player : players)
            ServerPlayNetworking.send(player, packet);
    }

    @Override
    public void broadcastSectionFluidsUpdatePacket(List<ServerPlayer> players, SectionPos sectionPos, ShortSet changedFluids, LevelChunkSection levelChunkSection) {
        ClientboundSectionFluidsUpdatePacket packet = new ClientboundSectionFluidsUpdatePacket(sectionPos, changedFluids, levelChunkSection);
        for (ServerPlayer player : players)
            ServerPlayNetworking.send(player, packet);
    }

}
