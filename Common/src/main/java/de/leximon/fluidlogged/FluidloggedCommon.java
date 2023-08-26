package de.leximon.fluidlogged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;

public class FluidloggedCommon {

    public static final String MOD_ID = "fluidlogged";

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

}
