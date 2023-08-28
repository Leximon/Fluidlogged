package de.leximon.fluidlogged;

import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class Fluidlogged {

    public static final String MOD_ID = "fluidlogged";

    public static final int UPDATE_SCHEDULE_FLUID_TICK = 0x80;

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static int getFluidId(@Nullable FluidState blockState) {
        if (blockState == null) {
            return 0;
        } else {
            int i = Fluid.FLUID_STATE_REGISTRY.getId(blockState);
            return i == -1 ? 0 : i;
        }
    }

    public static boolean canPlaceFluid(BlockState blockState) {
        return true;
    }

    public static boolean placeFluid(LevelAccessor level, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (level.isClientSide())
            return false;

        if (!level.getFluidState(pos).isEmpty())
            return false;

        return ((LevelExtension) level).setFluid(pos, fluidState, Block.UPDATE_ALL | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);
    }

    @ApiStatus.Internal
    public static class Internal {

        public static boolean hasDifferentLightEmission(FluidState prevFluidState, FluidState newFluidState) {
            return prevFluidState.createLegacyBlock().getLightEmission() != newFluidState.createLegacyBlock().getLightEmission();
        }

        public static boolean handleBlockRemoval(Level instance, BlockPos blockPos, BlockState blockState, int flags, int maxUpdateDepth) {
            FluidState fluidState = instance.getFluidState(blockPos);

            ((LevelExtension) instance).setFluid(blockPos, Fluids.EMPTY.defaultFluidState(), flags, maxUpdateDepth);

            int replacementFluidLevel = Mth.clamp(8 - fluidState.getAmount(), 0, 8);
            BlockState replacementBlock = fluidState.createLegacyBlock()
                    .trySetValue(LiquidBlock.LEVEL, replacementFluidLevel);
            return instance.setBlock(blockPos, replacementBlock, flags, maxUpdateDepth);
        }

    }

}
