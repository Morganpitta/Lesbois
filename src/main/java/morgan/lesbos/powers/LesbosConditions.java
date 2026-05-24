package morgan.lesbos.powers;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.registry.ApoliRegistries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LesbosConditions {

    public static final ConditionConfiguration<GroundDistanceCondition> GROUND_POUND_HEIGHT =
            register(ConditionConfiguration.of(
                    Identifier.of("lesbos", "ground_pound_height"),
                    GroundDistanceCondition.DATA_FACTORY
            ));

    public static void register() {
        // Forces static initialisation
    }

    @SuppressWarnings("unchecked")
    private static <T extends ConditionConfiguration<?>> T register(T configuration) {
        ConditionConfiguration<EntityConditionType> casted = (ConditionConfiguration<EntityConditionType>) configuration;
        Registry.register(ApoliRegistries.ENTITY_CONDITION_TYPE, casted.id(), casted);
        return configuration;
    }
}