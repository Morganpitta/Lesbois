package morgan.lesbos.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.PossessionInterface;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record UnPossessionS2CPacket() implements CustomPayload {
    public static final Id<UnPossessionS2CPacket> ID = new Id<>(Lesbos.id("un_possession"));
    public static final PacketCodec<ByteBuf, UnPossessionS2CPacket> CODEC = PacketCodec.unit(new UnPossessionS2CPacket());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}