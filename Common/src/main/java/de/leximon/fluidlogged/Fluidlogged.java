package de.leximon.fluidlogged;

import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.platform.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
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

    public static int getFluidId(@Nullable FluidState fluidState) {
        if (fluidState == null) {
            return 0;
        } else {
            int i = Services.PLATFORM.getFluidStateIdMapper().getId(fluidState);
            return i == -1 ? 0 : i;
        }
    }

    public static boolean canPlaceFluid(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
        if (blockState.getBlock() instanceof LiquidBlockContainer container
                && container.canPlaceLiquid(blockGetter, blockPos, blockState, fluid))
            return true;

        return Config.isFluidloggable(blockState);
    }

    public static boolean isFluidloggable(BlockState blockState) {
        if (blockState.getBlock() instanceof LiquidBlockContainer)
            return true;
        return Config.isFluidloggable(blockState);
    }

    public static boolean isFluidPermeable(BlockState blockState) {
        if (!Config.isFluidPermeabilityEnabled())
            return false;
        return Config.isFluidPermeable(blockState) || Config.isShapeIndependentFluidPermeable(blockState);
    }

    public static boolean isShapeIndependentFluidPermeable(BlockState blockState) {
        if (!Config.isFluidPermeabilityEnabled())
            return false;
        return Config.isShapeIndependentFluidPermeable(blockState);
    }

    @ApiStatus.Internal
    public static class Internal {

        public static void initialize() {
            Config.load();
        }

        public static boolean hasDifferentLightEmission(FluidState prevFluidState, FluidState newFluidState) {
            return prevFluidState.createLegacyBlock().getLightEmission() != newFluidState.createLegacyBlock().getLightEmission();
        }

        public static BlockState handleBlockRemoval(Level instance, BlockPos blockPos, int flags, int maxUpdateDepth) {
            FluidState fluidState = instance.getFluidState(blockPos);

            ((LevelExtension) instance).setFluid(
                    blockPos,
                    Fluids.EMPTY.defaultFluidState(),
                    flags,
                    maxUpdateDepth
            );

            int replacementFluidLevel = Mth.clamp(8 - fluidState.getAmount(), 0, 8);
            return fluidState.createLegacyBlock()
                    .trySetValue(LiquidBlock.LEVEL, replacementFluidLevel);
        }

    }

}
