package de.leximon.fluidlogged.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.leximon.fluidlogged.Fluidlogged;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;

public class DumpFluidsCommand
{
    public DumpFluidsCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register
                (
                Commands.literal("dumpfluids").requires((access) -> access.hasPermission(2))
                .executes((command)-> dumpFluids(command.getSource()))
                );
    }

    public int dumpFluids(CommandSourceStack source)
    {
        ArrayList<String> fluids = new ArrayList<>();
        for(Map.Entry<ResourceKey<Fluid>, Fluid> key : ForgeRegistries.FLUIDS.getEntries())
        {
            Fluid fluid = key.getValue();
            if(fluid.isSource(fluid.defaultFluidState()))
            {
                fluids.add(fluid.getRegistryName().toString());
            }
        }
        Fluidlogged.LOGGER.info(fluids);
        source.sendSuccess(new TextComponent("Dumped " + fluids.size() + " fluids to the latest.log."), true);
        return 1;
    }
}
