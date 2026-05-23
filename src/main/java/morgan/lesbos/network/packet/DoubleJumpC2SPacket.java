package morgan.lesbos.network.packet;

import io.netty.buffer.ByteBuf;
import morgan.lesbos.Lesbos;
import morgan.lesbos.components.DoubleJumpComponent;
import morgan.lesbos.components.LesbosComponents;
import morgan.lesbos.interfaces.DoubleJumpInterface;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public record DoubleJumpC2SPacket() implements CustomPayload {
    public static final Id<DoubleJumpC2SPacket> ID = new Id<>(Lesbos.id("double_jump"));
    public static final PacketCodec<ByteBuf, DoubleJumpC2SPacket> CODEC = PacketCodec.unit(new DoubleJumpC2SPacket());

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }

    public static void handle(DoubleJumpC2SPacket payload, ServerPlayNetworking.Context context) {
        MinecraftServer server = context.player().getServer();

        if ( server == null ) return;

        server.execute(() -> {
            ServerPlayerEntity player = context.player();
            DoubleJumpComponent comp = LesbosComponents.DOUBLE_JUMP.get(player);
            if (comp.getDoubleJumps() > 0) {
                ((DoubleJumpInterface) player).lesbos$doubleJump();
            }
        });
    }
}