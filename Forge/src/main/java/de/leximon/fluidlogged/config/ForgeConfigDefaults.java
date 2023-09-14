package de.leximon.fluidlogged.config;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class ForgeConfigDefaults implements ConfigDefaults {

    public static final ImmutableList<String> FLUIDLOGGABLE_BLOCKS = ImmutableList.of(
            "minecraft:stonecutter",
            "minecraft:grindstone",
            "minecraft:lectern",
            "minecraft:brewing_stand",
            "minecraft:enchanting_table",
            "minecraft:bell",
            "minecraft:composter",
            "minecraft:sniffer_egg",
            "minecraft:turtle_egg",
            "minecraft:cake",
            "minecraft:lever",
            "minecraft:vine",
            "minecraft:dragon_egg",
            "minecraft:end_portal_frame",
            "minecraft:daylight_detector",
            "minecraft:hopper",
            "minecraft:barrier",
            "minecraft:end_rod",
            "minecraft:end_portal",
            "minecraft:bamboo",
            "minecraft:azalea",
            "minecraft:moss_carpet",
            "minecraft:player_head",
            "minecraft:player_wall_head",
            "minecraft:creeper_head",
            "minecraft:creeper_wall_head",
            "minecraft:dragon_head",
            "minecraft:dragon_wall_head",
            "minecraft:piglin_head",
            "minecraft:piglin_wall_head",
            "minecraft:skeleton_skull",
            "minecraft:skeleton_wall_skull",
            "minecraft:wither_skeleton_skull",
            "minecraft:wither_skeleton_wall_skull",
            "minecraft:zombie_head",
            "minecraft:zombie_wall_head",
            "#minecraft:fence_gates",
            "#minecraft:doors",
            "#minecraft:beds",
            "#minecraft:pressure_plates",
            "#minecraft:buttons",
            "#minecraft:wool_carpets",
            "#minecraft:banners",
            "#minecraft:flower_pots",
            "#minecraft:candle_cakes",
            "#minecraft:anvil"
    );

    public static final ImmutableList<String> FLUID_PERMEABLE_BLOCKS = ImmutableList.of(
            "minecraft:grindstone",
            "minecraft:lectern",
            "minecraft:brewing_stand",
            "minecraft:bell",
            "minecraft:lantern",
            "minecraft:soul_lantern",
            "minecraft:iron_bars",
            "minecraft:chain",
            "minecraft:vine",
            "minecraft:glow_lichen",
            "minecraft:sculk_vein",
            "minecraft:dragon_egg",
            "minecraft:hopper",
            "minecraft:sniffer_egg",
            "minecraft:turtle_egg",
            "minecraft:sea_pickle",
            "minecraft:end_rod",
            "minecraft:bamboo",
            "minecraft:scaffolding",
            "minecraft:ladder",
            "minecraft:pointed_dripstone",
            "minecraft:lightning_rod",
            "minecraft:hanging_roots",
            "minecraft:small_dripleaf",
            "minecraft:big_dripleaf",
            "minecraft:azalea",
            "minecraft:mangrove_propagule",
            "minecraft:moss_carpet",
            "minecraft:decorated_pot",
            "minecraft:cake",
            "minecraft:lever",
            "minecraft:player_head",
            "minecraft:player_wall_head",
            "minecraft:creeper_head",
            "minecraft:creeper_wall_head",
            "minecraft:dragon_head",
            "minecraft:dragon_wall_head",
            "minecraft:piglin_head",
            "minecraft:piglin_wall_head",
            "minecraft:skeleton_skull",
            "minecraft:skeleton_wall_skull",
            "minecraft:wither_skeleton_skull",
            "minecraft:wither_skeleton_wall_skull",
            "minecraft:zombie_head",
            "minecraft:zombie_wall_head",
            "#minecraft:wool_carpets",
            "#minecraft:fences",
            "#minecraft:fence_gates",
            "#minecraft:trapdoors",
            "#minecraft:doors",
            "#minecraft:buttons",
            "#minecraft:pressure_plates",
            "#minecraft:rails",
            "#minecraft:banners",
            "#minecraft:all_signs",
            "#minecraft:flower_pots",
            "#minecraft:campfires",
            "#minecraft:candles",
            "#minecraft:candle_cakes",
            "#minecraft:small_amethyst_bud",
            "#minecraft:medium_amethyst_bud",
            "#minecraft:large_amethyst_bud",
            "#minecraft:amethyst_cluster",
            "#minecraft:anvil",
            "#forge:chests"
    );

    public static final ImmutableList<String> SHAPE_INDEPENDENT_FLUID_PERMEABLE_BLOCKS = ImmutableList.of(
            "minecraft:light",
            "minecraft:barrier",
            "#minecraft:leaves"
    );

    @Override
    public List<String> fluidloggableBlocks() {
        return FLUIDLOGGABLE_BLOCKS;
    }

    @Override
    public List<String> fluidPermeableBlocks() {
        return FLUID_PERMEABLE_BLOCKS;
    }

    @Override
    public List<String> shapeIndependentFluidPermeableBlocks() {
        return SHAPE_INDEPENDENT_FLUID_PERMEABLE_BLOCKS;
    }
}
