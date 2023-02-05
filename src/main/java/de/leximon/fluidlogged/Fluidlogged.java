package de.leximon.fluidlogged;

import de.leximon.fluidlogged.core.FluidProperty;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.BaseCoralFanBlock;
import net.minecraft.world.level.block.BaseCoralPlantBlock;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.BigDripleafBlock;
import net.minecraft.world.level.block.BigDripleafStemBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.CoralFanBlock;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.CoralWallFanBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.GlowLichenBlock;
import net.minecraft.world.level.block.HangingRootsBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.MangroveLeavesBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.MangroveRootsBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkVeinBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SmallDripleafBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.TrappedChestBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WeatheringCopperSlabBlock;
import net.minecraft.world.level.block.WeatheringCopperStairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class Fluidlogged implements ModInitializer {

	public static final String MOD_ID = "fluidlogged";
	public static final Logger LOGGER = LoggerFactory.getLogger("fluidlogged");

	static {
		FluidloggedConfig.init();
	}

	public static final ArrayList<Class<? extends Block>> VANILLA_WATERLOGGABLES = new ArrayList<>(41);
	public static final FluidProperty PROPERTY_FLUID = FluidProperty.of("fluidlogged");

	public static final HashMap<Fluid, LiquidBlock> fluidBlocks = new HashMap<>();

	@Override
	public void onInitialize() {
//		// Prints all vanilla waterloggables
//		ArrayList<Class<? extends Block>> blocks = new ArrayList<>();
//		for (Map.Entry<RegistryKey<Block>, Block> entry : Registry.BLOCK.getEntrySet()) {
//			Block block = entry.getValue();
//			if(block.getDefaultState().contains(Properties.WATERLOGGED) && !blocks.contains(block.getClass())) {
//				System.out.println(block.getClass().getSimpleName());
//				blocks.add(block.getClass());
//			}
//		}
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
		if (index >= FluidloggedConfig.fluidsLocked.size())
			return null;
		ResourceLocation id = FluidloggedConfig.fluidsLocked.get(index);
		if (id == null)
			return null;
		return BuiltInRegistries.FLUID.get(id);
	}

	/**
	 * @return the index of the fluid in the config
	 */
	public static int getFluidIndex(Fluid fluid) {
		if(fluid.equals(Fluids.EMPTY))
			return 0;
		return FluidloggedConfig.fluidsLocked.indexOf(BuiltInRegistries.FLUID.getKey(fluid)) + 1;
	}

	public static boolean isVanillaWaterloggable(Object block) {
		Class<?> blockClazz = block.getClass();
		for (Class<? extends Block> clazz : VANILLA_WATERLOGGABLES) {
			if (clazz.isAssignableFrom(blockClazz))
				return true;
		}
		return false;
	}

	static { // pain
		VANILLA_WATERLOGGABLES.add(StainedGlassPaneBlock.class);
		VANILLA_WATERLOGGABLES.add(LadderBlock.class);
		VANILLA_WATERLOGGABLES.add(StairBlock.class);
		VANILLA_WATERLOGGABLES.add(RailBlock.class);
		VANILLA_WATERLOGGABLES.add(SmallDripleafBlock.class);
		VANILLA_WATERLOGGABLES.add(CandleBlock.class);
		VANILLA_WATERLOGGABLES.add(CoralFanBlock.class);
		VANILLA_WATERLOGGABLES.add(WeatheringCopperStairBlock.class);
		VANILLA_WATERLOGGABLES.add(BaseCoralFanBlock.class);
		VANILLA_WATERLOGGABLES.add(WallBlock.class);
		VANILLA_WATERLOGGABLES.add(LanternBlock.class);
		VANILLA_WATERLOGGABLES.add(BaseCoralWallFanBlock.class);
		VANILLA_WATERLOGGABLES.add(SlabBlock.class);
		VANILLA_WATERLOGGABLES.add(WallSignBlock.class);
		VANILLA_WATERLOGGABLES.add(DetectorRailBlock.class);
		VANILLA_WATERLOGGABLES.add(SculkVeinBlock.class);
		VANILLA_WATERLOGGABLES.add(TrapDoorBlock.class);
		VANILLA_WATERLOGGABLES.add(LeavesBlock.class);
		VANILLA_WATERLOGGABLES.add(WeatheringCopperSlabBlock.class);
		VANILLA_WATERLOGGABLES.add(PointedDripstoneBlock.class);
		VANILLA_WATERLOGGABLES.add(AmethystClusterBlock.class);
		VANILLA_WATERLOGGABLES.add(SculkShriekerBlock.class);
		VANILLA_WATERLOGGABLES.add(CoralWallFanBlock.class);
		VANILLA_WATERLOGGABLES.add(IronBarsBlock.class);
		VANILLA_WATERLOGGABLES.add(BaseCoralPlantBlock.class);
		VANILLA_WATERLOGGABLES.add(CoralPlantBlock.class);
		VANILLA_WATERLOGGABLES.add(StandingSignBlock.class);
		VANILLA_WATERLOGGABLES.add(GlowLichenBlock.class);
		VANILLA_WATERLOGGABLES.add(MangrovePropaguleBlock.class);
		VANILLA_WATERLOGGABLES.add(FenceBlock.class);
		VANILLA_WATERLOGGABLES.add(HangingRootsBlock.class);
		VANILLA_WATERLOGGABLES.add(MangroveLeavesBlock.class);
		VANILLA_WATERLOGGABLES.add(ScaffoldingBlock.class);
		VANILLA_WATERLOGGABLES.add(SculkSensorBlock.class);
		VANILLA_WATERLOGGABLES.add(LightningRodBlock.class);
		VANILLA_WATERLOGGABLES.add(ChestBlock.class);
		VANILLA_WATERLOGGABLES.add(BigDripleafStemBlock.class);
		VANILLA_WATERLOGGABLES.add(PoweredRailBlock.class);
		VANILLA_WATERLOGGABLES.add(ConduitBlock.class);
		VANILLA_WATERLOGGABLES.add(BigDripleafBlock.class);
		VANILLA_WATERLOGGABLES.add(EnderChestBlock.class);
		VANILLA_WATERLOGGABLES.add(SeaPickleBlock.class);
		VANILLA_WATERLOGGABLES.add(CampfireBlock.class);
		VANILLA_WATERLOGGABLES.add(TrappedChestBlock.class);
		VANILLA_WATERLOGGABLES.add(ChainBlock.class);
		VANILLA_WATERLOGGABLES.add(LightBlock.class);
		VANILLA_WATERLOGGABLES.add(MangroveRootsBlock.class);
	}
}
