package mekanism.common.network.to_client;

import mekanism.common.network.IMekanismPacket;
import mekanism.quilt.NetworkContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.BlockHitResult;

public class PacketLaserHitBlock implements IMekanismPacket {

    private final BlockHitResult result;

    public PacketLaserHitBlock(BlockHitResult result) {
        this.result = result;
    }

    @Override
    public void handle(NetworkContext context) {
        if (Minecraft.getInstance().level != null) {
            // TODO: Quilt: Confirm if replacing addBlockHitEffects with destroy is correct
            Minecraft.getInstance().particleEngine.destroy(result.getBlockPos(), Minecraft.getInstance().level.getBlockState(result.getBlockPos()));
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBlockHitResult(result);
    }

    public static PacketLaserHitBlock decode(FriendlyByteBuf buffer) {
        return new PacketLaserHitBlock(buffer.readBlockHitResult());
    }
}