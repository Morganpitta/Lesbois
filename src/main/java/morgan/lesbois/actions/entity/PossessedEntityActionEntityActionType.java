package morgan.lesbois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class PossessedEntityActionEntityActionType extends EntityActionType {
    private final EntityAction entityAction;
    public static final TypedDataObjectFactory<PossessedEntityActionEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("entity_action", EntityAction.DATA_TYPE),
            data -> new PossessedEntityActionEntityActionType(
                    data.get("entity_action")
            ),
            (actionType, serializableData) -> serializableData.instance()
                    .set("entity_action", actionType.entityAction)
    );

    public PossessedEntityActionEntityActionType(EntityAction entityAction) {
        this.entityAction = entityAction;
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof ServerPlayerEntity player)) {
            return;
        }

        MobEntity entity = ((PossessionInterface) player).lesbois$getPossessedEntity();

        if (entity != null) {
            entityAction.execute(entity);
        }
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesboisActionTypes.POSSESSED_ENTITY_ACTION;
    }
}