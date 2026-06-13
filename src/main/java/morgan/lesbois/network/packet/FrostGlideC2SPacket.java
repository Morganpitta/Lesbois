package morgan.lesbois.network.packet;

import morgan.lesbois.Lesbois;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public record FrostGlideC2SPacket(BlockPos pos) implements CustomPayload {
    public static final Id<FrostGlideC2SPacket> ID = new CustomPayload.Id<>(Lesbois.id("place_frost_block"));
    public static final PacketCodec<PacketByteBuf, FrostGlideC2SPacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, FrostGlideC2SPacket::pos,
            FrostGlideC2SPacket::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}