package morgan.lesbos.network.packet;

import morgan.lesbos.Lesbos;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record UseParryPowerTypesS2CPacket(List<Identifier> powerIds) implements CustomPayload {
    public static final Id<UseParryPowerTypesS2CPacket> ID = new Id<>(Lesbos.id("use_parry_power_types"));
    public static final PacketCodec<RegistryByteBuf, UseParryPowerTypesS2CPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, Identifier.PACKET_CODEC), UseParryPowerTypesS2CPacket::powerIds,
            UseParryPowerTypesS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
