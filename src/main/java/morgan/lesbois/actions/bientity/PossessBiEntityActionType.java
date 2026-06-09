package morgan.lesbois.actions.bientity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.BiEntityActionContext;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class PossessBiEntityActionType extends BiEntityActionType {
    public static final TypedDataObjectFactory<PossessBiEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData(),
            data -> new PossessBiEntityActionType(),
            (actionType, serializableData) -> serializableData.instance()
    );

    @Override
    public void accept(BiEntityActionContext context) {
        Entity actor = context.actor();
        Entity target = context.target();

        if (actor instanceof PlayerEntity && target instanceof MobEntity) {
            ((PossessionInterface) actor).lesbois$possess((MobEntity) target);
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesboisActionTypes.POSSESS;
    }
}
