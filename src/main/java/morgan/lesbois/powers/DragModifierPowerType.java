package morgan.lesbois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DragModifierPowerType extends PowerType {
    private final float airDrag;
    private final float friction;
    private final boolean slideMode;
    private final boolean ignoreBlockFriction;

    public static final TypedDataObjectFactory<DragModifierPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("air_drag", SerializableDataTypes.FLOAT, 0.91F)
                    .add("friction", SerializableDataTypes.FLOAT, 0.91F)
                    .add("slide_mode", SerializableDataTypes.BOOLEAN, false)
                    .add("ignore_block_friction", SerializableDataTypes.BOOLEAN, false),
            (data, condition) -> new DragModifierPowerType(
                    data.get("air_drag"),
                    data.get("friction"),
                    data.get("slide_mode"),
                    data.get("ignore_block_friction"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("air_drag", powerType.airDrag)
                    .set("friction", powerType.friction)
                    .set("slide_mode", powerType.slideMode)
                    .set("ignore_block_friction", powerType.ignoreBlockFriction)
    );

    public DragModifierPowerType(float airDrag, float friction, boolean slideMode, boolean ignoreBlockFriction, Optional<EntityCondition> condition) {
        super(condition);
        this.airDrag = Math.clamp(airDrag, 0, 1);
        this.friction = Math.clamp(friction, 0, 1);
        this.slideMode = slideMode;
        this.ignoreBlockFriction = ignoreBlockFriction;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.DRAG_MODIFIER;
    }

    public static float getAirDrag(LivingEntity entity) {
        return (float) PowerHolderComponent.getPowerTypes(entity, DragModifierPowerType.class).stream()
                .mapToDouble(powerType -> powerType.airDrag).max()
                .orElse(0.91F);
    }

    public static float getFriction(LivingEntity entity) {
        return (float) PowerHolderComponent.getPowerTypes(entity, DragModifierPowerType.class).stream()
                .mapToDouble(powerType -> powerType.friction).max()
                .orElse(0.91);
    }

    public static boolean hasSlideMode(LivingEntity entity) {
        return PowerHolderComponent.getPowerTypes(entity, DragModifierPowerType.class).stream()
                .anyMatch(powerType -> powerType.slideMode);
    }

    public static boolean shouldIgnoreBlockFriction(LivingEntity entity) {
        return PowerHolderComponent.getPowerTypes(entity, DragModifierPowerType.class).stream()
                .anyMatch(powerType -> powerType.ignoreBlockFriction);
    }
}
