package morgan.lesbois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DisableHungerPowerType extends PowerType {
    DisableHungerPowerType(Optional<EntityCondition> condition) {
        super(condition);
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.DISABLE_HUNGER;
    }

    public static boolean shouldDisableHunger(PlayerEntity player) {
        return PowerHolderComponent.hasPowerType(player, DisableHungerPowerType.class);
    }
}
