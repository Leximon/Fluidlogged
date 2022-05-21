package de.leximon.fluidlogged;

/*
public class StringProperty extends Property<String>
{
    List<String> possibleValues;
    public StringProperty(String name)
    {
        super(name, String.class);
        possibleValues = new ArrayList<>(Config.getFluidList());
        possibleValues.add("");
        Fluidlogged.LOGGER.info("Fluidloggable fluids: " + possibleValues.toString());
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
            return this.possibleValues.equals(((StringProperty)property).possibleValues);
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
        String retName = (name.length() == 0) ? "_" : name;
        return retName.replaceAll(":", "_").replaceAll(" ", "_");
    }

    @Override
    public Optional<String> getValue(String value)
    {
        return possibleValues.contains(value) ? Optional.of(value) : Optional.empty();
    }
}
 */