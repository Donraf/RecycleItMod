package com.donraf.recycleitmod.network.packet;


import com.donraf.recycleitmod.network.RecycleItModPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

public record RecycleItModMessage(int item) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.item);
    }

    public static RecycleItModMessage decode(FriendlyByteBuf buf) {
        return new RecycleItModMessage(buf.readVarInt());
    }

    public static void handle(RecycleItModMessage msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RecycleItModPacketHandler.handle(msg, ctx)));
        ctx.setPacketHandled(true);
    }
}
