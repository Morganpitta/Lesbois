package morgan.lesbois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.interfaces.Grapple;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class GrappleEntityActionType extends EntityActionType {
    private final boolean disableFallDamage;
    private final float maxDistance;
    private final float minDistance;
    private final float lookAssist;
    private final float pullSpeed;
    private final float damping;

    public static final TypedDataObjectFactory<GrappleEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("disable_fall_damage", SerializableDataTypes.BOOLEAN, false)
                    .add("max_distance", SerializableDataTypes.FLOAT, 20F)
                    .add("min_distance", SerializableDataTypes.FLOAT, 2F)
                    .add("look_assist", SerializableDataTypes.FLOAT, 1F)
                    .add("pull_speed", SerializableDataTypes.FLOAT, 1F)
                    .add("damping", SerializableDataTypes.FLOAT, 0.1F),
            data -> new GrappleEntityActionType(
                    data.get("disable_fall_damage"),
                    data.get("max_distance"),
                    data.get("min_distance"),
                    data.get("pull_speed"),
                    data.get("look_assist"),
                    data.get("damping")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("disable_fall_damage", actionType.disableFallDamage)
                    .set("max_distance", actionType.maxDistance)
                    .set("min_distance", actionType.minDistance)
                    .set("look_assist", actionType.lookAssist)
                    .set("pull_speed", actionType.pullSpeed)
                    .set("damping", actionType.damping)
    );

    public GrappleEntityActionType(boolean disableFallDamage, float maxDistance, float minDistance, float pullSpeed, float lookAssist, float damping) {
        this.disableFallDamage = disableFallDamage;
        this.maxDistance = maxDistance;
        this.minDistance = minDistance;
        this.lookAssist = lookAssist;
        this.pullSpeed = pullSpeed;
        this.damping = damping;
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof ServerPlayerEntity)) {
            return;
        }

        Grapple player = (Grapple) context.entity();

        player.lesbois$grapple(this.maxDistance, this.minDistance, this.disableFallDamage, this.pullSpeed, this.lookAssist, this.damping);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesboisActionTypes.GRAPPLE;
    }
}