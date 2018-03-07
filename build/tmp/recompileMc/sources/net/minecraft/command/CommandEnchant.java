package net.minecraft.command;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandEnchant extends CommandBase
{
    /**
     * Gets the name of the command
     */
    public String getName()
    {
        return "enchant";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     */
    public String getUsage(ICommandSender sender)
    {
        return "commands.enchant.usage";
    }

    /**
     * Callback for when the command is executed
     */
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 2)
        {
            throw new WrongUsageException("commands.enchant.usage", new Object[0]);
        }
        else
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)getEntity(server, sender, args[0], EntityLivingBase.class);
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
            Enchantment enchantment;

            try
            {
                enchantment = Enchantment.getEnchantmentByID(parseInt(args[1], 0));
            }
            catch (NumberInvalidException var12)
            {
                enchantment = Enchantment.getEnchantmentByLocation(args[1]);
            }

            if (enchantment == null)
            {
                throw new NumberInvalidException("commands.enchant.notFound", new Object[] {Integer.valueOf(Enchantment.getEnchantmentID(enchantment))});
            }
            else
            {
                int i = 1;
                ItemStack itemstack = entitylivingbase.getHeldItemMainhand();

                if (itemstack == null)
                {
                    throw new CommandException("commands.enchant.noItem", new Object[0]);
                }
                else if (!enchantment.canApply(itemstack))
                {
                    throw new CommandException("commands.enchant.cantEnchant", new Object[0]);
                }
                else
                {
                    if (args.length >= 3)
                    {
                        i = parseInt(args[2], enchantment.getMinLevel(), enchantment.getMaxLevel());
                    }

                    if (itemstack.hasTagCompound())
                    {
                        NBTTagList nbttaglist = itemstack.getEnchantmentTagList();

                        if (nbttaglist != null)
                        {
                            for (int j = 0; j < nbttaglist.tagCount(); ++j)
                            {
                                int k = nbttaglist.getCompoundTagAt(j).getShort("id");

                                if (Enchantment.getEnchantmentByID(k) != null)
                                {
                                    Enchantment enchantment1 = Enchantment.getEnchantmentByID(k);

                                    if (!enchantment.canApplyTogether(enchantment1) || !enchantment1.canApplyTogether(enchantment)) //Forge BugFix: Let Both enchantments veto being together
                                    {
                                        throw new CommandException("commands.enchant.cantCombine", new Object[] {enchantment.getTranslatedName(i), enchantment1.getTranslatedName(nbttaglist.getCompoundTagAt(j).getShort("lvl"))});
                                    }
                                }
                            }
                        }
                    }

                    itemstack.addEnchantment(enchantment, i);
                    notifyCommandListener(sender, this, "commands.enchant.success", new Object[0]);
                    sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
                }
            }
        }
    }

    /**
     * Get a list of options for when the user presses the TAB key
     */
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.REGISTRY.getKeys()) : Collections.<String>emptyList());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }
}