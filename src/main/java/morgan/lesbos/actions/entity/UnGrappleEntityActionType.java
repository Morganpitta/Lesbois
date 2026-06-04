package morgan.lesbos.actions.entity;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.context.EntityActionContext;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import morgan.lesbos.actions.LesbosActionTypes;
import morgan.lesbos.interfaces.GrappleInterface;
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

        GrappleInterface player = (GrappleInterface) context.entity();

        player.lesbos$unGrapple();
    }

    @Override
    public @NotNull ActionConfiguration<?> getConfig() {
        return LesbosActionTypes.UN_GRAPPLE;
    }
}