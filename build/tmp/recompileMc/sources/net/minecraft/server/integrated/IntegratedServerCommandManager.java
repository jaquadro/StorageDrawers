package net.minecraft.server.integrated;

import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IntegratedServerCommandManager extends ServerCommandManager
{
    public IntegratedServerCommandManager(IntegratedServer server)
    {
        super(server);
    }
}