package morgan.lesbos.conditions.entity;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import morgan.lesbos.conditions.LesbosConditionTypes;
import morgan.lesbos.interfaces.ParryInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class IsParryingEntityConditionType extends EntityConditionType {
    @Override
    public boolean test(EntityConditionContext context) {
        Entity entity = context.entity();
        if ( entity instanceof PlayerEntity) {
            return ((ParryInterface) entity).lesbos$isParrying();
        }
        else {
            return false;
        }
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LesbosConditionTypes.IS_PARRYING;
    }
}
