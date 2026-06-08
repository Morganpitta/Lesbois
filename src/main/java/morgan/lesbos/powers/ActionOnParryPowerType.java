package morgan.lesbos.powers;

import io.github.apace100.apoli.action.EntityAction;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.util.keybinding.KeyBindingReference;
import io.github.apace100.calio.data.SerializableData;
import morgan.lesbos.network.packet.UseKeyReleasePowerTypesC2SPacket;
import morgan.lesbos.network.packet.UseParryPowerTypesS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class ActionOnParryPowerType extends PowerType {
    private final EntityAction entityAction;

    public static final TypedDataObjectFactory<ActionOnParryPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("entity_action", EntityAction.DATA_TYPE),
            (data, condition) -> new ActionOnParryPowerType(
                    data.get("entity_action"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("entity_action", powerType.entityAction)
    );


    public ActionOnParryPowerType(EntityAction entityAction, Optional<EntityCondition> condition) {
        super(condition);
        this.entityAction = entityAction;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesbosPowerTypes.ACTION_ON_PARRY;
    }


    public void onUse() {
        entityAction.execute(getHolder());
    }

    public static void triggerParryActions(PlayerEntity player) {
        if (!player.getWorld().isClient()) {
            PowerHolderComponent component = PowerHolderComponent.getNullable(player);

            if (component == null) return;

            List<Identifier> powerTypeIds = component.getPowerTypes()
                    .stream()
                    .filter(powerType -> powerType instanceof ActionOnParryPowerType)
                    .peek(powerType -> {
                        if (powerType.isActive()) {
                            ((ActionOnParryPowerType) powerType).onUse();
                        }
                    })
                    .map(PowerType::getPower)
                    .map(Power::getId)
                    .toList();

//            if (!powerTypeIds.isEmpty()) {
//                ServerPlayNetworking.send(new UseParryPowerTypesS2CPacket(powerTypeIds));
//            }
        }
    }
}