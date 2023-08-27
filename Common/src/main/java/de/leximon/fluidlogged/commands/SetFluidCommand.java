package de.leximon.fluidlogged.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.leximon.fluidlogged.commands.arguments.FluidInput;
import de.leximon.fluidlogged.commands.arguments.FluidStateArgument;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class SetFluidCommand {

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
        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "pos");
        FluidInput fluid = FluidStateArgument.getFluid(context, "fluid");
        fluid.place(context.getSource().getLevel(), pos, Block.UPDATE_CLIENTS);
        return 1;
    }

}
