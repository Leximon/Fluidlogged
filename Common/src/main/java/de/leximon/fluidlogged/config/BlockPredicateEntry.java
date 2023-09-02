package de.leximon.fluidlogged.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface BlockPredicateEntry {

    boolean check(BlockState blockState);

    static BlockPredicateEntry ofBlockOrTag(String id) {
        if (id.startsWith("#")) {
            ResourceLocation location = ResourceLocation.tryParse(id.substring(1));
            return location != null
                    ? ofTag(TagKey.create(Registries.BLOCK, location))
                    : null;
        }

        ResourceLocation location = ResourceLocation.tryParse(id);
        return location != null
                ? ofBlock(BuiltInRegistries.BLOCK.get(location))
                : null;
    }

    static BlockPredicateEntry ofTag(TagKey<Block> tag) {
        return blockState -> blockState.is(tag);
    }

    static BlockPredicateEntry ofBlock(Block block) {
        return blockState -> blockState.is(block);
    }
}
