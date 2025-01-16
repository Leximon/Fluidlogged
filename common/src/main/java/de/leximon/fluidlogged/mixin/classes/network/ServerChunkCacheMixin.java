package de.leximon.fluidlogged.mixin.classes.network;

import de.leximon.fluidlogged.mixin.extensions.ChunkHolderExtension;
import de.leximon.fluidlogged.mixin.extensions.ServerChunkCacheExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerChunkCache.class)
public abstract class ServerChunkCacheMixin implements ServerChunkCacheExtension {

    @Shadow @Nullable protected abstract ChunkHolder getVisibleChunkIfPresent(long l);

    @Override
    public void fluidChanged(BlockPos blockPos) {
        int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        ChunkHolder chunkHolder = this.getVisibleChunkIfPresent(ChunkPos.asLong(chunkX, chunkZ));
        if (chunkHolder != null)
            ((ChunkHolderExtension) chunkHolder).fluidChanged(blockPos);
    }

}
