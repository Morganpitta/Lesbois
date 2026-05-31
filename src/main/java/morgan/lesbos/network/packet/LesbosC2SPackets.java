package morgan.lesbos.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class LesbosC2SPackets {
    public static void register() {
        // Payload registry
        PayloadTypeRegistry.playC2S().register(DoubleJumpC2SPacket.ID, DoubleJumpC2SPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(UseKeyReleasePowerTypesC2SPacket.ID, UseKeyReleasePowerTypesC2SPacket.CODEC);


        // Handler registry
        ServerPlayNetworking.registerGlobalReceiver(DoubleJumpC2SPacket.ID, DoubleJumpC2SPacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(UseKeyReleasePowerTypesC2SPacket.ID, UseKeyReleasePowerTypesC2SPacket::handle);
    }
}
