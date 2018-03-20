package net.minecraft.util.text;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.EntityNotFoundException;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TextComponentUtils
{
    public static ITextComponent processComponent(ICommandSender commandSender, ITextComponent component, Entity entityIn) throws CommandException
    {
        ITextComponent itextcomponent = null;

        if (component instanceof TextComponentScore)
        {
            TextComponentScore textcomponentscore = (TextComponentScore)component;
            String s = textcomponentscore.getName();

            if (EntitySelector.hasArguments(s))
            {
                List<Entity> list = EntitySelector.<Entity>matchEntities(commandSender, s, Entity.class);

                if (list.size() != 1)
                {
                    throw new EntityNotFoundException();
                }

                Entity entity = (Entity)list.get(0);

                if (entity instanceof EntityPlayer)
                {
                    s = entity.getName();
                }
                else
                {
                    s = entity.getCachedUniqueIdString();
                }
            }

            itextcomponent = entityIn != null && s.equals("*") ? new TextComponentScore(entityIn.getName(), textcomponentscore.getObjective()) : new TextComponentScore(s, textcomponentscore.getObjective());
            ((TextComponentScore)itextcomponent).resolve(commandSender);
        }
        else if (component instanceof TextComponentSelector)
        {
            String s1 = ((TextComponentSelector)component).getSelector();
            itextcomponent = EntitySelector.matchEntitiesToTextComponent(commandSender, s1);

            if (itextcomponent == null)
            {
                itextcomponent = new TextComponentString("");
            }
        }
        else if (component instanceof TextComponentString)
        {
            itextcomponent = new TextComponentString(((TextComponentString)component).getText());
        }
        else
        {
            if (!(component instanceof TextComponentTranslation))
            {
                return component;
            }

            Object[] aobject = ((TextComponentTranslation)component).getFormatArgs();

            for (int i = 0; i < aobject.length; ++i)
            {
                Object object = aobject[i];

                if (object instanceof ITextComponent)
                {
                    aobject[i] = processComponent(commandSender, (ITextComponent)object, entityIn);
                }
            }

            itextcomponent = new TextComponentTranslation(((TextComponentTranslation)component).getKey(), aobject);
        }

        Style style = component.getStyle();

        if (style != null)
        {
            itextcomponent.setStyle(style.createShallowCopy());
        }

        for (ITextComponent itextcomponent1 : component.getSiblings())
        {
            itextcomponent.appendSibling(processComponent(commandSender, itextcomponent1, entityIn));
        }

        return itextcomponent;
    }
}