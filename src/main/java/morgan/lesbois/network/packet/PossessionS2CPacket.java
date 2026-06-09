package morgan.lesbois.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbois.Lesbois;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record PossessionS2CPacket(int entityId) implements CustomPayload {
    public static final Id<PossessionS2CPacket> ID = new Id<>(Lesbois.id("possession"));
    public static final PacketCodec<ByteBuf, PossessionS2CPacket> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, PossessionS2CPacket::entityId, PossessionS2CPacket::new);

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}