package morgan.lesbois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.interfaces.GrappleInterface;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class GrappleEntityActionType extends EntityActionType {
    private final double maxDistance;
    private final double minDistance;
    private final double lookAssist;
    private final double pullSpeed;
    private final double damping;

    public static final TypedDataObjectFactory<GrappleEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("max_distance", SerializableDataTypes.DOUBLE, 20D)
                    .add("min_distance", SerializableDataTypes.DOUBLE, 2D)
                    .add("look_assist", SerializableDataTypes.DOUBLE, 1D)
                    .add("pull_speed", SerializableDataTypes.DOUBLE, 1D)
                    .add("damping", SerializableDataTypes.DOUBLE, 0.1D),
            data -> new GrappleEntityActionType(
                    data.get("max_distance"),
                    data.get("min_distance"),
                    data.get("pull_speed"),
                    data.get("look_assist"),
                    data.get("damping")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("max_distance", actionType.maxDistance)
                    .set("min_distance", actionType.minDistance)
                    .set("look_assist", actionType.lookAssist)
                    .set("pull_speed", actionType.pullSpeed)
                    .set("damping", actionType.damping)
    );

    public GrappleEntityActionType(double maxDistance, double minDistance, double pullSpeed, double lookAssist, double damping) {
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

        GrappleInterface player = (GrappleInterface) context.entity();

        player.lesbois$grapple(this.maxDistance, this.minDistance, this.pullSpeed, this.lookAssist, this.damping);
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesboisActionTypes.GRAPPLE;
    }
}