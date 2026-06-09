package morgan.lesbois.powers;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import io.github.apace100.calio.data.SerializableData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ActionOnKeyReleasePowerType extends PowerType {
    public static final TypedDataObjectFactory<ActionOnKeyReleasePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("entity_action", EntityAction.DATA_TYPE)
                    .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, KeyBindingReference.NONE),
            (data, condition) -> new ActionOnKeyReleasePowerType(
                    data.get("entity_action"),
                    data.get("key"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("entity_action", powerType.entityAction)
                    .set("key", powerType.getKey())
    );

    private final EntityAction entityAction;
    private final KeyBindingReference key;

    public ActionOnKeyReleasePowerType(EntityAction entityAction, KeyBindingReference key, Optional<EntityCondition> condition) {
        super(condition);
        this.entityAction = entityAction;
        this.key = key;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.ACTION_ON_KEY_RELEASE;
    }


    public KeyBindingReference getKey() {
        return key;
    }

    public void onUse() {
        entityAction.execute(getHolder());
    }
}