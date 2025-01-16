package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.config.ConfigDefaults;
import de.leximon.fluidlogged.config.ForgeConfigDefaults;
import de.leximon.fluidlogged.network.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.ClientboundSectionFluidsUpdatePacket;
import de.leximon.fluidlogged.platform.services.IPlatformHelper;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;
import java.util.List;

public class ForgePlatformHelper implements IPlatformHelper {
    
    private static final ConfigDefaults CONFIG_DEFAULTS = new ForgeConfigDefaults();

    @Override
    public Path getConfigPath() {
        return Path.of("config", Config.CONFIG_FILE_NAME);
    }

    @Override
    public ConfigDefaults getConfigDefaults() {
        return CONFIG_DEFAULTS;
    }

    @Override
    public IdMapper<FluidState> getFluidStateIdMapper() {
        return Fluid.FLUID_STATE_REGISTRY;
    }

    @Override
    public void broadcastFluidUpdatePacket(List<ServerPlayer> players, BlockPos pos, FluidState state) {
        ClientboundFluidUpdatePacket packet = new ClientboundFluidUpdatePacket(pos, state);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

    @Override
    public void broadcastSectionFluidsUpdatePacket(List<ServerPlayer> players, SectionPos sectionPos, ShortSet changedFluids, LevelChunkSection levelChunkSection) {
        ClientboundSectionFluidsUpdatePacket packet = new ClientboundSectionFluidsUpdatePacket(sectionPos, changedFluids, levelChunkSection);
        for (ServerPlayer player : players) {
            PacketDistributor.sendToPlayer(player, packet);
        }
    }

}
