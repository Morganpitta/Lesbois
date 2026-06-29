package morgan.lesbois.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.interfaces.Grapple;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class UnGrappleEntityActionType extends EntityActionType {
    public static final TypedDataObjectFactory<UnGrappleEntityActionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData(),
            data -> new UnGrappleEntityActionType(),
            (actionType, serializableData) -> serializableData.instance()
    );

    public UnGrappleEntityActionType() {
    }

    @Override
    public void accept(EntityActionContext context) {
        if (!(context.entity() instanceof ServerPlayerEntity)) {
            return;
        }

        Grapple player = (Grapple) context.entity();

        player.lesbois$unGrapple();
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesboisActionTypes.UN_GRAPPLE;
    }
}