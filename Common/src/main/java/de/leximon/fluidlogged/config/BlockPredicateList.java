package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.swing.text.html.Option;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BlockPredicateList {

    private List<String> blocks;
    private final HashSet<Block> compiledBlocks = new HashSet<>();

    public BlockPredicateList(List<String> defaults) {
        setBlocks(defaults);
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
        compile();
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public void compile() {
        compiledBlocks.clear();

        for (String id : blocks) {
            if (!id.startsWith("#"))
                compileSingleBlock(id);
            else
                compileBlockTag(id.substring(1));
        }
    }

    private void compileSingleBlock(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null)
            return;

        Optional<Block> blockOpt = BuiltInRegistries.BLOCK.getOptional(location);
        if (blockOpt.isEmpty())
            return;

        compiledBlocks.add(blockOpt.get());
    }

    private void compileBlockTag(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null)
            return;

        TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, location);

        Iterable<Holder<Block>> blockHolders = BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag);
        for (Holder<Block> blockHolder : blockHolders)
            compiledBlocks.add(blockHolder.value());
    }

    public boolean contains(BlockState blockState) {
        Block block = blockState.getBlock();
        return compiledBlocks.contains(block);
    }
}
