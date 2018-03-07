package net.minecraft.client.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>
{
    private static final List<KeyBinding> KEYBIND_ARRAY = Lists.<KeyBinding>newArrayList();
    private static final net.minecraftforge.client.settings.KeyBindingMap HASH = new net.minecraftforge.client.settings.KeyBindingMap();
    private static final Set<String> KEYBIND_SET = Sets.<String>newHashSet();
    private final String keyDescription;
    private final int keyCodeDefault;
    private final String keyCategory;
    private int keyCode;
    /** Is the key held down? */
    private boolean pressed;
    private int pressTime;

    public static void onTick(int keyCode)
    {
        if (keyCode != 0)
        {
            KeyBinding keybinding = HASH.lookupActive(keyCode);

            if (keybinding != null)
            {
                ++keybinding.pressTime;
            }
        }
    }

    public static void setKeyBindState(int keyCode, boolean pressed)
    {
        if (keyCode != 0)
        {
            for (KeyBinding keybinding : HASH.lookupAll(keyCode))

            if (keybinding != null)
            {
                keybinding.pressed = pressed;
            }
        }
    }

    public static void updateKeyBindState()
    {
        for (KeyBinding keybinding : KEYBIND_ARRAY)
        {
            try
            {
                setKeyBindState(keybinding.keyCode, keybinding.keyCode < 256 && Keyboard.isKeyDown(keybinding.keyCode));
            }
            catch (IndexOutOfBoundsException var3)
            {
                ;
            }
        }
    }

    public static void unPressAllKeys()
    {
        for (KeyBinding keybinding : KEYBIND_ARRAY)
        {
            keybinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash()
    {
        HASH.clearMap();

        for (KeyBinding keybinding : KEYBIND_ARRAY)
        {
            HASH.addKey(keybinding.keyCode, keybinding);
        }
    }

    public static Set<String> getKeybinds()
    {
        return KEYBIND_SET;
    }

    public KeyBinding(String description, int keyCode, String category)
    {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        KEYBIND_ARRAY.add(this);
        HASH.addKey(keyCode, this);
        KEYBIND_SET.add(category);
    }

    /**
     * Returns true if the key is pressed (used for continuous querying). Should be used in tickers.
     */
    public boolean isKeyDown()
    {
        return this.pressed && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    public String getKeyCategory()
    {
        return this.keyCategory;
    }

    /**
     * Returns true on the initial key press. For continuous querying use {@link isKeyDown()}. Should be used in key
     * events.
     */
    public boolean isPressed()
    {
        if (this.pressTime == 0)
        {
            return false;
        }
        else
        {
            --this.pressTime;
            return true;
        }
    }

    private void unpressKey()
    {
        this.pressTime = 0;
        this.pressed = false;
    }

    public String getKeyDescription()
    {
        return this.keyDescription;
    }

    public int getKeyCodeDefault()
    {
        return this.keyCodeDefault;
    }

    public int getKeyCode()
    {
        return this.keyCode;
    }

    public void setKeyCode(int keyCode)
    {
        this.keyCode = keyCode;
    }

    public int compareTo(KeyBinding p_compareTo_1_)
    {
        int i = I18n.format(this.keyCategory, new Object[0]).compareTo(I18n.format(p_compareTo_1_.keyCategory, new Object[0]));

        if (i == 0)
        {
            i = I18n.format(this.keyDescription, new Object[0]).compareTo(I18n.format(p_compareTo_1_.keyDescription, new Object[0]));
        }

        return i;
    }

    /****************** Forge Start *****************************/
    private net.minecraftforge.client.settings.KeyModifier keyModifierDefault = net.minecraftforge.client.settings.KeyModifier.NONE;
    private net.minecraftforge.client.settings.KeyModifier keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
    private net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext = net.minecraftforge.client.settings.KeyConflictContext.UNIVERSAL;

    /**
     * Convenience constructor for creating KeyBindings with keyConflictContext set.
     */
    public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, int keyCode, String category)
    {
        this(description, keyConflictContext, net.minecraftforge.client.settings.KeyModifier.NONE, keyCode, category);
    }

    /**
     * Convenience constructor for creating KeyBindings with keyConflictContext and keyModifier set.
     */
    public KeyBinding(String description, net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext, net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode, String category)
    {
        this.keyDescription = description;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
        this.keyCategory = category;
        this.keyConflictContext = keyConflictContext;
        this.keyModifier = keyModifier;
        this.keyModifierDefault = keyModifier;
        if (this.keyModifier.matches(keyCode))
        {
            this.keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
        }
        KEYBIND_ARRAY.add(this);
        HASH.addKey(keyCode, this);
        KEYBIND_SET.add(category);
    }

    /**
     * Checks that the key conflict context and modifier are active, and that the keyCode matches this binding.
     */
    public boolean isActiveAndMatches(int keyCode)
    {
        return keyCode != 0 && keyCode == this.getKeyCode() && getKeyConflictContext().isActive() && getKeyModifier().isActive(getKeyConflictContext());
    }

    public void setKeyConflictContext(net.minecraftforge.client.settings.IKeyConflictContext keyConflictContext)
    {
        this.keyConflictContext = keyConflictContext;
    }

    public net.minecraftforge.client.settings.IKeyConflictContext getKeyConflictContext()
    {
        return keyConflictContext;
    }

    public net.minecraftforge.client.settings.KeyModifier getKeyModifierDefault()
    {
        return keyModifierDefault;
    }

    public net.minecraftforge.client.settings.KeyModifier getKeyModifier()
    {
        return keyModifier;
    }

    public void setKeyModifierAndCode(net.minecraftforge.client.settings.KeyModifier keyModifier, int keyCode)
    {
        this.keyCode = keyCode;
        if (keyModifier.matches(keyCode))
        {
            keyModifier = net.minecraftforge.client.settings.KeyModifier.NONE;
        }
        HASH.removeKey(this);
        this.keyModifier = keyModifier;
        HASH.addKey(keyCode, this);
    }

    public void setToDefault()
    {
        setKeyModifierAndCode(getKeyModifierDefault(), getKeyCodeDefault());
    }

    public boolean isSetToDefaultValue()
    {
        return getKeyCode() == getKeyCodeDefault() && getKeyModifier() == getKeyModifierDefault();
    }

	/**
     * Returns true when the other keyBinding conflicts with this one
     */
    public boolean conflicts(KeyBinding other)
    {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
        {
            net.minecraftforge.client.settings.KeyModifier keyModifier = getKeyModifier();
            net.minecraftforge.client.settings.KeyModifier otherKeyModifier = other.getKeyModifier();
            if (keyModifier.matches(other.getKeyCode()) || otherKeyModifier.matches(getKeyCode()))
            {
                return true;
            }
            else if (getKeyCode() == other.getKeyCode())
            {
                return keyModifier == otherKeyModifier ||
                        // IN_GAME key contexts have a conflict when at least one modifier is NONE.
                        // For example: If you hold shift to crouch, you can still press E to open your inventory. This means that a Shift+E hotkey is in conflict with E.
                        // GUI and other key contexts do not have this limitation.
                        (getKeyConflictContext().conflicts(net.minecraftforge.client.settings.KeyConflictContext.IN_GAME) &&
                                (keyModifier == net.minecraftforge.client.settings.KeyModifier.NONE || otherKeyModifier == net.minecraftforge.client.settings.KeyModifier.NONE));
            }
        }
        return false;
    }

    /**
     * Returns true when one of the bindings' key codes conflicts with the other's modifier.
     */
    public boolean hasKeyCodeModifierConflict(KeyBinding other)
    {
        if (getKeyConflictContext().conflicts(other.getKeyConflictContext()) || other.getKeyConflictContext().conflicts(getKeyConflictContext()))
        {
            if (getKeyModifier().matches(other.getKeyCode()) || other.getKeyModifier().matches(getKeyCode()))
            {
                return true;
            }
        }
        return false;
    }

    public String getDisplayName()
    {
        return getKeyModifier().getLocalizedComboName(getKeyCode());
    }
    /****************** Forge End *****************************/
}