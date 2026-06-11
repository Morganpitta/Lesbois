package morgan.lesbois.network.packet;

import morgan.lesbois.Lesbois;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record UseCoinPowerTypesC2SPacket(List<Identifier> powerIds) implements CustomPayload {
    public static final CustomPayload.Id<UseCoinPowerTypesC2SPacket> ID = new CustomPayload.Id<>(Lesbois.id("use_coin_power_types"));
    public static final PacketCodec<RegistryByteBuf, UseCoinPowerTypesC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, Identifier.PACKET_CODEC), UseCoinPowerTypesC2SPacket::powerIds,
            UseCoinPowerTypesC2SPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
