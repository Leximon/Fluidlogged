package de.leximon.fluidlogged.platform.services;

import de.leximon.fluidlogged.config.ConfigDefaults;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public interface IPlatformHelper {

    Path getConfigPath();

    default File getConfigFile() {
        return getConfigPath().toFile();
    }

    ConfigDefaults getConfigDefaults();

    IdMapper<FluidState> getFluidStateIdMapper();

    void broadcastFluidUpdatePacket(List<ServerPlayer> players, BlockPos pos, FluidState state);

    void broadcastSectionFluidsUpdatePacket(List<ServerPlayer> players, SectionPos sectionPos, ShortSet changedFluids, LevelChunkSection levelChunkSection);
}
