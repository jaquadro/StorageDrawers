package net.minecraft.stats;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Achievement extends StatBase
{
    /** Is the column (related to center of achievement gui, in 24 pixels unit) that the achievement will be displayed. */
    public final int displayColumn;
    /** Is the row (related to center of achievement gui, in 24 pixels unit) that the achievement will be displayed. */
    public final int displayRow;
    /** Holds the parent achievement, that must be taken before this achievement is avaiable. */
    public final Achievement parentAchievement;
    /** Holds the description of the achievement, ready to be formatted and/or displayed. */
    private final String achievementDescription;
    /**
     * Holds a string formatter for the achievement, some of then needs extra dynamic info - like the key used to open
     * the inventory.
     */
    @SideOnly(Side.CLIENT)
    private IStatStringFormat statStringFormatter;
    /** Holds the ItemStack that will be used to draw the achievement into the GUI. */
    public final ItemStack theItemStack;
    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    private boolean isSpecial;

    public Achievement(String statIdIn, String unlocalizedName, int column, int row, Item itemIn, Achievement parent)
    {
        this(statIdIn, unlocalizedName, column, row, new ItemStack(itemIn), parent);
    }

    public Achievement(String statIdIn, String unlocalizedName, int column, int row, Block blockIn, Achievement parent)
    {
        this(statIdIn, unlocalizedName, column, row, new ItemStack(blockIn), parent);
    }

    public Achievement(String statIdIn, String unlocalizedName, int column, int row, ItemStack stack, Achievement parent)
    {
        super(statIdIn, new TextComponentTranslation("achievement." + unlocalizedName, new Object[0]));
        this.theItemStack = stack;
        this.achievementDescription = "achievement." + unlocalizedName + ".desc";
        this.displayColumn = column;
        this.displayRow = row;

        if (column < AchievementList.minDisplayColumn)
        {
            AchievementList.minDisplayColumn = column;
        }

        if (row < AchievementList.minDisplayRow)
        {
            AchievementList.minDisplayRow = row;
        }

        if (column > AchievementList.maxDisplayColumn)
        {
            AchievementList.maxDisplayColumn = column;
        }

        if (row > AchievementList.maxDisplayRow)
        {
            AchievementList.maxDisplayRow = row;
        }

        this.parentAchievement = parent;
    }

    /**
     * Initializes the current stat as independent (i.e., lacking prerequisites for being updated) and returns the
     * current instance.
     */
    public Achievement initIndependentStat()
    {
        this.isIndependent = true;
        return this;
    }

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    public Achievement setSpecial()
    {
        this.isSpecial = true;
        return this;
    }

    /**
     * Register the stat into StatList.
     */
    public Achievement registerStat()
    {
        super.registerStat();
        AchievementList.ACHIEVEMENTS.add(this);
        return this;
    }

    /**
     * Returns whether or not the StatBase-derived class is a statistic (running counter) or an achievement (one-shot).
     */
    public boolean isAchievement()
    {
        return true;
    }

    public ITextComponent getStatName()
    {
        ITextComponent itextcomponent = super.getStatName();
        itextcomponent.getStyle().setColor(this.getSpecial() ? TextFormatting.DARK_PURPLE : TextFormatting.GREEN);
        return itextcomponent;
    }

    public Achievement setSerializableClazz(Class <? extends IJsonSerializable > clazz)
    {
        return (Achievement)super.setSerializableClazz(clazz);
    }

    /**
     * Returns the fully description of the achievement - ready to be displayed on screen.
     */
    @SideOnly(Side.CLIENT)
    public String getDescription()
    {
        return this.statStringFormatter != null ? this.statStringFormatter.formatString(I18n.translateToLocal(this.achievementDescription)) : I18n.translateToLocal(this.achievementDescription);
    }

    /**
     * Defines a string formatter for the achievement.
     */
    @SideOnly(Side.CLIENT)
    public Achievement setStatStringFormatter(IStatStringFormat statStringFormatterIn)
    {
        this.statStringFormatter = statStringFormatterIn;
        return this;
    }

    /**
     * Special achievements have a 'spiked' (on normal texture pack) frame, special achievements are the hardest ones to
     * achieve.
     */
    public boolean getSpecial()
    {
        return this.isSpecial;
    }
}