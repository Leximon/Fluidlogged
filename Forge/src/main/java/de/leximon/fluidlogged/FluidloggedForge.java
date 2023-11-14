package de.leximon.fluidlogged;

import com.mojang.brigadier.CommandDispatcher;
import de.leximon.fluidlogged.commands.SetFluidCommand;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import de.leximon.fluidlogged.config.Config;
import de.leximon.fluidlogged.config.YaclMissingScreen;
import de.leximon.fluidlogged.network.forge.ClientboundFluidUpdatePacket;
import de.leximon.fluidlogged.network.forge.ClientboundSectionFluidsUpdatePacket;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.IdMapper;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.*;

import java.util.Optional;

@Mod(Fluidlogged.MOD_ID)
public class FluidloggedForge {

    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, "fluidlogged");

    public static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
            Fluidlogged.id("channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public static IdMapper<FluidState> fluidStateIdMapper;

    public FluidloggedForge() {
        Fluidlogged.Internal.initialize();
        registerPackets();
        registerArguments();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::setupClient);

    }

    @OnlyIn(Dist.CLIENT)
    private void setupClient() {
        // Register the configuration GUI factory
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> {
                    if(!ModList.get().isLoaded("yet_another_config_lib_v3")) // we could cache this value but it's not worth it
                        return new YaclMissingScreen(parent);
                    return Config.createConfigScreen(parent);
                })
        );
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        IdMapper<FluidState> idMapper = new IdMapper<>();
        for (Fluid fluid : ForgeRegistries.FLUIDS) {
            for(FluidState fluidstate : fluid.getStateDefinition().getPossibleStates()) {
                idMapper.add(fluidstate);
            }
        }
        fluidStateIdMapper = idMapper;
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
            Config.compile();
        }

        @SubscribeEvent
        public void dataPackReload(AddReloadListenerEvent event) {
            Config.compile();
        }

    }
}
