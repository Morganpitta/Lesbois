package morgan.lesbos.actions;

import io.github.apace100.apoli.action.ActionConfiguration;
import io.github.apace100.apoli.action.type.BiEntityActionType;
import io.github.apace100.apoli.action.type.BiEntityActionTypes;
import io.github.apace100.apoli.action.type.EntityActionType;
import io.github.apace100.apoli.action.type.EntityActionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import morgan.lesbos.Lesbos;
import morgan.lesbos.actions.bientity.PossessBiEntityActionType;
import morgan.lesbos.actions.entity.GrappleEntityActionType;
import morgan.lesbos.actions.entity.UnGrappleEntityActionType;

public class LesbosActionTypes {
    public static final ActionConfiguration<GrappleEntityActionType> GRAPPLE = registerEntityAction("grapple", GrappleEntityActionType.DATA_FACTORY);
    public static final ActionConfiguration<UnGrappleEntityActionType> UN_GRAPPLE = registerEntityAction("un_grapple", UnGrappleEntityActionType.DATA_FACTORY);
    public static final ActionConfiguration<PossessBiEntityActionType> POSSESS = registerBiEntityAction("possess", PossessBiEntityActionType.DATA_FACTORY);

    public static void register() {
    }

    public static <T extends EntityActionType> ActionConfiguration<T> registerEntityAction(String path, TypedDataObjectFactory<T> dataFactory) {
        ActionConfiguration<T> configuration = ActionConfiguration.of(Lesbos.id(path), dataFactory);

        EntityActionTypes.register(configuration);

        return configuration;
    }

    public static <T extends BiEntityActionType> ActionConfiguration<T> registerBiEntityAction(String path, TypedDataObjectFactory<T> dataFactory) {
        ActionConfiguration<T> configuration = ActionConfiguration.of(Lesbos.id(path), dataFactory);

        BiEntityActionTypes.register(configuration);

        return configuration;
    }
}
