package de.leximon.fluidlogged;

import de.leximon.fluidlogged.api.FluidloggedRegistries;
import de.leximon.fluidlogged.config.Addon;
import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import de.leximon.fluidlogged.platform.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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

    public static final Config CONFIG = new Config();

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
        return CONFIG.isFluidloggable(blockState);
    }

    public static boolean isFluidloggable(BlockState blockState) {
        if (blockState.getBlock() instanceof LiquidBlockContainer)
            return true;
        return CONFIG.isFluidloggable(blockState);
    }

    public static boolean isFluidPermeable(BlockState blockState) {
        if (!CONFIG.isFluidPermeabilityEnabled())
            return false;
        return CONFIG.isFluidPermeable(blockState) || CONFIG.isShapeIndependentFluidPermeable(blockState);
    }

    public static boolean isShapeIndependentFluidPermeable(BlockState blockState) {
        if (!CONFIG.isFluidPermeabilityEnabled())
            return false;
        return CONFIG.isShapeIndependentFluidPermeable(blockState);
    }

    @ApiStatus.Internal
    public static class Internal {

        public static void initialize() {
            FluidloggedRegistries.register(
                    FluidloggedRegistries.ADDONS, id("mod_defaults"),
                    Addon.builder()
                            .enabledByDefault(true)
                            .fluidloggableBlocks(
                                    Blocks.STONECUTTER,
                                    Blocks.GRINDSTONE,
                                    Blocks.LECTERN,
                                    Blocks.BREWING_STAND,
                                    Blocks.ENCHANTING_TABLE,
                                    Blocks.BELL,
                                    Blocks.COMPOSTER,
                                    Blocks.SNIFFER_EGG,
                                    Blocks.TURTLE_EGG,
                                    Blocks.CAKE,
                                    Blocks.LEVER,
                                    Blocks.VINE,
                                    Blocks.DRAGON_EGG,
                                    Blocks.END_PORTAL_FRAME,
                                    Blocks.DAYLIGHT_DETECTOR,
                                    Blocks.HOPPER,
                                    Blocks.PISTON_HEAD,
                                    Blocks.BARRIER,
                                    Blocks.END_ROD,
                                    Blocks.END_PORTAL,
                                    Blocks.BAMBOO,
                                    Blocks.AZALEA,
                                    Blocks.MOSS_CARPET,
                                    Blocks.PLAYER_HEAD,
                                    Blocks.PLAYER_WALL_HEAD,
                                    Blocks.CREEPER_HEAD,
                                    Blocks.CREEPER_WALL_HEAD,
                                    Blocks.DRAGON_HEAD,
                                    Blocks.DRAGON_WALL_HEAD,
                                    Blocks.PIGLIN_HEAD,
                                    Blocks.PIGLIN_WALL_HEAD,
                                    Blocks.SKELETON_SKULL,
                                    Blocks.SKELETON_WALL_SKULL,
                                    Blocks.WITHER_SKELETON_SKULL,
                                    Blocks.WITHER_SKELETON_WALL_SKULL,
                                    Blocks.ZOMBIE_HEAD,
                                    Blocks.ZOMBIE_WALL_HEAD
                            )
                            .fluidloggableBlockTags(
                                    BlockTags.FENCE_GATES,
                                    BlockTags.DOORS,
                                    BlockTags.BEDS,
                                    BlockTags.PRESSURE_PLATES,
                                    BlockTags.BUTTONS,
                                    BlockTags.WOOL_CARPETS,
                                    BlockTags.BANNERS,
                                    BlockTags.FLOWER_POTS,
                                    BlockTags.CANDLE_CAKES,
                                    BlockTags.ANVIL
                            )
                            .build()
            );

            CONFIG.load();
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
