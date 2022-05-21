package de.leximon.fluidlogged;

import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.core.FluidProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;

@Mod(Fluidlogged.MOD_ID)
public class Fluidlogged
{
    public static final String MOD_ID = "fluidlogged";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final HashSet<Class<? extends Block>> VANILLA_WATERLOGGABLES = new HashSet<>();
    public static final FluidProperty PROPERTY_FLUID = FluidProperty.Create("fluidlogged");

    public static final HashMap<Fluid, LiquidBlock> fluidBlocks = new HashMap<>();
    public Fluidlogged()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FluidloggedConfig.CONFIG_SPEC);
    }

    /**
     * @return the fluid of the block state by its property
     */
    public static Fluid getFluid(BlockState state) {
        if(state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED))
            return Fluids.WATER;
        if (!state.hasProperty(Fluidlogged.PROPERTY_FLUID))
            return null;
        int index = state.getValue(Fluidlogged.PROPERTY_FLUID) - 1;
        if(index < 0)
            return Fluids.EMPTY;
        if (index >= FluidloggedConfig.getFluidList().size())
            return null;
        ResourceLocation id = ResourceLocation.tryParse(FluidloggedConfig.getFluidList().get(index));
        if (id == null)
            return null;
        return ForgeRegistries.FLUIDS.getValue(id);
    }

    /**
     * @return the index of the fluid in the config
     */
    public static int getFluidIndex(Fluid fluid) {
        if(fluid.equals(Fluids.EMPTY))
            return 0;
        ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
        return FluidloggedConfig.getFluidList().indexOf(id.toString()) + 1;
    }

    public static boolean isVanillaWaterloggable(Object block) {
        return VANILLA_WATERLOGGABLES.contains(block.getClass());
    }

    static
    { // pain
        VANILLA_WATERLOGGABLES.add(PoweredRailBlock.class);
        VANILLA_WATERLOGGABLES.add(DetectorRailBlock.class);
        VANILLA_WATERLOGGABLES.add(StairBlock.class);
        VANILLA_WATERLOGGABLES.add(ChestBlock.class);
        VANILLA_WATERLOGGABLES.add(SignBlock.class);
        VANILLA_WATERLOGGABLES.add(LadderBlock.class);
        VANILLA_WATERLOGGABLES.add(RailBlock.class);
        VANILLA_WATERLOGGABLES.add(WallSignBlock.class);
        VANILLA_WATERLOGGABLES.add(FenceBlock.class);
        VANILLA_WATERLOGGABLES.add(TrapDoorBlock.class);
        VANILLA_WATERLOGGABLES.add(IronBarsBlock.class);
        VANILLA_WATERLOGGABLES.add(ChainBlock.class);
        VANILLA_WATERLOGGABLES.add(GlowLichenBlock.class);
        VANILLA_WATERLOGGABLES.add(EnderChestBlock.class);
        VANILLA_WATERLOGGABLES.add(WallBlock.class);
        VANILLA_WATERLOGGABLES.add(TrappedChestBlock.class);
        VANILLA_WATERLOGGABLES.add(StainedGlassPaneBlock.class);
        VANILLA_WATERLOGGABLES.add(LightBlock.class);
        VANILLA_WATERLOGGABLES.add(SlabBlock.class);
        VANILLA_WATERLOGGABLES.add(BaseCoralPlantBlock.class);
        VANILLA_WATERLOGGABLES.add(CoralBlock.class);
        VANILLA_WATERLOGGABLES.add(BaseCoralFanBlock.class);
        VANILLA_WATERLOGGABLES.add(CoralFanBlock.class);
        VANILLA_WATERLOGGABLES.add(BaseCoralWallFanBlock.class);
        VANILLA_WATERLOGGABLES.add(CoralWallFanBlock.class);
        VANILLA_WATERLOGGABLES.add(SeaPickleBlock.class);
        VANILLA_WATERLOGGABLES.add(ConduitBlock.class);
        VANILLA_WATERLOGGABLES.add(ScaffoldingBlock.class);
        VANILLA_WATERLOGGABLES.add(LanternBlock.class);
        VANILLA_WATERLOGGABLES.add(CampfireBlock.class);
        VANILLA_WATERLOGGABLES.add(CandleBlock.class);
        VANILLA_WATERLOGGABLES.add(AmethystClusterBlock.class);
        VANILLA_WATERLOGGABLES.add(SculkSensorBlock.class);
        VANILLA_WATERLOGGABLES.add(WeatheringCopperStairBlock.class);
        VANILLA_WATERLOGGABLES.add(WeatheringCopperSlabBlock.class);
        VANILLA_WATERLOGGABLES.add(LightningRodBlock.class);
        VANILLA_WATERLOGGABLES.add(PointedDripstoneBlock.class);
        VANILLA_WATERLOGGABLES.add(BigDripleafBlock.class);
        VANILLA_WATERLOGGABLES.add(BigDripleafStemBlock.class);
        VANILLA_WATERLOGGABLES.add(SmallDripleafBlock.class);
        VANILLA_WATERLOGGABLES.add(HangingRootsBlock.class);
    }
}
