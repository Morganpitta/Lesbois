package morgan.lesbois.powers;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import io.github.apace100.calio.data.SerializableData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FrostGlidingPowerType extends PowerType {
    public static final TypedDataObjectFactory<FrostGlidingPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("key", ApoliDataTypes.BACKWARDS_COMPATIBLE_KEY, KeyBindingReference.NONE)
                    .add("entity_action", EntityAction.DATA_TYPE.optional(), Optional.empty()),
            (data, condition) -> new FrostGlidingPowerType(
                    data.get("key"),
                    data.get("entity_action"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("key", powerType.getKey())
                    .set("entity_action", powerType.entityAction)
    );

    private final KeyBindingReference key;
    private final Optional<EntityAction> entityAction;

    FrostGlidingPowerType(KeyBindingReference key, Optional<EntityAction> entityAction, Optional<EntityCondition> condition) {
        super(condition);
        this.key = key;
        this.entityAction = entityAction;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.FROST_GLIDE;
    }


    public void onUse() {
        this.entityAction.ifPresent(action -> action.execute(getHolder()));
    }

    public boolean isActive() {
        return super.isActive() && this.getHolder().getWorld().isClient() && key.asKeyBinding().map(KeyBinding::isPressed).orElse(false);
    }

    public KeyBindingReference getKey() {
        return key;
    }

    public static boolean shouldFrostGlide(PlayerEntity player) {
        return PowerHolderComponent.hasPowerType(player, FrostGlidingPowerType.class);
    }

    public static void triggerActions(PlayerEntity player) {
        if (player.getWorld().isClient()) return;

        PowerHolderComponent.getPowerTypes(player, FrostGlidingPowerType.class).forEach(FrostGlidingPowerType::onUse);
    }
}
