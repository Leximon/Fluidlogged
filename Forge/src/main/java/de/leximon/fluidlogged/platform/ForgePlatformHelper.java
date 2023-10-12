package de.leximon.fluidlogged.platform;

import de.leximon.fluidlogged.FluidloggedForge;
import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.config.ConfigDefaults;
import de.leximon.fluidlogged.config.ForgeConfigDefaults;
import de.leximon.fluidlogged.content.EmbeddedBlockEntity;
import de.leximon.fluidlogged.network.forge.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.forge.ClientboundSectionFluidsUpdatePacket;
import de.leximon.fluidlogged.platform.services.IPlatformHelper;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.IdMapper;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.nio.file.Path;
import java.util.List;

public class ForgePlatformHelper implements IPlatformHelper {

    private static final PacketDistributor<List<ServerPlayer>> PLAYER_LIST_DISTRIBUTOR = new PacketDistributor<>(
            (distributor, supplier) -> packet -> supplier.get().forEach(player -> player.connection.send(packet)),
            NetworkDirection.PLAY_TO_CLIENT
    );
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
        return FluidloggedForge.fluidStateIdMapper;
    }

    @Override
    public void broadcastFluidUpdatePacket(List<ServerPlayer> players, BlockPos pos, FluidState state) {
        FluidloggedForge.PACKET_CHANNEL.send(PLAYER_LIST_DISTRIBUTOR.with(() -> players), new ClientboundFluidUpdatePacket(pos, state));
    }

    @Override
    public void broadcastSectionFluidsUpdatePacket(List<ServerPlayer> players, SectionPos sectionPos, ShortSet changedFluids, LevelChunkSection levelChunkSection) {
        FluidloggedForge.PACKET_CHANNEL.send(PLAYER_LIST_DISTRIBUTOR.with(() -> players), new ClientboundSectionFluidsUpdatePacket(sectionPos, changedFluids, levelChunkSection));
    }

    @Override
    public BlockEntityType<EmbeddedBlockEntity> blockEntityCoatedBlock() {
        return null;
    }

}
