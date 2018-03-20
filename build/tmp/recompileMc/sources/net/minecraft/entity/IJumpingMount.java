package net.minecraft.entity;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IJumpingMount
{
    @SideOnly(Side.CLIENT)
    void setJumpPower(int jumpPowerIn);

    boolean canJump();

    void handleStartJump(int p_184775_1_);

    void handleStopJump();
}