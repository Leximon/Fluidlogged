package de.leximon.fluidlogged.commands.arguments;

import com.google.common.collect.Maps;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.*;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FluidStateParser {
    public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(Component.translatable("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((object) -> {
        return Component.translatable("argument.block.id.invalid", object);
    });
    public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((object, object2) -> {
        return Component.translatable("argument.block.property.unknown", object, object2);
    });
    public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((object, object2) -> {
        return Component.translatable("argument.block.property.duplicate", object2, object);
    });
    public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((object, object2, object3) -> {
        return Component.translatable("argument.block.property.invalid", object, object3, object2);
    });
    public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((object, object2) -> {
        return Component.translatable("argument.block.property.novalue", object, object2);
    });
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(Component.translatable("argument.block.property.unclosed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType((object) -> {
        return Component.translatable("arguments.block.tag.unknown", object);
    });
    private static final char SYNTAX_START_PROPERTIES = '[';
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_END_PROPERTIES = ']';
    private static final char SYNTAX_EQUALS = '=';
    private static final char SYNTAX_PROPERTY_SEPARATOR = ',';
    private static final char SYNTAX_TAG = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Fluid> fluids;
    private final StringReader reader;
    private final boolean forTesting;
    private final Map<Property<?>, Comparable<?>> properties = Maps.newHashMap();
    private final Map<String, String> vagueProperties = Maps.newHashMap();
    private ResourceLocation id = new ResourceLocation("");
    @Nullable private StateDefinition<Fluid, FluidState> definition;
    @Nullable
    private FluidState state;
    @Nullable
    private HolderSet<Fluid> tag;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions;

    private FluidStateParser(HolderLookup<Fluid> holderLookup, StringReader stringReader, boolean forTesting) {
        this.suggestions = SUGGEST_NOTHING;
        this.fluids = holderLookup;
        this.reader = stringReader;
        this.forTesting = forTesting;
    }

    public static FluidResult parseForFluid(HolderLookup<Fluid> holderLookup, StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();

        try {
            FluidStateParser parser = new FluidStateParser(holderLookup, stringReader, false);
            parser.parse();
            return new FluidResult(parser.state, parser.properties);
        } catch (CommandSyntaxException var5) {
            stringReader.setCursor(i);
            throw var5;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Fluid> holderLookup, SuggestionsBuilder suggestionsBuilder, boolean forTesting) {
        StringReader stringReader = new StringReader(suggestionsBuilder.getInput());
        stringReader.setCursor(suggestionsBuilder.getStart());
        FluidStateParser FluidStateParser = new FluidStateParser(holderLookup, stringReader, forTesting);

        try {
            FluidStateParser.parse();
        } catch (CommandSyntaxException ignore) {}

        return FluidStateParser.suggestions.apply(suggestionsBuilder.createOffset(stringReader.getCursor()));
    }

    private void parse() throws CommandSyntaxException {
        if (this.forTesting) {
            this.suggestions = this::suggestFluidIdOrTag;
        } else {
            this.suggestions = this::suggestItem;
        }

        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
            this.suggestions = this::suggestOpenVagueProperties;
            if (this.reader.canRead() && this.reader.peek() == '[')
                this.readVagueProperties();
        } else {
            this.readFluid();
            this.suggestions = this::suggestOpenProperties;
            if (this.reader.canRead() && this.reader.peek() == '[')
                this.readProperties();
        }

    }

    private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }

        return this.suggestPropertyName(suggestionsBuilder);
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }

        return this.suggestVaguePropertyName(suggestionsBuilder);
    }

    private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);

        for (Property<?> property : this.state.getProperties())
            if (!this.properties.containsKey(property) && property.getName().startsWith(string))
                suggestionsBuilder.suggest(property.getName() + "=");


        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyName(SuggestionsBuilder suggestionsBuilder) {
        String string = suggestionsBuilder.getRemaining().toLowerCase(Locale.ROOT);
        if (this.tag != null)
            for (Holder<Fluid> holder : this.tag)
                for (Property<?> property : holder.value().getStateDefinition().getProperties())
                    if (!this.vagueProperties.containsKey(property.getName()) && property.getName().startsWith(string))
                        suggestionsBuilder.suggest(property.getName() + "=");
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf('='));
        }

        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty()) {
            suggestionsBuilder.suggest(String.valueOf(']'));
        }

        if (suggestionsBuilder.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
            suggestionsBuilder.suggest(String.valueOf(','));
        }

        return suggestionsBuilder.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(SuggestionsBuilder suggestionsBuilder, Property<T> property) {
        for (T t : property.getPossibleValues()) {
            if (t instanceof Integer i)
                suggestionsBuilder.suggest(i);
            else
                suggestionsBuilder.suggest(property.getName(t));
        }
        return suggestionsBuilder;
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyValue(SuggestionsBuilder builder, String name) {
        boolean bl = false;
        if (this.tag != null) {
            for (Holder<Fluid> registryEntry : this.tag) {
                Fluid fluid = registryEntry.value();
                Property<?> property = fluid.getStateDefinition().getProperty(name);
                if (property != null) {
                    addSuggestions(builder, property);
                }

                if (!bl) {
                    for (Property<?> property2 : fluid.getStateDefinition().getProperties()) {
                        if (!this.vagueProperties.containsKey(property2.getName())) {
                            bl = true;
                            break;
                        }
                    }
                }
            }
        }

        if (bl) {
            builder.suggest(String.valueOf(','));
        }

        builder.suggest(String.valueOf(']'));
        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenVagueProperties(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty() && this.tag != null) {
            boolean hasProps = false;

            for (Holder<Fluid> holder : this.tag) {
                Fluid block = holder.value();
                hasProps |= !block.getStateDefinition().getProperties().isEmpty();
            }

            if (hasProps)
                suggestionsBuilder.suggest(String.valueOf('['));
        }

        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenProperties(SuggestionsBuilder suggestionsBuilder) {
        if (suggestionsBuilder.getRemaining().isEmpty())
            if (!this.definition.getProperties().isEmpty())
                suggestionsBuilder.suggest(String.valueOf('['));
        return suggestionsBuilder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(this.fluids.listTagIds().map(TagKey::location), suggestionsBuilder, String.valueOf('#'));
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder suggestionsBuilder) {
        return SharedSuggestionProvider.suggestResource(this.fluids.listElementIds().map(ResourceKey::location), suggestionsBuilder);
    }

    private CompletableFuture<Suggestions> suggestFluidIdOrTag(SuggestionsBuilder suggestionsBuilder) {
        this.suggestTag(suggestionsBuilder);
        this.suggestItem(suggestionsBuilder);
        return suggestionsBuilder.buildFuture();
    }

    private void readFluid() throws CommandSyntaxException {
        int i = this.reader.getCursor();
        this.id = ResourceLocation.read(this.reader);
        Fluid block = this.fluids.get(ResourceKey.create(Registries.FLUID, this.id)).orElseThrow(() -> {
            this.reader.setCursor(i);
            return ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
        }).value();
        this.definition = block.getStateDefinition();
        this.state = block.defaultFluidState();
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.forTesting) {
            throw ERROR_NO_TAGS_ALLOWED.createWithContext(this.reader);
        } else {
            int i = this.reader.getCursor();
            this.reader.expect('#');
            this.suggestions = this::suggestTag;
            ResourceLocation resourceLocation = ResourceLocation.read(this.reader);
            this.tag = this.fluids.get(TagKey.create(Registries.FLUID, resourceLocation)).orElseThrow(() -> {
                this.reader.setCursor(i);
                return ERROR_UNKNOWN_TAG.createWithContext(this.reader, resourceLocation.toString());
            });
        }
    }

    private void readProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestPropertyNameOrEnd;
        this.reader.skipWhitespace();

        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String string = this.reader.readString();
            Property<?> property = this.definition.getProperty(string);
            if (property == null) {
                this.reader.setCursor(i);
                throw ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), string);
            }

            if (this.properties.containsKey(property)) {
                this.reader.setCursor(i);
                throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), string);
            }

            this.reader.skipWhitespace();
            this.suggestions = this::suggestEquals;
            if (this.reader.canRead() && this.reader.peek() == '=') {
                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = (suggestionsBuilder) -> addSuggestions(suggestionsBuilder, property).buildFuture();
                int j = this.reader.getCursor();
                this.setValue(property, this.reader.readString(), j);
                this.suggestions = this::suggestNextPropertyOrEnd;
                this.reader.skipWhitespace();
                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestions = this::suggestPropertyName;
                    continue;
                }

                if (this.reader.peek() != ']') {
                    throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
                }
                break;
            }

            throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), string);
        }

        if (this.reader.canRead()) {
            this.reader.skip();
        } else {
            throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
        }
    }

    private void readVagueProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestVaguePropertyNameOrEnd;
        int i = -1;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != ']') {
                this.reader.skipWhitespace();
                int j = this.reader.getCursor();
                String string = this.reader.readString();
                if (this.vagueProperties.containsKey(string)) {
                    this.reader.setCursor(j);
                    throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), string);
                }

                this.reader.skipWhitespace();
                if (!this.reader.canRead() || this.reader.peek() != '=') {
                    this.reader.setCursor(j);
                    throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), string);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = (suggestionsBuilder) -> {
                    return this.suggestVaguePropertyValue(suggestionsBuilder, string);
                };
                i = this.reader.getCursor();
                String string2 = this.reader.readString();
                this.vagueProperties.put(string, string2);
                this.reader.skipWhitespace();
                if (!this.reader.canRead()) {
                    continue;
                }

                i = -1;
                if (this.reader.peek() == ',') {
                    this.reader.skip();
                    this.suggestions = this::suggestVaguePropertyName;
                    continue;
                }

                if (this.reader.peek() != ']') {
                    throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                return;
            }

            if (i >= 0) {
                this.reader.setCursor(i);
            }

            throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
        }
    }


    private <T extends Comparable<T>> void setValue(Property<T> property, String string, int i) throws CommandSyntaxException {
        Optional<T> optional = property.getValue(string);
        if (optional.isPresent()) {
            this.state = this.state.setValue(property, optional.get());
            this.properties.put(property, optional.get());
        } else {
            this.reader.setCursor(i);
            throw ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), property.getName(), string);
        }
    }

    public static String serialize(FluidState blockState) {
        StringBuilder stringBuilder = new StringBuilder(blockState.holder().unwrapKey()
                .map((resourceKey) -> resourceKey.location().toString())
                .orElse("empty")
        );
        if (!blockState.getProperties().isEmpty()) {
            stringBuilder.append('[');
            boolean bl = false;

            for (UnmodifiableIterator<Map.Entry<Property<?>, Comparable<?>>> var3 = blockState.getValues().entrySet().iterator(); var3.hasNext(); bl = true) {
                Map.Entry<Property<?>, Comparable<?>> entry = var3.next();
                if (bl) {
                    stringBuilder.append(',');
                }

                appendProperty(stringBuilder, entry.getKey(), entry.getValue());
            }

            stringBuilder.append(']');
        }

        return stringBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> void appendProperty(StringBuilder stringBuilder, Property<T> property, Comparable<?> comparable) {
        stringBuilder.append(property.getName());
        stringBuilder.append('=');
        stringBuilder.append(property.getName((T) comparable));
    }

    public record FluidResult(FluidState fluidState, Map<Property<?>, Comparable<?>> properties) {}

    public record TagResult(HolderSet<Fluid> tag, Map<String, String> vagueProperties) {}
}
