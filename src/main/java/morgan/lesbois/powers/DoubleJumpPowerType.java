package morgan.lesbois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DoubleJumpPowerType extends PowerType {
    private final int doubleJumps;
    private final double height;

    public static final TypedDataObjectFactory<DoubleJumpPowerType> DATA_FACTORY =
            PowerType.createConditionedDataFactory(
                    new SerializableData()
                        .add("double_jumps", SerializableDataTypes.INT, 1)
                        .add("height", SerializableDataTypes.DOUBLE, 1D),
                    (data, condition) -> new DoubleJumpPowerType(
                            data.get("double_jumps"),
                            data.get("height"),
                            condition
                    ),
                    (powerType, serializableData) -> serializableData.instance()
                            .set("double_jumps", powerType.doubleJumps)
                            .set("height", powerType.height)
            );

    public DoubleJumpPowerType(int doubleJumps, double height, Optional<EntityCondition> condition) {
        super(condition);
        this.doubleJumps = doubleJumps;
        this.height = height;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.DOUBLE_JUMP;
    }

    public static boolean canDoubleJump(PlayerEntity player) {
        return PowerHolderComponent.hasPowerType(player, DoubleJumpPowerType.class);
    }

    public static int getMaxDoubleJumps(PlayerEntity player) {
        return PowerHolderComponent.getPowerTypes(player, DoubleJumpPowerType.class).stream()
                .mapToInt(powerType -> powerType.doubleJumps).max()
                .orElse(0);
    }

    public static double getDoubleJumpHeight(PlayerEntity player) {
        return PowerHolderComponent.getPowerTypes(player, DoubleJumpPowerType.class).stream()
                .mapToDouble(powerType -> powerType.height).max()
                .orElse(0);
    }
}
