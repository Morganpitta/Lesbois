package morgan.lesbos.network.packet;

import io.github.apace100.apoli.Apoli;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.Active;
import io.github.apace100.apoli.power.type.PowerType;
import io.netty.buffer.ByteBuf;
import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import morgan.lesbos.powers.ActionOnKeyReleasePowerType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

// See UseActivePowerTypesC2SPacket.java
public record UseKeyReleasePowerTypesC2SPacket(List<Identifier> powerIds) implements CustomPayload {

    public static final Id<UseKeyReleasePowerTypesC2SPacket> ID = new Id<>(Lesbos.id("use_key_release_power_types"));
    public static final PacketCodec<RegistryByteBuf, UseKeyReleasePowerTypesC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, Identifier.PACKET_CODEC), UseKeyReleasePowerTypesC2SPacket::powerIds,
            UseKeyReleasePowerTypesC2SPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void handle(UseKeyReleasePowerTypesC2SPacket payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        PowerHolderComponent component = PowerHolderComponent.KEY.get(player);

        for (Identifier powerId : payload.powerIds()) {
            PowerType powerType = PowerManager.getOptional(powerId)
                    .map(component::getPowerType)
                    .orElse(null);

            if (powerType instanceof ActionOnKeyReleasePowerType keyReleasePowerType) {

                if (keyReleasePowerType.isActive()) {
                    keyReleasePowerType.onUse();
                }

            }
            else if (powerType != null) {
                Lesbos.LOGGER.warn("Unexpectedly found power \"{}\" (which doesn't have a key release power type) while receiving packet for triggering active power types of player {}!", powerId, player.getName().getString());
            }
            else {
                Lesbos.LOGGER.warn("Found unknown power \"{}\" while receiving packet for triggering key release power types of player {}!", powerId, player.getName().getString());
            }
        }
    }
}
