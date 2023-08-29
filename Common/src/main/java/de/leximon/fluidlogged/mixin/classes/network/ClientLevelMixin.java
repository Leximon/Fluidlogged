package de.leximon.fluidlogged.mixin.classes.network;

import de.leximon.fluidlogged.mixin.extensions.ClientLevelExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements ClientLevelExtension, LevelExtension {

    @Shadow @Final private LevelRenderer levelRenderer;

    protected ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, supplier, bl, bl2, l, i);
    }

    @Override
    public void syncFluidState(BlockPos blockPos, FluidState fluidState) {
        FluidState prevFluidState = getFluidState(blockPos);
        if (prevFluidState != fluidState)
            this.setFluid(blockPos, fluidState, Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
    }

    @Override
    public void fluidlogged$setBlocksDirty(int x1, int y1, int z1, int x2, int y2, int z2) {
        levelRenderer.setBlocksDirty(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void sendFluidUpdated(BlockPos blockPos, int flags) {
        // some parameters are not used by the method itself, pass null instead
        levelRenderer.blockChanged(null, blockPos, null, null, flags);
    }
}
