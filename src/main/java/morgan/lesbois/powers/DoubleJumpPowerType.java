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

public class DoubleJumpPowerType extends PowerType {
    private final int doubleJumps;

    public static final TypedDataObjectFactory<DoubleJumpPowerType> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData().add("double_jumps", SerializableDataTypes.INT, 1),
                    (data, condition) -> new DoubleJumpPowerType(
                            data.get("double_jumps"),
                            condition
                    ),
                    (powerType, serializableData) -> serializableData.instance()
                            .set("double_jumps", powerType.doubleJumps)
            );

    public DoubleJumpPowerType(int doubleJumps, Optional<EntityCondition> condition) {
        super(condition);
        this.doubleJumps = doubleJumps;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.DOUBLE_JUMP;
    }

    public int getDoubleJumps() {
        return doubleJumps;
    }

    public static boolean canDoubleJump(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if ( component == null ) return false;

        return component.getPowers(true).stream().anyMatch(power -> (power.getType() instanceof DoubleJumpPowerType && power.isActive(entity)));
    }

    public static int getMaxDoubleJumps(LivingEntity entity) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(entity);

        if (component == null) return 0;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof DoubleJumpPowerType && power.isActive(entity))
                .mapToInt(power -> ((DoubleJumpPowerType) power.getType()).getDoubleJumps()).max()
                .orElse(0);
    }
}
