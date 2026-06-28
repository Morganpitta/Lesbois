package morgan.lesbois.network.packet;

import morgan.lesbois.Lesbois;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record FlyingC2SPacket(boolean isFlying) implements CustomPayload {
    public static final CustomPayload.Id<FlyingC2SPacket> ID = new CustomPayload.Id<>(Lesbois.id("flying"));
    public static final PacketCodec<RegistryByteBuf, FlyingC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, FlyingC2SPacket::isFlying,
            FlyingC2SPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}