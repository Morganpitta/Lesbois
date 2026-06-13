package morgan.lesbois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ParryPowerType extends PowerType {
    ParryPowerType(Optional<EntityCondition> condition) {
        super(condition);
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
}
