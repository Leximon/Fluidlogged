package de.leximon.fluidlogged;

import de.leximon.fluidlogged.core.FluidProperty;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class Fluidlogged implements ModInitializer {

	public static final String MOD_ID = "fluidlogged";
	public static final Logger LOGGER = LoggerFactory.getLogger("fluidlogged");

	public static final ArrayList<Class<? extends Block>> VANILLA_WATERLOGGABLES = new ArrayList<>(40);
	public static final FluidProperty PROPERTY_FLUID = FluidProperty.of("fluidlogged");

	public static final HashMap<Fluid, FluidBlock> fluidBlocks = new HashMap<>();

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
		if(state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED))
			return Fluids.WATER;
		if (!state.contains(Fluidlogged.PROPERTY_FLUID))
			return null;
		int index = state.get(Fluidlogged.PROPERTY_FLUID) - 1;
		if(index < 0)
			return Fluids.EMPTY;
		if (index >= FluidloggedConfig.fluidsLocked.size())
			return null;
		Identifier id = FluidloggedConfig.fluidsLocked.get(index);
		if (id == null)
			return null;
		return Registry.FLUID.get(id);
	}

	/**
	 * @return the index of the fluid in the config
	 */
	public static int getFluidIndex(Fluid fluid) {
		if(fluid.equals(Fluids.EMPTY))
			return 0;
		return FluidloggedConfig.fluidsLocked.indexOf(Registry.FLUID.getId(fluid)) + 1;
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
		VANILLA_WATERLOGGABLES.add(StairsBlock.class);
		VANILLA_WATERLOGGABLES.add(RailBlock.class);
		VANILLA_WATERLOGGABLES.add(SmallDripleafBlock.class);
		VANILLA_WATERLOGGABLES.add(CandleBlock.class);
		VANILLA_WATERLOGGABLES.add(CoralFanBlock.class);
		VANILLA_WATERLOGGABLES.add(OxidizableStairsBlock.class);
		VANILLA_WATERLOGGABLES.add(DeadCoralFanBlock.class);
		VANILLA_WATERLOGGABLES.add(WallBlock.class);
		VANILLA_WATERLOGGABLES.add(LanternBlock.class);
		VANILLA_WATERLOGGABLES.add(DeadCoralWallFanBlock.class);
		VANILLA_WATERLOGGABLES.add(SlabBlock.class);
		VANILLA_WATERLOGGABLES.add(WallSignBlock.class);
		VANILLA_WATERLOGGABLES.add(DetectorRailBlock.class);
		VANILLA_WATERLOGGABLES.add(TrapdoorBlock.class);
		VANILLA_WATERLOGGABLES.add(LeavesBlock.class);
		VANILLA_WATERLOGGABLES.add(OxidizableSlabBlock.class);
		VANILLA_WATERLOGGABLES.add(PointedDripstoneBlock.class);
		VANILLA_WATERLOGGABLES.add(AmethystClusterBlock.class);
		VANILLA_WATERLOGGABLES.add(CoralWallFanBlock.class);
		VANILLA_WATERLOGGABLES.add(PaneBlock.class);
		VANILLA_WATERLOGGABLES.add(DeadCoralBlock.class);
		VANILLA_WATERLOGGABLES.add(CoralBlock.class);
		VANILLA_WATERLOGGABLES.add(SignBlock.class);
		VANILLA_WATERLOGGABLES.add(GlowLichenBlock.class);
		VANILLA_WATERLOGGABLES.add(FenceBlock.class);
		VANILLA_WATERLOGGABLES.add(HangingRootsBlock.class);
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
	}
}
