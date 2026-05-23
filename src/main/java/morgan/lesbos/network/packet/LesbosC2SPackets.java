package morgan.lesbos.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class LesbosC2SPackets {
    public static void register() {
        PayloadTypeRegistry.playC2S().register(DoubleJumpC2SPacket.ID, DoubleJumpC2SPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpC2SPacket.ID, DoubleJumpC2SPacket::handle);
    }
}
