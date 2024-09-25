package com.jaquadro.minecraft.storagedrawers.core;

import com.jaquadro.minecraft.storagedrawers.network.CountUpdateMessage;
import com.jaquadro.minecraft.storagedrawers.network.PlayerBoolConfigMessage;
import com.texelsaurus.minecraft.chameleon.api.ChameleonInit;
import com.texelsaurus.minecraft.chameleon.service.ChameleonNetworking;

public class ModNetworking implements ChameleonInit
{
    public static final ModNetworking INSTANCE = new ModNetworking();

    @Override
    public void init () {
        ChameleonNetworking.registerPacket(CountUpdateMessage.TYPE, CountUpdateMessage.STREAM_CODEC, true);
        ChameleonNetworking.registerPacket(PlayerBoolConfigMessage.TYPE, PlayerBoolConfigMessage.STREAM_CODEC, false);
    }
}
