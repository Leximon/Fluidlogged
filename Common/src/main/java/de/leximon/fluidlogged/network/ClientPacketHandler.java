package de.leximon.fluidlogged.network;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import de.leximon.fluidlogged.mixin.extensions.ClientLevelExtension;
import de.leximon.fluidlogged.mixin.extensions.LevelExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class ClientPacketHandler {

    public static void handleFluidUpdate(BlockPos pos, FluidState state) {
        ClientLevel level = Minecraft.getInstance().level;

        ((LevelExtension) level).setFluid(
                pos, state,
                Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
        );
    }

    public static void handleSectionFluidsUpdate(SectionPos sectionPos, short[] positions, FluidState[] states) {
        ClientLevel level = Minecraft.getInstance().level;

        BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < positions.length; i++) {
            short pos = positions[i];
            mutPos.set(sectionPos.relativeToBlockX(pos), sectionPos.relativeToBlockY(pos), sectionPos.relativeToBlockZ(pos));

            ((LevelExtension) level).setFluid(
                    mutPos, states[i],
                    Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
            );
        }
    }
}
