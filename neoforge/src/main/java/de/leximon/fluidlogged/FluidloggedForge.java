package de.leximon.fluidlogged;

import com.mojang.brigadier.CommandDispatcher;
import de.leximon.fluidlogged.commands.SetFluidCommand;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import de.leximon.fluidlogged.config.ConfigScreen;
import de.leximon.fluidlogged.config.YaclMissingScreen;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.IdMapper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;

@Mod(Fluidlogged.MOD_ID)
public class FluidloggedForge {

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, "fluidlogged");
    
    public FluidloggedForge() {
        Fluidlogged.Internal.initialize();
        registerArguments();

        NeoForge.EVENT_BUS.register(new EventHandler());

    }

    private void registerArguments() {
        SingletonArgumentInfo<FluidStateArgument> fluidArgument = SingletonArgumentInfo.contextAware(FluidStateArgument::fluid);
        ARGUMENT_TYPES.register("fluid_state", () -> fluidArgument);
        ArgumentTypeInfos.registerByClass(FluidStateArgument.class, fluidArgument);
    }


    public static class EventHandler {

        @SubscribeEvent
        public void commandRegistration(RegisterCommandsEvent event) {
            CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
            CommandBuildContext context = event.getBuildContext();

            SetFluidCommand.register(dispatcher, context);
        }

        @SubscribeEvent
        public void serverStart(ServerStartedEvent event) {
            Fluidlogged.CONFIG.compile();
        }

        @SubscribeEvent
        public void dataPackReload(AddReloadListenerEvent event) {
            Fluidlogged.CONFIG.compile();
        }

    }
    
    @EventBusSubscriber(modid = Fluidlogged.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Register the configuration GUI factory
            ModLoadingContext.get().registerExtensionPoint(
              IConfigScreenFactory.class,
              () -> (mc, parent) -> {
                  if(!ModList.get().isLoaded("yet_another_config_lib_v3")) // we could cache this value but it's not worth it
                      return YaclMissingScreen.create(parent);
                  return ConfigScreen.create(parent);
              }
            );
        }
    }
}
