package de.leximon.fluidlogged.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.leximon.fluidlogged.config.controller.BlockPredicateController;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class BlockPredicateList {

    private final Supplier<List<String>> defaultBlocks;
    private final boolean justForFunBlacklist;
    private final Component categoryName;
    private final List<Component> description;

    private List<String> blocks;
    private boolean blacklist = false;

    private final HashSet<Block> compiledBlocks = new HashSet<>();

    public BlockPredicateList(
            Supplier<List<String>> defaultBlocks,
            boolean justForFunBlacklist,
            Component categoryName,
            List<Component> description
    ) {
        this.defaultBlocks = defaultBlocks;
        this.justForFunBlacklist = justForFunBlacklist;
        this.categoryName = categoryName;
        this.description = description;

        setBlocks(defaultBlocks.get());
    }

    public void setBlocks(List<String> blocks) {
        this.blocks = blocks;
        compile();
    }

    public List<String> getBlocks() {
        return this.blocks;
    }

    public void compile() {
        this.compiledBlocks.clear();

        for (String id : this.blocks) {
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

        this.compiledBlocks.add(blockOpt.get());
    }

    private void compileBlockTag(String id) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null)
            return;

        TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, location);

        Iterable<Holder<Block>> blockHolders = BuiltInRegistries.BLOCK.getTagOrEmpty(blockTag);
        for (Holder<Block> blockHolder : blockHolders)
            this.compiledBlocks.add(blockHolder.value());
    }

    public boolean contains(BlockState blockState) {
        Block block = blockState.getBlock();

        if (block instanceof LiquidBlock || blockState.isAir())
            return false;

        return this.compiledBlocks.contains(block) ^ this.blacklist;
    }

    public ConfigCategory createCategory() {
        return ConfigCategory.createBuilder()
                .name(this.categoryName)
                .option(Option.<Boolean>createBuilder()
                        .name(Component.translatable(this.justForFunBlacklist ? "fluidlogged.config.blacklist_just_for_fun" : "fluidlogged.config.blacklist"))
                        .description(OptionDescription.of(
                                Component.translatable("fluidlogged.config.blacklist.desc")
                        ))
                        .controller(option -> BooleanControllerBuilder.create(option)
                                .coloured(true)
                                .yesNoFormatter()
                        )
                        .binding(false, () -> this.blacklist, value -> this.blacklist = value)
                        .build()
                )
                .group(ListOption.<String>createBuilder()
                        .name(Component.translatable("fluidlogged.config.blocks"))
                        .description(OptionDescription.of(this.description.toArray(Component[]::new)))
                        .binding(this.defaultBlocks.get(), this::getBlocks, this::setBlocks)
                        .customController(BlockPredicateController::new)
                        .initial("")
                        .build()
                )
                .build();
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("blacklist", this.blacklist);
        obj.add("blocks", Config.GSON.toJsonTree(this.blocks));
        return obj;
    }

    public void fromJson(JsonObject obj) {
        if (obj.has("blocks")) {
            JsonElement element = obj.get("blocks");
            List<String> blocks = Config.GSON.fromJson(element, new TypeToken<>() {});
            setBlocks(blocks);
        }

        if (obj.has("blacklist"))
            this.blacklist = obj.get("blacklist").getAsBoolean();
    }
}
