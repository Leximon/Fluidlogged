package de.leximon.fluidlogged.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.commands.arguments.FluidInput;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

public class SetFluidCommand {

    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(Component.translatable("commands.setfluid.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> d, CommandBuildContext buildContext) {
        d.register(Commands.literal("setfluid")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .then(Commands.argument("fluid", FluidStateArgument.fluid(buildContext))
                                .executes(SetFluidCommand::setFluid)
                        )
                )
        );
    }

    private static int setFluid(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        FluidInput fluid = FluidStateArgument.getFluid(context, "fluid");

        boolean success = fluid.place(context.getSource().getLevel(), pos, Block.UPDATE_CLIENTS | Fluidlogged.UPDATE_SCHEDULE_FLUID_TICK);
        if (!success)
            throw ERROR_FAILED.create();

        source.sendSuccess(
                () -> Component.translatable("commands.setfluid.success", pos.getX(), pos.getY(), pos.getZ()),
                true
        );
        return 1;
    }

}
