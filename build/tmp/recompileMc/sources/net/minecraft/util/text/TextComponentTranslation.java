package net.minecraft.util.text;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.text.translation.I18n;

public class TextComponentTranslation extends TextComponentBase
{
    private final String key;
    private final Object[] formatArgs;
    private final Object syncLock = new Object();
    private long lastTranslationUpdateTimeInMilliseconds = -1L;
    /**
     * The discrete elements that make up this component.  For example, this would be ["Prefix, ", "FirstArg",
     * "SecondArg", " again ", "SecondArg", " and ", "FirstArg", " lastly ", "ThirdArg", " and also ", "FirstArg", "
     * again!"] for "translation.test.complex" (see en-US.lang)
     */
    @VisibleForTesting
    List<ITextComponent> children = Lists.<ITextComponent>newArrayList();
    public static final Pattern STRING_VARIABLE_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public TextComponentTranslation(String translationKey, Object... args)
    {
        this.key = translationKey;
        this.formatArgs = args;

        for (Object object : args)
        {
            if (object instanceof ITextComponent)
            {
                ((ITextComponent)object).getStyle().setParentStyle(this.getStyle());
            }
        }
    }

    @VisibleForTesting

    /**
     * ensures that our children are initialized from the most recent string translation mapping.
     */
    synchronized void ensureInitialized()
    {
        synchronized (this.syncLock)
        {
            long i = I18n.getLastTranslationUpdateTimeInMilliseconds();

            if (i == this.lastTranslationUpdateTimeInMilliseconds)
            {
                return;
            }

            this.lastTranslationUpdateTimeInMilliseconds = i;
            this.children.clear();
        }

        try
        {
            this.initializeFromFormat(I18n.translateToLocal(this.key));
        }
        catch (TextComponentTranslationFormatException textcomponenttranslationformatexception)
        {
            this.children.clear();

            try
            {
                this.initializeFromFormat(I18n.translateToFallback(this.key));
            }
            catch (TextComponentTranslationFormatException var5)
            {
                throw textcomponenttranslationformatexception;
            }
        }
    }

    /**
     * initializes our children from a format string, using the format args to fill in the placeholder variables.
     */
    protected void initializeFromFormat(String format)
    {
        boolean flag = false;
        Matcher matcher = STRING_VARIABLE_PATTERN.matcher(format);
        int i = 0;
        int j = 0;

        try
        {
            int l;

            for (; matcher.find(j); j = l)
            {
                int k = matcher.start();
                l = matcher.end();

                if (k > j)
                {
                    TextComponentString textcomponentstring = new TextComponentString(String.format(format.substring(j, k), new Object[0]));
                    textcomponentstring.getStyle().setParentStyle(this.getStyle());
                    this.children.add(textcomponentstring);
                }

                String s2 = matcher.group(2);
                String s = format.substring(k, l);

                if ("%".equals(s2) && "%%".equals(s))
                {
                    TextComponentString textcomponentstring2 = new TextComponentString("%");
                    textcomponentstring2.getStyle().setParentStyle(this.getStyle());
                    this.children.add(textcomponentstring2);
                }
                else
                {
                    if (!"s".equals(s2))
                    {
                        throw new TextComponentTranslationFormatException(this, "Unsupported format: \'" + s + "\'");
                    }

                    String s1 = matcher.group(1);
                    int i1 = s1 != null ? Integer.parseInt(s1) - 1 : i++;

                    if (i1 < this.formatArgs.length)
                    {
                        this.children.add(this.getFormatArgumentAsComponent(i1));
                    }
                }
            }

            if (j < format.length())
            {
                TextComponentString textcomponentstring1 = new TextComponentString(String.format(format.substring(j), new Object[0]));
                textcomponentstring1.getStyle().setParentStyle(this.getStyle());
                this.children.add(textcomponentstring1);
            }
        }
        catch (IllegalFormatException illegalformatexception)
        {
            throw new TextComponentTranslationFormatException(this, illegalformatexception);
        }
    }

    private ITextComponent getFormatArgumentAsComponent(int index)
    {
        if (index >= this.formatArgs.length)
        {
            throw new TextComponentTranslationFormatException(this, index);
        }
        else
        {
            Object object = this.formatArgs[index];
            ITextComponent itextcomponent;

            if (object instanceof ITextComponent)
            {
                itextcomponent = (ITextComponent)object;
            }
            else
            {
                itextcomponent = new TextComponentString(object == null ? "null" : object.toString());
                itextcomponent.getStyle().setParentStyle(this.getStyle());
            }

            return itextcomponent;
        }
    }

    public ITextComponent setStyle(Style style)
    {
        super.setStyle(style);

        for (Object object : this.formatArgs)
        {
            if (object instanceof ITextComponent)
            {
                ((ITextComponent)object).getStyle().setParentStyle(this.getStyle());
            }
        }

        if (this.lastTranslationUpdateTimeInMilliseconds > -1L)
        {
            for (ITextComponent itextcomponent : this.children)
            {
                itextcomponent.getStyle().setParentStyle(style);
            }
        }

        return this;
    }

    public Iterator<ITextComponent> iterator()
    {
        this.ensureInitialized();
        return Iterators.<ITextComponent>concat(createDeepCopyIterator(this.children), createDeepCopyIterator(this.siblings));
    }

    /**
     * Gets the text of this component, without any special formatting codes added, for chat.  TODO: why is this two
     * different methods?
     */
    public String getUnformattedComponentText()
    {
        this.ensureInitialized();
        StringBuilder stringbuilder = new StringBuilder();

        for (ITextComponent itextcomponent : this.children)
        {
            stringbuilder.append(itextcomponent.getUnformattedComponentText());
        }

        return stringbuilder.toString();
    }

    /**
     * Creates a copy of this component.  Almost a deep copy, except the style is shallow-copied.
     */
    public TextComponentTranslation createCopy()
    {
        Object[] aobject = new Object[this.formatArgs.length];

        for (int i = 0; i < this.formatArgs.length; ++i)
        {
            if (this.formatArgs[i] instanceof ITextComponent)
            {
                aobject[i] = ((ITextComponent)this.formatArgs[i]).createCopy();
            }
            else
            {
                aobject[i] = this.formatArgs[i];
            }
        }

        TextComponentTranslation textcomponenttranslation = new TextComponentTranslation(this.key, aobject);
        textcomponenttranslation.setStyle(this.getStyle().createShallowCopy());

        for (ITextComponent itextcomponent : this.getSiblings())
        {
            textcomponenttranslation.appendSibling(itextcomponent.createCopy());
        }

        return textcomponenttranslation;
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof TextComponentTranslation))
        {
            return false;
        }
        else
        {
            TextComponentTranslation textcomponenttranslation = (TextComponentTranslation)p_equals_1_;
            return Arrays.equals(this.formatArgs, textcomponenttranslation.formatArgs) && this.key.equals(textcomponenttranslation.key) && super.equals(p_equals_1_);
        }
    }

    public int hashCode()
    {
        int i = super.hashCode();
        i = 31 * i + this.key.hashCode();
        i = 31 * i + Arrays.hashCode(this.formatArgs);
        return i;
    }

    public String toString()
    {
        return "TranslatableComponent{key=\'" + this.key + '\'' + ", args=" + Arrays.toString(this.formatArgs) + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    public String getKey()
    {
        return this.key;
    }

    public Object[] getFormatArgs()
    {
        return this.formatArgs;
    }
}