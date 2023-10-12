package de.leximon.fluidlogged.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EmbeddedObsidian extends BaseEntityBlock {

    public EmbeddedObsidian(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new EmbeddedBlockEntity(blockPos, blockState);
    }


    @Override
    public void playerDestroy(Level level, Player player, BlockPos blockPos, BlockState blockState, @Nullable BlockEntity blockEntity, ItemStack itemStack) {
        if (!(blockEntity instanceof EmbeddedBlockEntity embeddedBlockEntity)) {
            super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
            return;
        }

        BlockState replacementBlock = embeddedBlockEntity.getContent();
        if (replacementBlock == null) {
            super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
            return;
        }

        level.setBlock(blockPos, replacementBlock, Block.UPDATE_ALL);

        super.playerDestroy(level, player, blockPos, blockState, blockEntity, itemStack);
    }
}
