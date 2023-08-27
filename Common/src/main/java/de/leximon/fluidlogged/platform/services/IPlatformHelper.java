package de.leximon.fluidlogged.platform.services;

import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public interface IPlatformHelper {

    void broadcastFluidUpdatePacket(List<ServerPlayer> players, BlockPos pos, FluidState state);

    void broadcastSectionFluidsUpdatePacket(List<ServerPlayer> players, SectionPos sectionPos, ShortSet changedFluids, LevelChunkSection levelChunkSection);
}
