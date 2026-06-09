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

public class PossessedEntityConditionEntityConditionType extends EntityConditionType {
    private final EntityCondition entityCondition;


    public static final TypedDataObjectFactory<PossessedEntityConditionEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("entity_condition", EntityCondition.DATA_TYPE),
            data -> new PossessedEntityConditionEntityConditionType(
                    data.get("entity_condition")
            ),
            (conditionType, serializableData) -> serializableData.instance()
                    .set("entity_condition", conditionType.entityCondition)
    );

    public PossessedEntityConditionEntityConditionType(EntityCondition entityCondition) {
        this.entityCondition = entityCondition;
    }

    @Override
    public boolean test(EntityConditionContext context) {
        Entity player = context.entity();
        if (player instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) player).lesbois$getPossessedEntity();

            if (entity != null) {
                return entityCondition.test(entity);
            }
        }

        return false;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LesboisConditionTypes.POSSESSED_ENTITY_CONDITION;
    }
}
