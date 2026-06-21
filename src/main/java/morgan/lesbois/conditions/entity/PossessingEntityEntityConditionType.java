package morgan.lesbois.conditions.entity;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import morgan.lesbois.conditions.LesboisConditionTypes;
import morgan.lesbois.interfaces.PossessionInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PossessingEntityEntityConditionType extends EntityConditionType {
    public static final TypedDataObjectFactory<PossessingEntityEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("entity_condition", EntityCondition.DATA_TYPE.optional()),
            data -> new PossessingEntityEntityConditionType(
                    data.get("entity_condition")
            ),
            (conditionType, serializableData) -> serializableData.instance()
                    .set("entity_condition", conditionType.entityCondition)
    );

    private final Optional<EntityCondition> entityCondition;

    public PossessingEntityEntityConditionType(Optional<EntityCondition> entityCondition) {
        this.entityCondition = entityCondition;
    }

    @Override
    public boolean test(EntityConditionContext context) {
        Entity player = context.entity();
        if (player instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) player).lesbois$getPossessedEntity();

            if (entity != null) {
                return entityCondition.map(condition -> condition.test(entity)).orElse(true);
            }
        }

        return false;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LesboisConditionTypes.POSSESSING_ENTITY;
    }
}
