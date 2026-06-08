package morgan.lesbos.network.packet;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.PowerManager;
import io.github.apace100.apoli.power.type.PowerType;
import morgan.lesbos.Lesbos;
import morgan.lesbos.interfaces.PossessionInterface;
import morgan.lesbos.powers.ActionOnKeyReleasePowerType;
import morgan.lesbos.powers.ActionOnParryPowerType;
import morgan.lesbos.sound.LivingEntityTrackingSoundInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LesbosClientPackets {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PossessionS2CPacket.ID, LesbosClientPackets::handlePossessionPacket);
        ClientPlayNetworking.registerGlobalReceiver(UnPossessionS2CPacket.ID, LesbosClientPackets::handleUnPossessionPacket);
        ClientPlayNetworking.registerGlobalReceiver(MovingSoundS2CPacket.ID, LesbosClientPackets::handleMovingSoundPacket);
    }

    public static void handlePossessionPacket(PossessionS2CPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> tryPossess(context.player(), payload.entityId(), 20));
    }

    private static void tryPossess(PlayerEntity player, int entityId, int retries) {
        if (player == null || player.getWorld() == null) return;

        Entity entity = player.getWorld().getEntityById(entityId);

        if (entity instanceof MobEntity mob) {
            ((PossessionInterface) player).lesbos$possess(mob);
        }
        else {
            if (retries > 0) {
                MinecraftClient.getInstance().execute(() -> {
                    tryPossess(player, entityId, retries - 1);
                });
            }
            else {
                Lesbos.LOGGER.warn("Failed to possess entity ID {}, entity was never found", entityId);
            }
        }
    }

    public static void handleUnPossessionPacket(UnPossessionS2CPacket payload, ClientPlayNetworking.Context context) {
        PlayerEntity player = context.player();

        ((PossessionInterface) player).lesbos$unPossess();
    }

    public static void handleMovingSoundPacket(MovingSoundS2CPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            var client = context.client();
            if (client.world == null) return;

            Entity entity = client.world.getEntityById(payload.entityId());
            SoundEvent sound = Registries.SOUND_EVENT.get(payload.soundId());

            if (entity instanceof LivingEntity && sound != null) {
                client.getSoundManager().play(
                        new LivingEntityTrackingSoundInstance(sound, SoundCategory.PLAYERS, payload.volume(), payload.pitch(), (LivingEntity) entity, payload.seed())
                );
            }
        });
    }
}