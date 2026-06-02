package morgan.lesbos.network.packet;

import morgan.lesbos.interfaces.PossessionInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class LesbosClientPackets {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PossessionS2CPacket.ID, LesbosClientPackets::handlePossessionPacket);
        ClientPlayNetworking.registerGlobalReceiver(UnPossessionS2CPacket.ID, LesbosClientPackets::handleUnPossessionPacket);
    }

    public static void handlePossessionPacket(PossessionS2CPacket payload, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player();
        Entity entity = player.getWorld().getEntityById(payload.entityId());

        if (entity instanceof MobEntity) {
            ((PossessionInterface) player).lesbos$possess((MobEntity) entity);
        }
    }

    public static void handleUnPossessionPacket(UnPossessionS2CPacket payload, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player();

        ((PossessionInterface) player).lesbos$unPossess();
    }
}