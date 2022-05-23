package de.leximon.fluidlogged.core;

import net.minecraft.world.level.block.state.properties.Property;
import java.util.*;

public class StringProperty extends Property<String>
{
    private final HashSet<String> values;
    protected StringProperty(String name)
    {
        super(name, String.class);
        this.values = new HashSet<>();
        this.values.add("");
        this.values.addAll( FluidloggedConfig.getFluidList());
    }

    @Override
    public Collection<String> getPossibleValues()
    {
        return this.values;
    }

    @Override
    public String getName(String name)
    {
        return name;
    }

    @Override
    public Optional<String> getValue(String value)
    {
        try
        {
            if(this.values.contains(value))
            {
                return Optional.of(value);
            }
            return Optional.empty();
        }
        catch(Exception e)
        {
            return Optional.empty();
        }
    }

    public static StringProperty create(String name)
    {
        return new StringProperty(name);
    }
}
