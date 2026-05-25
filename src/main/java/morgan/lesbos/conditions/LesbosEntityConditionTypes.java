package morgan.lesbos.conditions;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.ConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.condition.type.EntityConditionTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import morgan.lesbos.Lesbos;
import morgan.lesbos.powers.DoubleJumpPowerType;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LesbosEntityConditionTypes {
    public static void register() {
    }

    public static <T extends EntityConditionType> ConditionConfiguration<T> register(String path, TypedDataObjectFactory<T> dataFactory) {
        ConditionConfiguration<T> configuration = ConditionConfiguration.of(Lesbos.id(path), dataFactory);

        EntityConditionTypes.register(configuration);

        return configuration;
    }
}