package morgan.lesbos.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbos.actions.LesbosActionTypes;
import morgan.lesbos.common.Util;
import morgan.lesbos.interfaces.GrappleInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShockwaveEntityActionType extends EntityActionType {
    private final double minSpeed;
    private final double distance;
    private final double damage;
    private final double knockback;

    public static final TypedDataObjectFactory<ShockwaveEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("minSpeed", SerializableDataTypes.DOUBLE, 0.5D)
                    .add("distance", SerializableDataTypes.DOUBLE, 15D)
                    .add("damage", SerializableDataTypes.DOUBLE, 5D)
                    .add("knockback", SerializableDataTypes.DOUBLE, 5D),
            data -> new ShockwaveEntityActionType(
                    data.get("minSpeed"),
                    data.get("distance"),
                    data.get("damage"),
                    data.get("knockback")),
            (actionType, serializableData) -> serializableData.instance()
                    .set("min_speed", actionType.minSpeed)
                    .set("distance", actionType.distance)
                    .set("damage", actionType.damage)
                    .set("knockback", actionType.knockback)
    );

    public ShockwaveEntityActionType(double minSpeed, double distance, double damage, double knockback) {
        this.minSpeed = minSpeed;
        this.distance = distance;
        this.damage = damage;
        this.knockback = knockback;
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof ServerPlayerEntity player)) {
            return;
        }

        Vec3d playerVelocity = player.getVelocity();
        double playerSpeed = playerVelocity.length();

        if (playerSpeed < this.minSpeed) return;

        playerVelocity = playerVelocity.normalize();

        List<Entity> entities = Util.getEntitiesInCone(player.getServerWorld(), player, player.getPos(), playerVelocity, this.distance, 90);

        player.setVelocity(Vec3d.ZERO);
        player.velocityDirty = true;
        player.velocityModified = true;

        entities.forEach(entity -> {
            Vec3d vectorToEntity = entity.getPos().subtract(player.getPos());
            double distanceToEntity = vectorToEntity.length() * playerSpeed;

            if ( distanceToEntity < 1 ) distanceToEntity = 1;

            entity.damage(entity.getDamageSources().playerAttack(player), (float) ((this.damage * playerSpeed)/distanceToEntity));
            entity.setVelocity(entity.getVelocity().add(vectorToEntity.normalize().multiply((this.knockback * playerSpeed)/distanceToEntity).add(0, this.knockback * playerSpeed/distanceToEntity, 0)));
        });
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesbosActionTypes.SHOCKWAVE;
    }
}