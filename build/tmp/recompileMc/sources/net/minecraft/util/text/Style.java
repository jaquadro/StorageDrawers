package net.minecraft.util.text;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Style
{
    /** The parent of this ChatStyle.  Used for looking up values that this instance does not override. */
    private Style parentStyle;
    private TextFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    private String insertion;
    /** The base of the ChatStyle hierarchy.  All ChatStyle instances are implicitly children of this. */
    private static final Style ROOT = new Style()
    {
        /**
         * Gets the effective color of this ChatStyle.
         */
        @Nullable
        public TextFormatting getColor()
        {
            return null;
        }
        /**
         * Whether or not text of this ChatStyle should be in bold.
         */
        public boolean getBold()
        {
            return false;
        }
        /**
         * Whether or not text of this ChatStyle should be italicized.
         */
        public boolean getItalic()
        {
            return false;
        }
        /**
         * Whether or not to format text of this ChatStyle using strikethrough.
         */
        public boolean getStrikethrough()
        {
            return false;
        }
        /**
         * Whether or not text of this ChatStyle should be underlined.
         */
        public boolean getUnderlined()
        {
            return false;
        }
        /**
         * Whether or not text of this ChatStyle should be obfuscated.
         */
        public boolean getObfuscated()
        {
            return false;
        }
        /**
         * The effective chat click event.
         */
        @Nullable
        public ClickEvent getClickEvent()
        {
            return null;
        }
        /**
         * The effective chat hover event.
         */
        @Nullable
        public HoverEvent getHoverEvent()
        {
            return null;
        }
        /**
         * Get the text to be inserted into Chat when the component is shift-clicked
         */
        @Nullable
        public String getInsertion()
        {
            return null;
        }
        /**
         * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using
         * the specific methods.
         */
        public Style setColor(TextFormatting color)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is
         * bold and you want text of this style to be unbolded.
         */
        public Style setBold(Boolean boldIn)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
         * italicized and you want to override that for this style.
         */
        public Style setItalic(Boolean italic)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
         * style uses strikethrough and you want to override that for this style.
         */
        public Style setStrikethrough(Boolean strikethrough)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
         * underlined and you want to override that for this style.
         */
        public Style setUnderlined(Boolean underlined)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
         * obfuscated and you want to override that for this style.
         */
        public Style setObfuscated(Boolean obfuscated)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets the event that should be run when text of this ChatStyle is clicked on.
         */
        public Style setClickEvent(ClickEvent event)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets the event that should be run when text of this ChatStyle is hovered over.
         */
        public Style setHoverEvent(HoverEvent event)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
         * defaults are used (bold: false, underlined: false, etc).
         */
        public Style setParentStyle(Style parent)
        {
            throw new UnsupportedOperationException();
        }
        public String toString()
        {
            return "Style.ROOT";
        }
        /**
         * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy,
         * but changes to the parent style's values WILL be reflected in both this instance and the copy, wherever
         * either does not override a value.
         */
        public Style createShallowCopy()
        {
            return this;
        }
        /**
         * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
         * copy.
         */
        public Style createDeepCopy()
        {
            return this;
        }
        /**
         * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
         */
        @SideOnly(Side.CLIENT)
        public String getFormattingCode()
        {
            return "";
        }
    };

    /**
     * Gets the effective color of this ChatStyle.
     */
    @Nullable
    public TextFormatting getColor()
    {
        return this.color == null ? this.getParent().getColor() : this.color;
    }

    /**
     * Whether or not text of this ChatStyle should be in bold.
     */
    public boolean getBold()
    {
        return this.bold == null ? this.getParent().getBold() : this.bold.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be italicized.
     */
    public boolean getItalic()
    {
        return this.italic == null ? this.getParent().getItalic() : this.italic.booleanValue();
    }

    /**
     * Whether or not to format text of this ChatStyle using strikethrough.
     */
    public boolean getStrikethrough()
    {
        return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be underlined.
     */
    public boolean getUnderlined()
    {
        return this.underlined == null ? this.getParent().getUnderlined() : this.underlined.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be obfuscated.
     */
    public boolean getObfuscated()
    {
        return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated.booleanValue();
    }

    /**
     * Whether or not this style is empty (inherits everything from the parent).
     */
    public boolean isEmpty()
    {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null && this.clickEvent == null && this.hoverEvent == null && this.insertion == null;
    }

    /**
     * The effective chat click event.
     */
    @Nullable
    public ClickEvent getClickEvent()
    {
        return this.clickEvent == null ? this.getParent().getClickEvent() : this.clickEvent;
    }

    /**
     * The effective chat hover event.
     */
    @Nullable
    public HoverEvent getHoverEvent()
    {
        return this.hoverEvent == null ? this.getParent().getHoverEvent() : this.hoverEvent;
    }

    /**
     * Get the text to be inserted into Chat when the component is shift-clicked
     */
    @Nullable
    public String getInsertion()
    {
        return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
    }

    /**
     * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using the
     * specific methods.
     */
    public Style setColor(TextFormatting color)
    {
        this.color = color;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is bold
     * and you want text of this style to be unbolded.
     */
    public Style setBold(Boolean boldIn)
    {
        this.bold = boldIn;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
     * italicized and you want to override that for this style.
     */
    public Style setItalic(Boolean italic)
    {
        this.italic = italic;
        return this;
    }

    /**
     * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
     * style uses strikethrough and you want to override that for this style.
     */
    public Style setStrikethrough(Boolean strikethrough)
    {
        this.strikethrough = strikethrough;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
     * underlined and you want to override that for this style.
     */
    public Style setUnderlined(Boolean underlined)
    {
        this.underlined = underlined;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
     * obfuscated and you want to override that for this style.
     */
    public Style setObfuscated(Boolean obfuscated)
    {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is clicked on.
     */
    public Style setClickEvent(ClickEvent event)
    {
        this.clickEvent = event;
        return this;
    }

    /**
     * Sets the event that should be run when text of this ChatStyle is hovered over.
     */
    public Style setHoverEvent(HoverEvent event)
    {
        this.hoverEvent = event;
        return this;
    }

    /**
     * Set a text to be inserted into Chat when the component is shift-clicked
     */
    public Style setInsertion(String insertion)
    {
        this.insertion = insertion;
        return this;
    }

    /**
     * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
     * defaults are used (bold: false, underlined: false, etc).
     */
    public Style setParentStyle(Style parent)
    {
        this.parentStyle = parent;
        return this;
    }

    /**
     * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
     */
    public String getFormattingCode()
    {
        if (this.isEmpty())
        {
            return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.getColor() != null)
            {
                stringbuilder.append((Object)this.getColor());
            }

            if (this.getBold())
            {
                stringbuilder.append((Object)TextFormatting.BOLD);
            }

            if (this.getItalic())
            {
                stringbuilder.append((Object)TextFormatting.ITALIC);
            }

            if (this.getUnderlined())
            {
                stringbuilder.append((Object)TextFormatting.UNDERLINE);
            }

            if (this.getObfuscated())
            {
                stringbuilder.append((Object)TextFormatting.OBFUSCATED);
            }

            if (this.getStrikethrough())
            {
                stringbuilder.append((Object)TextFormatting.STRIKETHROUGH);
            }

            return stringbuilder.toString();
        }
    }

    /**
     * Gets the immediate parent of this ChatStyle.
     */
    private Style getParent()
    {
        return this.parentStyle == null ? ROOT : this.parentStyle;
    }

    public String toString()
    {
        return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + '}';
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof Style))
        {
            return false;
        }
        else
        {
            boolean flag;
            label0:
            {
                Style style = (Style)p_equals_1_;

                if (this.getBold() == style.getBold() && this.getColor() == style.getColor() && this.getItalic() == style.getItalic() && this.getObfuscated() == style.getObfuscated() && this.getStrikethrough() == style.getStrikethrough() && this.getUnderlined() == style.getUnderlined())
                {
                    label85:
                    {
                        if (this.getClickEvent() != null)
                        {
                            if (!this.getClickEvent().equals(style.getClickEvent()))
                            {
                                break label85;
                            }
                        }
                        else if (style.getClickEvent() != null)
                        {
                            break label85;
                        }

                        if (this.getHoverEvent() != null)
                        {
                            if (!this.getHoverEvent().equals(style.getHoverEvent()))
                            {
                                break label85;
                            }
                        }
                        else if (style.getHoverEvent() != null)
                        {
                            break label85;
                        }

                        if (this.getInsertion() != null)
                        {
                            if (this.getInsertion().equals(style.getInsertion()))
                            {
                                break label0;
                            }
                        }
                        else if (style.getInsertion() == null)
                        {
                            break label0;
                        }
                    }
                }

                flag = false;
                return flag;
            }
            flag = true;
            return flag;
        }
    }

    public int hashCode()
    {
        int i = this.color.hashCode();
        i = 31 * i + this.bold.hashCode();
        i = 31 * i + this.italic.hashCode();
        i = 31 * i + this.underlined.hashCode();
        i = 31 * i + this.strikethrough.hashCode();
        i = 31 * i + this.obfuscated.hashCode();
        i = 31 * i + this.clickEvent.hashCode();
        i = 31 * i + this.hoverEvent.hashCode();
        i = 31 * i + this.insertion.hashCode();
        return i;
    }

    /**
     * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy, but
     * changes to the parent style's values WILL be reflected in both this instance and the copy, wherever either does
     * not override a value.
     */
    public Style createShallowCopy()
    {
        Style style = new Style();
        style.bold = this.bold;
        style.italic = this.italic;
        style.strikethrough = this.strikethrough;
        style.underlined = this.underlined;
        style.obfuscated = this.obfuscated;
        style.color = this.color;
        style.clickEvent = this.clickEvent;
        style.hoverEvent = this.hoverEvent;
        style.parentStyle = this.parentStyle;
        style.insertion = this.insertion;
        return style;
    }

    /**
     * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
     * copy.
     */
    public Style createDeepCopy()
    {
        Style style = new Style();
        style.setBold(Boolean.valueOf(this.getBold()));
        style.setItalic(Boolean.valueOf(this.getItalic()));
        style.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
        style.setUnderlined(Boolean.valueOf(this.getUnderlined()));
        style.setObfuscated(Boolean.valueOf(this.getObfuscated()));
        style.setColor(this.getColor());
        style.setClickEvent(this.getClickEvent());
        style.setHoverEvent(this.getHoverEvent());
        style.setInsertion(this.getInsertion());
        return style;
    }

    public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style>
        {
            @Nullable
            public Style deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
            {
                if (p_deserialize_1_.isJsonObject())
                {
                    Style style = new Style();
                    JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();

                    if (jsonobject == null)
                    {
                        return null;
                    }
                    else
                    {
                        if (jsonobject.has("bold"))
                        {
                            style.bold = Boolean.valueOf(jsonobject.get("bold").getAsBoolean());
                        }

                        if (jsonobject.has("italic"))
                        {
                            style.italic = Boolean.valueOf(jsonobject.get("italic").getAsBoolean());
                        }

                        if (jsonobject.has("underlined"))
                        {
                            style.underlined = Boolean.valueOf(jsonobject.get("underlined").getAsBoolean());
                        }

                        if (jsonobject.has("strikethrough"))
                        {
                            style.strikethrough = Boolean.valueOf(jsonobject.get("strikethrough").getAsBoolean());
                        }

                        if (jsonobject.has("obfuscated"))
                        {
                            style.obfuscated = Boolean.valueOf(jsonobject.get("obfuscated").getAsBoolean());
                        }

                        if (jsonobject.has("color"))
                        {
                            style.color = (TextFormatting)p_deserialize_3_.deserialize(jsonobject.get("color"), TextFormatting.class);
                        }

                        if (jsonobject.has("insertion"))
                        {
                            style.insertion = jsonobject.get("insertion").getAsString();
                        }

                        if (jsonobject.has("clickEvent"))
                        {
                            JsonObject jsonobject1 = jsonobject.getAsJsonObject("clickEvent");

                            if (jsonobject1 != null)
                            {
                                JsonPrimitive jsonprimitive = jsonobject1.getAsJsonPrimitive("action");
                                ClickEvent.Action clickevent$action = jsonprimitive == null ? null : ClickEvent.Action.getValueByCanonicalName(jsonprimitive.getAsString());
                                JsonPrimitive jsonprimitive1 = jsonobject1.getAsJsonPrimitive("value");
                                String s = jsonprimitive1 == null ? null : jsonprimitive1.getAsString();

                                if (clickevent$action != null && s != null && clickevent$action.shouldAllowInChat())
                                {
                                    style.clickEvent = new ClickEvent(clickevent$action, s);
                                }
                            }
                        }

                        if (jsonobject.has("hoverEvent"))
                        {
                            JsonObject jsonobject2 = jsonobject.getAsJsonObject("hoverEvent");

                            if (jsonobject2 != null)
                            {
                                JsonPrimitive jsonprimitive2 = jsonobject2.getAsJsonPrimitive("action");
                                HoverEvent.Action hoverevent$action = jsonprimitive2 == null ? null : HoverEvent.Action.getValueByCanonicalName(jsonprimitive2.getAsString());
                                ITextComponent itextcomponent = (ITextComponent)p_deserialize_3_.deserialize(jsonobject2.get("value"), ITextComponent.class);

                                if (hoverevent$action != null && itextcomponent != null && hoverevent$action.shouldAllowInChat())
                                {
                                    style.hoverEvent = new HoverEvent(hoverevent$action, itextcomponent);
                                }
                            }
                        }

                        return style;
                    }
                }
                else
                {
                    return null;
                }
            }

            @Nullable
            public JsonElement serialize(Style p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
            {
                if (p_serialize_1_.isEmpty())
                {
                    return null;
                }
                else
                {
                    JsonObject jsonobject = new JsonObject();

                    if (p_serialize_1_.bold != null)
                    {
                        jsonobject.addProperty("bold", p_serialize_1_.bold);
                    }

                    if (p_serialize_1_.italic != null)
                    {
                        jsonobject.addProperty("italic", p_serialize_1_.italic);
                    }

                    if (p_serialize_1_.underlined != null)
                    {
                        jsonobject.addProperty("underlined", p_serialize_1_.underlined);
                    }

                    if (p_serialize_1_.strikethrough != null)
                    {
                        jsonobject.addProperty("strikethrough", p_serialize_1_.strikethrough);
                    }

                    if (p_serialize_1_.obfuscated != null)
                    {
                        jsonobject.addProperty("obfuscated", p_serialize_1_.obfuscated);
                    }

                    if (p_serialize_1_.color != null)
                    {
                        jsonobject.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
                    }

                    if (p_serialize_1_.insertion != null)
                    {
                        jsonobject.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
                    }

                    if (p_serialize_1_.clickEvent != null)
                    {
                        JsonObject jsonobject1 = new JsonObject();
                        jsonobject1.addProperty("action", p_serialize_1_.clickEvent.getAction().getCanonicalName());
                        jsonobject1.addProperty("value", p_serialize_1_.clickEvent.getValue());
                        jsonobject.add("clickEvent", jsonobject1);
                    }

                    if (p_serialize_1_.hoverEvent != null)
                    {
                        JsonObject jsonobject2 = new JsonObject();
                        jsonobject2.addProperty("action", p_serialize_1_.hoverEvent.getAction().getCanonicalName());
                        jsonobject2.add("value", p_serialize_3_.serialize(p_serialize_1_.hoverEvent.getValue()));
                        jsonobject.add("hoverEvent", jsonobject2);
                    }

                    return jsonobject;
                }
            }
        }
}