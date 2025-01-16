package de.leximon.fluidlogged.config;

import com.mojang.datafixers.util.Either;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public record Addon(
        boolean enabledByDefault,
        List<Either<Block, TagKey<Block>>> fluidloggableBlocks,
        List<Either<Block, TagKey<Block>>> fluidPermeableBlocks,
        List<Either<Block, TagKey<Block>>> shapeIndependentFluidPermeableBlocks
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

            private boolean enabledByDefault = true;
            private final List<Either<Block, TagKey<Block>>> fluidloggableBlocks = new ArrayList<>();
            private final List<Either<Block, TagKey<Block>>> fluidPermeableBlocks = new ArrayList<>();
            private final List<Either<Block, TagKey<Block>>> shapeIndependentFluidPermeableBlocks = new ArrayList<>();

            public Builder enabledByDefault(boolean enabledByDefault) {
                this.enabledByDefault = enabledByDefault;
                return this;
            }

            public Builder fluidloggableBlocks(Collection<Block> blocks) {
                for (Block block : blocks) {
                    this.fluidloggableBlocks.add(Either.left(block));
                }
                return this;
            }

            public Builder fluidloggableBlocks(Block... blocks) {
                return fluidloggableBlocks(Arrays.asList(blocks));
            }

            public Builder fluidloggableBlockTags(Collection<TagKey<Block>> tags) {
                for (TagKey<Block> tag : tags) {
                    this.fluidloggableBlocks.add(Either.right(tag));
                }
                return this;
            }

            @SafeVarargs
            public final Builder fluidloggableBlockTags(TagKey<Block>... tags) {
                return fluidloggableBlockTags(Arrays.asList(tags));
            }

            public Builder fluidPermeableBlocks(Collection<Block> blocks) {
                for (Block block : blocks) {
                    this.fluidPermeableBlocks.add(Either.left(block));
                }
                return this;
            }

            public Builder fluidPermeableBlocks(Block... blocks) {
                return fluidPermeableBlocks(Arrays.asList(blocks));
            }

            public Builder fluidPermeableBlockTags(Collection<TagKey<Block>> tags) {
                for (TagKey<Block> tag : tags) {
                    this.fluidPermeableBlocks.add(Either.right(tag));
                }
                return this;
            }

            @SafeVarargs
            public final Builder fluidPermeableBlockTags(TagKey<Block>... tags) {
                return fluidPermeableBlockTags(Arrays.asList(tags));
            }

            public Builder shapeIndependentFluidPermeableBlocks(Collection<Block> blocks) {
                for (Block block : blocks) {
                    this.shapeIndependentFluidPermeableBlocks.add(Either.left(block));
                }
                return this;
            }

            public Builder shapeIndependentFluidPermeableBlocks(Block... blocks) {
                return shapeIndependentFluidPermeableBlocks(Arrays.asList(blocks));
            }

            public Builder shapeIndependentFluidPermeableBlockTags(Collection<TagKey<Block>> tags) {
                for (TagKey<Block> tag : tags) {
                    this.shapeIndependentFluidPermeableBlocks.add(Either.right(tag));
                }
                return this;
            }

            @SafeVarargs
            public final Builder shapeIndependentFluidPermeableBlockTags(TagKey<Block>... tags) {
                return shapeIndependentFluidPermeableBlockTags(Arrays.asList(tags));
            }

            public Addon build() {
                return new Addon(this.enabledByDefault, this.fluidloggableBlocks, this.fluidPermeableBlocks, this.shapeIndependentFluidPermeableBlocks);
            }
    }
}
