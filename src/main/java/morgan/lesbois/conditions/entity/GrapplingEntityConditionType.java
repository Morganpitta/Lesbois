package morgan.lesbois.conditions.entity;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import morgan.lesbois.conditions.LesboisConditionTypes;
import morgan.lesbois.interfaces.GrappleInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class GrapplingEntityConditionType extends EntityConditionType {
    @Override
    public boolean test(EntityConditionContext context) {
        Entity entity = context.entity();
        if ( entity instanceof PlayerEntity ) {
            return ((GrappleInterface) entity).lesbois$getGrappleHook() != null;
        }
        else {
            return false;
        }
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LesboisConditionTypes.GRAPPLING;
    }
}
