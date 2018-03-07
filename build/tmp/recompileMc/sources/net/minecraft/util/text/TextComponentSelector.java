package net.minecraft.util.text;

public class TextComponentSelector extends TextComponentBase
{
    /** The selector used to find the matching entities of this text component */
    private final String selector;

    public TextComponentSelector(String selectorIn)
    {
        this.selector = selectorIn;
    }

    /**
     * Gets the selector of this component, in plain text.
     */
    public String getSelector()
    {
        return this.selector;
    }

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    public String getUnformattedComponentText()
    {
        return this.selector;
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public TextComponentSelector createCopy()
    {
        TextComponentSelector textcomponentselector = new TextComponentSelector(this.selector);
        textcomponentselector.setStyle(this.getStyle().createShallowCopy());

        for (ITextComponent itextcomponent : this.getSiblings())
        {
            textcomponentselector.appendSibling(itextcomponent.createCopy());
        }

        return textcomponentselector;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof TextComponentSelector))
        {
            return false;
        }
        else
        {
            TextComponentSelector textcomponentselector = (TextComponentSelector)p_equals_1_;
            return this.selector.equals(textcomponentselector.selector) && super.equals(p_equals_1_);
        }
    }

    public String toString()
    {
        return "SelectorComponent{pattern=\'" + this.selector + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
}