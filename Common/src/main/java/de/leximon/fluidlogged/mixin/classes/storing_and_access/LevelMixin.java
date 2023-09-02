package de.leximon.fluidlogged.mixin.classes.storing_and_access;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.mixin.extensions.LevelChunkExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Default implementations are in {@link de.leximon.fluidlogged.mixin.extensions.LevelExtension}
 */
@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor, AutoCloseable, LevelExtension {

    @Shadow public abstract LevelChunk getChunkAt(BlockPos blockPos);

    @Override
    public boolean setFluid(BlockPos blockPos, FluidState fluidState, int flags, int maxUpdateDepth) {
        Level $this = (Level) (Object) this;
        
        if ((flags & Block.UPDATE_MOVE_BY_PISTON) != 0)
            throw new IllegalArgumentException("Flag UPDATE_MOVE_BY_PISTON (0x40) is not permitted for fluid state updates");

        if ($this.isOutsideBuildHeight(blockPos))
            return false;

        if (!$this.isClientSide && $this.isDebug())
            return false;


        LevelChunk levelChunk = $this.getChunkAt(blockPos);
        FluidState prevFluidState = ((LevelChunkExtension) levelChunk).setFluidState(blockPos, fluidState);

        if (prevFluidState == null)
            return false;


        if (prevFluidState != fluidState)
            fluidlogged$setBlocksDirty(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());

        if ((flags & Block.UPDATE_CLIENTS) != 0
                && (!$this.isClientSide || (flags & Block.UPDATE_INVISIBLE) == 0)
                && ($this.isClientSide || levelChunk.getFullStatus() != null && levelChunk.getFullStatus().isOrAfter(FullChunkStatus.BLOCK_TICKING))) {
            this.sendFluidUpdated(blockPos, flags);
        }

        BlockState blockState = $this.getBlockState(blockPos);

        if ((flags & Block.UPDATE_NEIGHBORS) != 0) {
            $this.blockUpdated(blockPos, blockState.getBlock());
            if (!$this.isClientSide && blockState.hasAnalogOutputSignal())
                $this.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
        }

        if ((flags & Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK) != 0) {
            Fluid fluid = fluidState.getType();
            scheduleTick(blockPos, fluid, fluid.getTickDelay($this));
        }

        if ((flags & Block.UPDATE_KNOWN_SHAPE) == 0 && maxUpdateDepth > 0) {
            int updateNeighbourFlags = flags & -34;
            blockState.updateIndirectNeighbourShapes($this, blockPos, updateNeighbourFlags, maxUpdateDepth - 1); // TODO: might be handled wrong
            blockState.updateNeighbourShapes($this, blockPos, updateNeighbourFlags, maxUpdateDepth - 1);
            blockState.updateIndirectNeighbourShapes($this, blockPos, updateNeighbourFlags, maxUpdateDepth - 1);
        }

        return true;
    }

    @Override
    public boolean setBlockAndInsertFluidIfPossible(BlockPos blockPos, BlockState blockState, int flags) {
        FluidState fluidState = getFluidState(blockPos);

        boolean success = setBlock(blockPos, blockState, flags);

        if (blockState.hasProperty(BlockStateProperties.WATERLOGGED) && blockState.getValue(BlockStateProperties.WATERLOGGED))
            return success;

        if (success && Fluidlogged.canPlaceFluid(this, blockPos, blockState, fluidState.getType()))
            setFluid(blockPos, fluidState, flags | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);

        return success;
    }
}
