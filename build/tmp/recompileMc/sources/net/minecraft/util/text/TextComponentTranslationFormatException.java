package net.minecraft.util.text;

public class TextComponentTranslationFormatException extends IllegalArgumentException
{
    public TextComponentTranslationFormatException(TextComponentTranslation component, String message)
    {
        super(String.format("Error parsing: %s: %s", new Object[] {component, message}));
    }

    public TextComponentTranslationFormatException(TextComponentTranslation component, int index)
    {
        super(String.format("Invalid index %d requested for %s", new Object[] {Integer.valueOf(index), component}));
    }

    public TextComponentTranslationFormatException(TextComponentTranslation component, Throwable cause)
    {
        super(String.format("Error while parsing: %s", new Object[] {component}), cause);
    }
}