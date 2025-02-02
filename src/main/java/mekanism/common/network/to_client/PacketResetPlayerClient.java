package mekanism.common.network.to_client;

import java.util.UUID;
import mekanism.common.Mekanism;
import mekanism.common.network.IMekanismPacket;
import mekanism.quilt.NetworkContext;
import net.minecraft.network.FriendlyByteBuf;

public class PacketResetPlayerClient implements IMekanismPacket {

    private final UUID uuid;

    public PacketResetPlayerClient(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void handle(NetworkContext context) {
        Mekanism.playerState.clearPlayer(uuid, true);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
    }

    public static PacketResetPlayerClient decode(FriendlyByteBuf buffer) {
        return new PacketResetPlayerClient(buffer.readUUID());
    }
}
