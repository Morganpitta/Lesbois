package morgan.lesbois.common;

import morgan.lesbois.network.packet.MovingSoundS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class Util {
    public static List<Entity> getEntitiesInCone(ServerWorld world, Entity except, Vec3d origin, Vec3d direction, double distance, double coneAngle) {
        Box searchBox = new Box(origin, origin).expand(distance);

        double cosHalfAngle = Math.cos(Math.toRadians(coneAngle / 2.0));
        Vec3d directionNormalised = direction.normalize();

        return world.getOtherEntities(except, searchBox).stream()
                .filter(entity -> {
                    Vec3d vectorToEntity = entity.getPos().subtract(origin);
                    double distSq = vectorToEntity.lengthSquared();

                    if (distSq > distance * distance) return false;

                    if (distSq < 0.0001) {
                        return true;
                    }

                    double dotProduct = directionNormalised.dotProduct(vectorToEntity.normalize());

                    return dotProduct >= cosHalfAngle;
                }).toList();
    }

    public static <T extends ParticleEffect> int spawnParticles(ServerWorld world, T particle, double x, double y, double z, int count, double deltaX, double deltaY, double deltaZ, double speed, boolean force) {
        ParticleS2CPacket particleS2CPacket = new ParticleS2CPacket(particle, false, x, y, z, (float)deltaX, (float)deltaY, (float)deltaZ, (float)speed, count);
        int i = 0;

        for (int j = 0; j < world.getPlayers().size(); j++) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)world.getPlayers().get(j);
            if (world.sendToPlayerIfNearby(serverPlayerEntity, force, x, y, z, particleS2CPacket)) {
                i++;
            }
        }

        return i;
    }

    public static void sendMovingSound(SoundEvent sound, float volume, float pitch, Entity entity, long seed)
     {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            MovingSoundS2CPacket packet = new MovingSoundS2CPacket(sound.getId(), volume, pitch, entity.getId(), seed);

            if ( entity instanceof ServerPlayerEntity )
            {
                for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                    ServerPlayNetworking.send(player, packet);
                }
            }
            else {
                for (ServerPlayerEntity player : PlayerLookup.tracking(entity)) {
                    ServerPlayNetworking.send(player, packet);
                }
            }
        }
    }
}
