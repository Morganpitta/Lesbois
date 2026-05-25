package morgan.lesbos.powers;

import io.github.apace100.apoli.ApoliClient;
import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.networking.packet.c2s.UseActivePowerTypesC2SPacket;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.Active;
import io.github.apace100.apoli.power.type.ActiveCooldownPowerType;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.power.type.PowerTypes;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbos.network.packet.UseKeyReleasePowerTypesC2SPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
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
        return LesbosPowerTypes.ACTION_ON_KEY_RELEASE;
    }


    public KeyBindingReference getKey() {
        return key;
    }

    // See Apoli Active interface
    @Environment(EnvType.CLIENT)
    public static void integrateCallback(MinecraftClient client) {

        if (client.player == null) {
            return;
        }

        List<PowerType> powerTypes = PowerHolderComponent.getOptional(client.player).orElseThrow().getPowerTypes();
        List<ActionOnKeyReleasePowerType> triggeredPowerTypes = new LinkedList<>();

        Map<String, Boolean> currentKeybindingStates = new HashMap<>();
        for (PowerType powerType : powerTypes) {

            if (!(powerType instanceof ActionOnKeyReleasePowerType keyReleasePowerType)) {
                continue;
            }

            KeyBindingReference keyBindingReference = keyReleasePowerType.getKey();
            TriState keyPressed = keyBindingReference.asKeyBinding()
                    .map((key)->!key.isPressed() && key.wasPressed())
                    .map(TriState::of)
                    .orElse(TriState.DEFAULT);

            if (keyPressed == TriState.DEFAULT) {
                continue;
            }

            if (currentKeybindingStates.computeIfAbsent(keyBindingReference.id(), k -> keyPressed.get()) && (keyBindingReference.continuous() || !ApoliClient.lastKeyBindingStates.getOrDefault(keyBindingReference.id(), false))) {
                //noinspection unchecked
                triggeredPowerTypes.add(keyReleasePowerType);
            }

        }

        ApoliClient.lastKeyBindingStates.putAll(currentKeybindingStates);

        List<Identifier> powerTypeIds = triggeredPowerTypes
                .stream()
                .peek(powerType -> {if (powerType.isActive()) {powerType.onUse();}})
                .map(PowerType::getPower)
                .map(Power::getId)
                .toList();

        if (!powerTypeIds.isEmpty()) {
            ClientPlayNetworking.send(new UseKeyReleasePowerTypesC2SPacket(powerTypeIds));
        }

    }

    public void onUse() {
        entityAction.execute(getHolder());
    }

}