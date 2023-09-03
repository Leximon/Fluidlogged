package de.leximon.fluidlogged;

import com.mojang.brigadier.CommandDispatcher;
import de.leximon.fluidlogged.commands.SetFluidCommand;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.network.forge.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.forge.ClientboundSectionFluidsUpdatePacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Optional;

@Mod(Fluidlogged.MOD_ID)
public class FluidloggedForge {

    public static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
            Fluidlogged.id("channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, "fluidlogged");


    public FluidloggedForge() {
        Fluidlogged.Internal.initialize();
        registerPackets();
        registerArguments();
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClient);

    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient(){
        // Register the configuration GUI factory
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> Config.createConfigScreen(parent))
        );
    }

    private void registerArguments() {
        SingletonArgumentInfo<FluidStateArgument> fluidArgument = SingletonArgumentInfo.contextAware(FluidStateArgument::fluid);
        ARGUMENT_TYPES.register("fluid_state", () -> fluidArgument);
        ArgumentTypeInfos.registerByClass(FluidStateArgument.class, fluidArgument);
    }

    private void registerPackets() {
        PACKET_CHANNEL.registerMessage(
                0, ClientboundFluidUpdatePacket.class,
                ClientboundFluidUpdatePacket::write, ClientboundFluidUpdatePacket::read,
                ClientboundFluidUpdatePacket::apply,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
        PACKET_CHANNEL.registerMessage(
                1, ClientboundSectionFluidsUpdatePacket.class,
                ClientboundSectionFluidsUpdatePacket::write, ClientboundSectionFluidsUpdatePacket::read,
                ClientboundSectionFluidsUpdatePacket::apply,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
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
            Config.invalidateCaches();
        }

        @SubscribeEvent
        public void dataPackReload(AddReloadListenerEvent event) {
            Config.invalidateCaches();
        }

    }
}
