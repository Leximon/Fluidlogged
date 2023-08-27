package de.leximon.fluidlogged;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class FluidloggedCommon {

    public static final String MOD_ID = "fluidlogged";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static boolean hasDifferentLightEmission(FluidState prevFluidState, FluidState newFluidState) {
        return prevFluidState.createLegacyBlock().getLightEmission() != newFluidState.createLegacyBlock().getLightEmission();
    }

    public static FriendlyByteBuf createByteBuf() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static int getFluidId(@Nullable FluidState blockState) {
        if (blockState == null) {
            return 0;
        } else {
            int i = Fluid.FLUID_STATE_REGISTRY.getId(blockState);
            return i == -1 ? 0 : i;
        }
    }
}
