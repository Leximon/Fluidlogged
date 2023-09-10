package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.config.ConfigDefaults;
import de.leximon.fluidlogged.config.FabricConfigDefaults;
import de.leximon.fluidlogged.network.fabric.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.fabric.ClientboundSectionFluidsUpdatePacket;
import de.leximon.fluidlogged.platform.services.IPlatformHelper;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.nio.file.Path;
import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(Config.CONFIG_FILE_NAME);
    private static final ConfigDefaults CONFIG_DEFAULTS = new FabricConfigDefaults();


    @Override
    public Path getConfigPath() {
        return CONFIG_PATH;
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
