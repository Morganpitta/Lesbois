package morgan.lesbos.network.packet;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import morgan.lesbos.Lesbos;
import morgan.lesbos.powers.ActionOnKeyReleasePowerType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
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
}
