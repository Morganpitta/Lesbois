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

public class ParryPowerType extends PowerType {
    private final int maxDurationTicks;
    private final int failedFalterTicks;
    private final int successFalterTicks;

    public static final TypedDataObjectFactory<ParryPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("max_duration_ticks", SerializableDataTypes.INT, 20)
                    .add("failed_falter_ticks", SerializableDataTypes.INT, 40)
                    .add("success_falter_ticks", SerializableDataTypes.INT, 40),
            (data, condition) -> new ParryPowerType(
                    data.get("max_duration_ticks"),
                    data.get("failed_falter_ticks"),
                    data.get("success_falter_ticks"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("max_duration_ticks", powerType.maxDurationTicks)
                    .set("failed_falter_ticks", powerType.failedFalterTicks)
                    .set("success_falter_ticks", powerType.successFalterTicks)
    );

    ParryPowerType(int maxDurationTicks, int failedFalterTicks, int successFalterTicks, Optional<EntityCondition> condition) {
        super(condition);
        this.maxDurationTicks = maxDurationTicks;
        this.failedFalterTicks = failedFalterTicks;
        this.successFalterTicks = successFalterTicks;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.PARRY;
    }

    public static boolean canParry(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return false;

        return component.getPowers(true).stream().anyMatch(power -> power.getType() instanceof ParryPowerType && power.isActive(player));
    }

    public static int getMaxDurationTicks(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return 0;

        return component.getPowers(true).stream()
            .filter(power -> power.getType() instanceof ParryPowerType && power.isActive(player))
            .mapToInt(power -> ((ParryPowerType) power.getType()).maxDurationTicks).max().orElse(0);
    }

    public static int getFailedFalterTicks(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return 0;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof ParryPowerType && power.isActive(player))
                .mapToInt(power -> ((ParryPowerType) power.getType()).failedFalterTicks).max().orElse(0);
    }

    public static int getSuccessFalterTicks(PlayerEntity player) {
        PowerHolderComponent component = PowerHolderComponent.getNullable(player);

        if (component == null) return 0;

        return component.getPowers(true).stream()
                .filter(power -> power.getType() instanceof ParryPowerType && power.isActive(player))
                .mapToInt(power -> ((ParryPowerType) power.getType()).successFalterTicks).max().orElse(0);
    }
}
