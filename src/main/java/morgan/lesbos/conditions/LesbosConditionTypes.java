package morgan.lesbos.conditions;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import morgan.lesbos.Lesbos;
import morgan.lesbos.conditions.entity.GrapplingEntityConditionType;
import morgan.lesbos.conditions.entity.IsParryingEntityConditionType;
import morgan.lesbos.conditions.entity.PossessedEntityConditionEntityConditionType;

import java.util.function.Supplier;

public class LesbosConditionTypes {
    public static final ConditionConfiguration<GrapplingEntityConditionType> GRAPPLING = register("grappling", GrapplingEntityConditionType::new);
    public static final ConditionConfiguration<PossessedEntityConditionEntityConditionType> POSSESSED_ENTITY_CONDITION = register("possessed_entity_condition", PossessedEntityConditionEntityConditionType.DATA_FACTORY);
    public static final ConditionConfiguration<IsParryingEntityConditionType> IS_PARRYING = register("grappling", IsParryingEntityConditionType::new);

    public static void register() {
    }

    public static <T extends EntityConditionType> ConditionConfiguration<T> register(String path, TypedDataObjectFactory<T> dataFactory) {
        ConditionConfiguration<T> configuration = ConditionConfiguration.of(Lesbos.id(path), dataFactory);

        EntityConditionTypes.register(configuration);

        return configuration;
    }

    public static <T extends EntityConditionType> ConditionConfiguration<T> register(String path, Supplier<T> constructor) {
        ConditionConfiguration<T> configuration = ConditionConfiguration.simple(Lesbos.id(path), constructor);

        EntityConditionTypes.register(configuration);

        return configuration;
    }
}