package morgan.lesbois.powers;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.DamageCondition;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnParryPowerType extends PowerType {
    private final EntityAction entityAction;
    private final Optional<DamageCondition> damageCondition;

    public static final TypedDataObjectFactory<ActionOnParryPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("entity_action", EntityAction.DATA_TYPE)
                    .add("damage_condition", DamageCondition.DATA_TYPE.optional(), Optional.empty()),
            (data, condition) -> new ActionOnParryPowerType(
                    data.get("entity_action"),
                    data.get("damage_condition"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("entity_action", powerType.entityAction)
                    .set("damage_condition", powerType.damageCondition)
    );


    public ActionOnParryPowerType(EntityAction entityAction, Optional<DamageCondition> damageCondition, Optional<EntityCondition> condition) {
        super(condition);
        this.entityAction = entityAction;
        this.damageCondition = damageCondition;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.ACTION_ON_PARRY;
    }


    public void onUse() {
        entityAction.execute(getHolder());
    }

    public boolean doesApply(DamageSource source, float amount) {
        return damageCondition.map(condition -> condition.test(source, amount)).orElse(true);
    }

    public static void triggerParryActions(PlayerEntity player, DamageSource source, float amount) {
        if (!player.getWorld().isClient()) {
            PowerHolderComponent.getPowerTypes(player, ActionOnParryPowerType.class).stream()
                    .filter(powerType -> powerType.doesApply(source, amount))
                    .forEach(ActionOnParryPowerType::onUse);
        }
    }
}