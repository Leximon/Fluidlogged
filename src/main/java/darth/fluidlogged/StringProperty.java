package darth.fluidlogged;

import net.minecraft.world.level.block.state.properties.Property;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class StringProperty extends Property<String>
{
    List<String> possibleValues;
    public StringProperty(String name)
    {
        super(name, String.class);
        possibleValues.addAll(Config.fluids.get());
    }

    public static StringProperty create(String name)
    {
        return new StringProperty(name);
    }

    public boolean equals(Object property)
    {
        if (this == property)
        {
            return true;
        }
        else if (property instanceof StringProperty && super.equals(property))
        {
            StringProperty stringproperty = (StringProperty)property;
            return this.possibleValues.equals(stringproperty.possibleValues);
        }
        else
        {
            return false;
        }
    }

    @Override
    public Collection<String> getPossibleValues()
    {
        return this.possibleValues;
    }

    @Override
    public String getName(String name)
    {
        return name;
    }

    @Override
    public Optional<String> getValue(String p_61701_)
    {
        return Optional.empty();
    }
}
