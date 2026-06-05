package morgan.lesbos.common;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
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
}
