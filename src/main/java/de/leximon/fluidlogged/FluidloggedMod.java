package de.leximon.fluidlogged;

import de.leximon.fluidlogged.core.FluidProperty;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.mixin.BucketItemAccessor;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MilkBucketItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

public class FluidloggedMod implements ModInitializer {

	public static final String MOD_ID = "fluidlogged";
	public static final Logger LOGGER = LoggerFactory.getLogger("fluidlogged");

	static {
		MidnightConfig.init(MOD_ID, FluidloggedConfig.class);
	}

	public static final FluidProperty PROPERTY_FLUID = FluidProperty.of("fluidlogged");

	@Override
	public void onInitialize() {}

	public static Fluid getFluid(BlockState state) {
		if(state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED))
			return Fluids.WATER;
		if (!state.contains(FluidloggedMod.PROPERTY_FLUID))
			return null;
		int index = state.get(FluidloggedMod.PROPERTY_FLUID) - 1;
		if(index < 0)
			return Fluids.EMPTY;
		if (index >= FluidloggedConfig.fluids.size())
			return null;
		Identifier id = Identifier.tryParse(FluidloggedConfig.fluids.get(index));
		if (id == null)
			return null;
		return Registry.FLUID.get(id);
	}

	public static int getFluidIndex(Fluid fluid) {
		if(fluid.equals(Fluids.EMPTY))
			return 0;
		Identifier id = Registry.FLUID.getId(fluid);
		return FluidloggedConfig.fluids.indexOf(id.toString()) + 1;
	}
}
