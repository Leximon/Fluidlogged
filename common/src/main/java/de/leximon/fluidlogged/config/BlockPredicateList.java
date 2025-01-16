package de.leximon.fluidlogged.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mojang.datafixers.util.Either;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
@Getter
public class BlockPredicateList {

    private final Config config;
    private Function<Addon, List<Either<Block, TagKey<Block>>>> addonBlockSupplier;
    private List<String> entries = Collections.emptyList();
    private boolean blacklist = false;

    private final HashSet<Block> effectiveBlocks = new HashSet<>();

    public BlockPredicateList(Config config, Function<Addon, List<Either<Block, TagKey<Block>>>> addonBlockSupplier) {
        this.config = config;
        this.addonBlockSupplier = addonBlockSupplier;
        compile();
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
        compile();
    }

    public void compile() {
        this.effectiveBlocks.clear();

        for (Addon enabledAddon : this.config.getEnabledAddons()) {
            List<Either<Block, TagKey<Block>>> blockOrTags = this.addonBlockSupplier.apply(enabledAddon);
            blockOrTags.forEach(blockOrTag -> blockOrTag
                    .ifLeft(this.effectiveBlocks::add)
                    .ifRight(tag -> {
                        Iterable<Holder<Block>> blockHolders = BuiltInRegistries.BLOCK.getTagOrEmpty(tag);
                        for (Holder<Block> blockHolder : blockHolders)
                            this.effectiveBlocks.add(blockHolder.value());
                    })
            );
        }

        for (String id : this.entries) {
            if (!id.startsWith("#"))
                parseBlockEntry(id);
            else
                parseBlockTagEntry(id.substring(1));
        }
    }

    private void parseBlockEntry(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null)
            return;

        Optional<Block> blockOpt = BuiltInRegistries.BLOCK.getOptional(location);
        if (blockOpt.isEmpty())
            return;

        this.effectiveBlocks.add(blockOpt.get());
    }

    private void parseBlockTagEntry(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null)
            return;

        TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, location);

        Iterable<Holder<Block>> blockHolders = BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag);
        for (Holder<Block> blockHolder : blockHolders)
            this.effectiveBlocks.add(blockHolder.value());
    }

    public boolean contains(BlockState blockState) {
        Block block = blockState.getBlock();

        if (block instanceof LiquidBlock || blockState.isAir())
            return false;

        return this.effectiveBlocks.contains(block) ^ this.blacklist;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("blacklist", this.blacklist);
        obj.add("blocks", Config.GSON.toJsonTree(this.entries));
        return obj;
    }

    public void fromJson(JsonObject obj) {
        if (obj.has("blocks")) {
            JsonElement element = obj.get("blocks");
            List<String> blocks = Config.GSON.fromJson(element, new TypeToken<>() {});
            setEntries(blocks);
        }

        if (obj.has("blacklist"))
            this.blacklist = obj.get("blacklist").getAsBoolean();
    }
}
