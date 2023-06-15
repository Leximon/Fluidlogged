package de.leximon.fluidlogged;

import net.fabricmc.api.ModInitializer;


public class Fluidlogged implements ModInitializer {
	@Override
	public void onInitialize() {
		// Prints all vanilla waterloggables
//		ArrayList<Class<? extends Block>> blocks = new ArrayList<>();
//		for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
//			Block block = entry.getValue();
//			if(block.defaultBlockState().hasProperty(BlockStateProperties.WATERLOGGED) && !blocks.contains(block.getClass())) {
//				System.out.println(block.getClass().getSimpleName());
//				blocks.add(block.getClass());
//			}
//		}
	}

}
