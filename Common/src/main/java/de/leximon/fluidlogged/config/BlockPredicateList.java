package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Objects;

public class BlockPredicateList {

    private List<String> blocks;
    private List<BlockPredicateEntry> blockPredicates = ImmutableList.of();
    private final Object2BooleanMap<Block> predicateResultCache = new Object2BooleanOpenHashMap<>();

    public BlockPredicateList(List<String> defaults) {
        this.blocks = defaults;
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
        this.blockPredicates = blocks.stream()
                .map(BlockPredicateEntry::ofBlockOrTag)
                .filter(Objects::nonNull)
                .toList();
        invalidateCache();
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public void invalidateCache() {
        predicateResultCache.clear();
    }

    public boolean contains(BlockState blockState) {
        Block block = blockState.getBlock();
        return predicateResultCache.computeIfAbsent(block, b -> {
            for (BlockPredicateEntry predicate : blockPredicates)
                if (predicate.check(blockState))
                    return true;
            return false;
        });
    }
}
