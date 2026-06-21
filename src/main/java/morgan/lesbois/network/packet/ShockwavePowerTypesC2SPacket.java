package morgan.lesbois.network.packet;

import morgan.lesbois.Lesbois;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public record ShockwavePowerTypesC2SPacket(List<Identifier> powerIds, Vector3f velocity, Vector3f pos) implements CustomPayload {
    public static final Id<ShockwavePowerTypesC2SPacket> ID = new Id<>(Lesbois.id("shockwave_power_types"));
    public static final PacketCodec<RegistryByteBuf, ShockwavePowerTypesC2SPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, Identifier.PACKET_CODEC), ShockwavePowerTypesC2SPacket::powerIds,
            PacketCodecs.VECTOR3F, ShockwavePowerTypesC2SPacket::velocity,
            PacketCodecs.VECTOR3F, ShockwavePowerTypesC2SPacket::pos,
            ShockwavePowerTypesC2SPacket::new
    );

    public ShockwavePowerTypesC2SPacket(List<Identifier> powerIds, Vec3d velocity, Vec3d pos) {
        this(
            powerIds,
            new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z),
            new Vector3f((float) pos.x, (float) pos.y, (float) pos.z)
        );
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
