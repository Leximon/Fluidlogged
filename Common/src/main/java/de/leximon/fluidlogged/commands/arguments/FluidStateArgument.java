package de.leximon.fluidlogged.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class FluidStateArgument implements ArgumentType<FluidInput> {
    private static final Collection<String> EXAMPLES = Arrays.asList("water", "minecraft:water", "water[foo=bar]");
    private final HolderLookup<Fluid> fluids;

    public FluidStateArgument(CommandBuildContext commandBuildContext) {
        this.fluids = commandBuildContext.holderLookup(Registries.FLUID);
    }

    public static FluidStateArgument fluid(CommandBuildContext commandBuildContext) {
        return new FluidStateArgument(commandBuildContext);
    }

    @Override
    public FluidInput parse(StringReader stringReader) throws CommandSyntaxException {
        FluidStateParser.FluidResult result = FluidStateParser.parseForFluid(this.fluids, stringReader);
        return new FluidInput(result.fluidState(), result.properties().keySet());
    }

    public static FluidInput getFluid(CommandContext<CommandSourceStack> commandContext, String string) {
        return commandContext.getArgument(string, FluidInput.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return FluidStateParser.fillSuggestions(this.fluids, suggestionsBuilder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
