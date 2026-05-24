package morgan.lesbos.powers;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.NotNull;

public class GroundDistanceCondition extends EntityConditionType {

    public static final TypedDataObjectFactory<GroundDistanceCondition> DATA_FACTORY =
            TypedDataObjectFactory.simple(
                    new SerializableData()
                            .add("distance", SerializableDataTypes.DOUBLE, 10.0),
                    data -> new GroundDistanceCondition(data.get("distance")),
                    (conditionType, serializableData) -> serializableData.instance()
                            .set("distance", conditionType.distance)
            );

    private final double distance;

    public GroundDistanceCondition(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean test(EntityConditionContext context) {
        Entity entity = context.entity();

        Vec3d start = entity.getPos();
        Vec3d end = start.add(0, -distance, 0);

        RaycastContext raycastContext = new RaycastContext(
                start,
                end,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        );

        BlockHitResult result = entity.getWorld().raycast(raycastContext);

        return result.getType() == HitResult.Type.MISS;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LesbosConditions.GROUND_POUND_HEIGHT;
    }
}