package de.leximon.fluidlogged.mixin.classes.network;

import de.leximon.fluidlogged.mixin.extensions.ClientLevelExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.mixin.extensions.ServerChunkCacheExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ClientLevelExtension, LevelExtension {

    @Shadow public abstract @NotNull ServerChunkCache getChunkSource();

    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Override
    public void sendFluidUpdated(BlockPos blockPos, int flags) {
        ((ServerChunkCacheExtension) this.getChunkSource()).fluidChanged(blockPos);
    }
}
