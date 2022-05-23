package de.leximon.fluidlogged.commands;

import de.leximon.fluidlogged.Fluidlogged;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.ConfigCommand;

@Mod.EventBusSubscriber(modid = Fluidlogged.MOD_ID)
public class CommandRegistry
{
    @SubscribeEvent
    public static void RegisterCommands(RegisterCommandsEvent event)
    {
        new DumpFluidsCommand(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }
}
