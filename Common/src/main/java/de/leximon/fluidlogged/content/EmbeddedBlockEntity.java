package de.leximon.fluidlogged.content;

import de.leximon.fluidlogged.platform.services.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmbeddedBlockEntity extends BlockEntity {

    private BlockState content = null;

    public EmbeddedBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(Services.PLATFORM.blockEntityCoatedBlock(), blockPos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (this.content != null)
            tag.put("content_state", NbtUtils.writeBlockState(this.content));
    }

    @Override
    public void load(CompoundTag tag) {
        HolderGetter<Block> holderGetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();

        this.content = NbtUtils.readBlockState(holderGetter, tag.getCompound("content_state"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    public BlockState getContent() {
        return this.content;
    }

    public void setContent(BlockState content) {
        this.content = content;

        BlockState state = getBlockState();
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS);
    }


}
