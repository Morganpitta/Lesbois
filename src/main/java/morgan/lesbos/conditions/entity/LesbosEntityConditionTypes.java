package morgan.lesbos.conditions.entity;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.ConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import morgan.lesbos.Lesbos;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class LesbosEntityConditionTypes {
    public static final ConditionConfiguration<GrapplingEntityConditionType> GRAPPLING = register("grappling", GrapplingEntityConditionType::new);

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