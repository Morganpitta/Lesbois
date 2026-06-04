package morgan.lesbos.conditions.entity;

import io.github.apace100.apoli.condition.ConditionConfiguration;
import io.github.apace100.apoli.condition.context.EntityConditionContext;
import io.github.apace100.apoli.condition.type.EntityConditionType;
import io.github.apace100.apoli.condition.type.entity.EntityTypeEntityConditionType;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbos.interfaces.GrappleInterface;
import morgan.lesbos.interfaces.PossessionInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

public class PossessedEntityConditionType extends EntityConditionType {
    private final EntityType<?> entityType;


    public static final TypedDataObjectFactory<PossessedEntityConditionType> DATA_FACTORY = TypedDataObjectFactory.simple(
            new SerializableData()
                    .add("entity_type", SerializableDataTypes.ENTITY_TYPE),
            data -> new PossessedEntityConditionType(
                    data.get("entity_type")
            ),
            (conditionType, serializableData) -> serializableData.instance()
                    .set("entity_type", conditionType.entityType)
    );

    public PossessedEntityConditionType(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean test(EntityConditionContext context) {
        Entity player = context.entity();
        if (player instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) player).lesbos$getPossessedEntity();

            if (entity != null) {
                return entity.getType().equals(entityType);
            }
        }

        return false;
    }

    @Override
    public @NotNull ConditionConfiguration<?> getConfig() {
        return LesbosEntityConditionTypes.POSSESSED_ENTITY;
    }
}
