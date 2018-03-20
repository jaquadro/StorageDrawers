package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CreativeTabs
{
    public static CreativeTabs[] CREATIVE_TAB_ARRAY = new CreativeTabs[12];
    public static final CreativeTabs BUILDING_BLOCKS = new CreativeTabs(0, "buildingBlocks")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.BRICK_BLOCK);
        }
    };
    public static final CreativeTabs DECORATIONS = new CreativeTabs(1, "decorations")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.DOUBLE_PLANT);
        }
        @SideOnly(Side.CLIENT)
        public int getIconItemDamage()
        {
            return BlockDoublePlant.EnumPlantType.PAEONIA.getMeta();
        }
    };
    public static final CreativeTabs REDSTONE = new CreativeTabs(2, "redstone")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.REDSTONE;
        }
    };
    public static final CreativeTabs TRANSPORTATION = new CreativeTabs(3, "transportation")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.GOLDEN_RAIL);
        }
    };
    public static final CreativeTabs MISC = (new CreativeTabs(4, "misc")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.LAVA_BUCKET;
        }
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.ALL});
    public static final CreativeTabs SEARCH = (new CreativeTabs(5, "search")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.COMPASS;
        }
    }).setBackgroundImageName("item_search.png");
    public static final CreativeTabs FOOD = new CreativeTabs(6, "food")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.APPLE;
        }
    };
    public static final CreativeTabs TOOLS = (new CreativeTabs(7, "tools")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.IRON_AXE;
        }
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE});
    public static final CreativeTabs COMBAT = (new CreativeTabs(8, "combat")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.GOLDEN_SWORD;
        }
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_CHEST, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON});
    public static final CreativeTabs BREWING = new CreativeTabs(9, "brewing")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.POTIONITEM;
        }
    };
    public static final CreativeTabs MATERIALS = new CreativeTabs(10, "materials")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Items.STICK;
        }
    };
    public static final CreativeTabs INVENTORY = (new CreativeTabs(11, "inventory")
    {
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem()
        {
            return Item.getItemFromBlock(Blocks.CHEST);
        }
    }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;
    /** Texture to use. */
    private String theTexture = "items.png";
    private boolean hasScrollbar = true;
    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean drawTitle = true;
    private EnumEnchantmentType[] enchantmentTypes;
    @SideOnly(Side.CLIENT)
    private ItemStack iconItemStack;

    public CreativeTabs(String label)
    {
        this(getNextID(), label);
    }

    public CreativeTabs(int index, String label)
    {
        if (index >= CREATIVE_TAB_ARRAY.length)
        {
            CreativeTabs[] tmp = new CreativeTabs[index + 1];
            for (int x = 0; x < CREATIVE_TAB_ARRAY.length; x++)
            {
                tmp[x] = CREATIVE_TAB_ARRAY[x];
            }
            CREATIVE_TAB_ARRAY = tmp;
        }
        this.tabIndex = index;
        this.tabLabel = label;
        CREATIVE_TAB_ARRAY[index] = this;
    }

    @SideOnly(Side.CLIENT)
    public int getTabIndex()
    {
        return this.tabIndex;
    }

    public CreativeTabs setBackgroundImageName(String texture)
    {
        this.theTexture = texture;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public String getTabLabel()
    {
        return this.tabLabel;
    }

    /**
     * Gets the translated Label.
     */
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel()
    {
        return "itemGroup." + this.getTabLabel();
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getIconItemStack()
    {
        if (this.iconItemStack == null)
        {
            this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
        }

        return this.iconItemStack;
    }

    @SideOnly(Side.CLIENT)
    public abstract Item getTabIconItem();

    @SideOnly(Side.CLIENT)
    public int getIconItemDamage()
    {
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public String getBackgroundImageName()
    {
        return this.theTexture;
    }

    @SideOnly(Side.CLIENT)
    public boolean drawInForegroundOfTab()
    {
        return this.drawTitle;
    }

    public CreativeTabs setNoTitle()
    {
        this.drawTitle = false;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldHidePlayerInventory()
    {
        return this.hasScrollbar;
    }

    public CreativeTabs setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    /**
     * returns index % 6
     */
    @SideOnly(Side.CLIENT)
    public int getTabColumn()
    {
        if (tabIndex > 11)
        {
            return ((tabIndex - 12) % 10) % 5;
        }
        return this.tabIndex % 6;
    }

    /**
     * returns tabIndex < 6
     */
    @SideOnly(Side.CLIENT)
    public boolean isTabInFirstRow()
    {
        if (tabIndex > 11)
        {
            return ((tabIndex - 12) % 10) < 5;
        }
        return this.tabIndex < 6;
    }

    /**
     * Returns the enchantment types relevant to this tab
     */
    @SideOnly(Side.CLIENT)
    public EnumEnchantmentType[] getRelevantEnchantmentTypes()
    {
        return this.enchantmentTypes;
    }

    /**
     * Sets the enchantment types for populating this tab with enchanting books
     */
    public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... types)
    {
        this.enchantmentTypes = types;
        return this;
    }

    @SideOnly(Side.CLIENT)
    public boolean hasRelevantEnchantmentType(EnumEnchantmentType enchantmentType)
    {
        if (this.enchantmentTypes == null)
        {
            return false;
        }
        else
        {
            for (EnumEnchantmentType enumenchantmenttype : this.enchantmentTypes)
            {
                if (enumenchantmenttype == enchantmentType)
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * only shows items which have tabToDisplayOn == this
     */
    @SideOnly(Side.CLIENT)
    public void displayAllRelevantItems(List<ItemStack> p_78018_1_)
    {
        for (Item item : Item.REGISTRY)
        {
            if (item == null)
            {
                continue;
            }
            for (CreativeTabs tab : item.getCreativeTabs())
            {
                if (tab == this)
                {
                    item.getSubItems(item, this, p_78018_1_);
                }
            }
        }

        if (this.getRelevantEnchantmentTypes() != null)
        {
            this.addEnchantmentBooksToList(p_78018_1_, this.getRelevantEnchantmentTypes());
        }
    }

    /**
     * Adds the enchantment books from the supplied EnumEnchantmentType to the given list.
     */
    @SideOnly(Side.CLIENT)
    public void addEnchantmentBooksToList(List<ItemStack> itemList, EnumEnchantmentType... enchantmentType)
    {
        for (Enchantment enchantment : Enchantment.REGISTRY)
        {
            if (enchantment != null && enchantment.type != null)
            {
                boolean flag = false;

                for (int i = 0; i < enchantmentType.length && !flag; ++i)
                {
                    if (enchantment.type == enchantmentType[i])
                    {
                        flag = true;
                    }
                }

                if (flag)
                {
                    itemList.add(Items.ENCHANTED_BOOK.getEnchantedItemStack(new EnchantmentData(enchantment, enchantment.getMaxLevel())));
                }
            }
        }
    }

    public int getTabPage()
    {
        if (tabIndex > 11)
        {
            return ((tabIndex - 12) / 10) + 1;
        }
        return 0;
    }

    public static int getNextID()
    {
        return CREATIVE_TAB_ARRAY.length;
    }

    /**
     * Determines if the search bar should be shown for this tab.
     *
     * @return True to show the bar
     */
    public boolean hasSearchBar()
    {
        return tabIndex == CreativeTabs.SEARCH.tabIndex;
    }

    /**
     * Gets the width of the search bar of the creative tab, use this if your
     * creative tab name overflows together with a custom texture.
     *
     * @return The width of the search bar, 89 by default
     */
    public int getSearchbarWidth()
    {
        return 89;
    }
}