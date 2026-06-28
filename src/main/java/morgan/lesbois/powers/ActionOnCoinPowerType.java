package morgan.lesbois.powers;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ActionOnCoinPowerType extends PowerType {
    private final EntityAction entityAction;

    public static final TypedDataObjectFactory<ActionOnCoinPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("entity_action", EntityAction.DATA_TYPE),
            (data, condition) -> new ActionOnCoinPowerType(
                    data.get("entity_action"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("entity_action", powerType.entityAction)
    );

    public ActionOnCoinPowerType(EntityAction entityAction, Optional<EntityCondition> condition) {
        super(condition);
        this.entityAction = entityAction;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.ACTION_ON_COIN;
    }

    public void onUse() {
        entityAction.execute(getHolder());
    }

    public static void triggerCoinActions(PlayerEntity player) {
        PowerHolderComponent.getPowerTypes(player, ActionOnCoinPowerType.class).forEach(ActionOnCoinPowerType::onUse);
    }
}