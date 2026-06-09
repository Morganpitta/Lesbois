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

    public float getAirDrag() {
        return this.airDrag;
    }

    public float getFriction() {
        return this.friction;
    }

    public boolean getSlideMode() {
        return this.slideMode;
    }

    public boolean shouldIgnoreBlockFriction() {
        return this.ignoreBlockFriction;
    }

    public static float getAirDrag(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return 0.91F;

        return (float) component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DragModifierPowerType && power.isActive(entity))
                .mapToDouble(power -> ((DragModifierPowerType) power.getType()).getAirDrag()).max()
                .orElse(0.91);
    }

    public static float getFriction(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return 0.91F;

        return (float) component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DragModifierPowerType && power.isActive(entity))
                .mapToDouble(power -> ((DragModifierPowerType) power.getType()).getFriction()).max()
                .orElse(0.91);
    }

    public static boolean hasSlideMode(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return false;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DragModifierPowerType && power.isActive(entity))
                .anyMatch(power -> ((DragModifierPowerType) power.getType()).getSlideMode());
    }

    public static boolean shouldIgnoreBlockFriction(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return false;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DragModifierPowerType && power.isActive(entity))
                .anyMatch(power -> ((DragModifierPowerType) power.getType()).shouldIgnoreBlockFriction());
    }
}
