package com.donraf.recycleitmod.network;

import com.donraf.recycleitmod.network.packet.RecycleItModMessage;
import com.donraf.recycleitmod.screen.SynthesizerMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class RecycleItModPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = ChannelBuilder.named("recycleitmod").simpleChannel();

    public static void registerMessages(){
        INSTANCE.messageBuilder(RecycleItModMessage.class)
                .encoder(RecycleItModMessage::encode)
                .decoder(RecycleItModMessage::decode)
                .consumer(RecycleItModMessage::handle).add().build();
    }

    public static void handle(RecycleItModMessage msg, CustomPayloadEvent.Context ctx){
        int item = msg.item();
        ServerPlayer player = ctx.getSender();
        if (player.containerMenu instanceof SynthesizerMenu synthesizerMenu) {
            if (!synthesizerMenu.stillValid(player)) {
                return;
            }
            synthesizerMenu.giveItem(player, item);
        }
    }

}
