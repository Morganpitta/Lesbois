package morgan.lesbois.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbois.Lesbois;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record DoubleJumpC2SPacket() implements CustomPayload {
    public static final Id<DoubleJumpC2SPacket> ID = new Id<>(Lesbois.id("double_jump"));
    public static final PacketCodec<ByteBuf, DoubleJumpC2SPacket> CODEC = PacketCodec.unit(new DoubleJumpC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}