package mekanism.common.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import mekanism.quilt.NetworkContext;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

// Quilt: glorified wrapper over C2SPacket and S2CPacket
public interface IMekanismPacket extends C2SPacket, S2CPacket {

    void handle(NetworkContext context);

    default void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
        handle(new NetworkContext(server));
    }

    @Override
    default void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
        handle(new NetworkContext(client));
    }

    // Quilt: replaced with default handle methods above
    // static <PACKET extends IMekanismPacket> void handle(PACKET message, Supplier<NetworkContext> ctx) {
    //     if (message != null) {
    //         //Message should never be null unless something went horribly wrong decoding.
    //         // In which case we don't want to try enqueuing handling it, or set the packet as handled
    //         NetworkContext context = ctx.get();
    //         context.enqueueWork(() -> message.handle(context));
    //     }
    // }
}