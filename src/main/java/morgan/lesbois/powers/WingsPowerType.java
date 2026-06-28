package morgan.lesbois.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WingsPowerType extends PowerType {
    private final float speed;
    private final float boost;
    private final Identifier texture;

    public static final TypedDataObjectFactory<WingsPowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
            new SerializableData()
                    .add("speed", SerializableDataTypes.FLOAT, 0.15F)
                    .add("boost", SerializableDataTypes.FLOAT, 0.025F)
                    .add("texture", SerializableDataTypes.IDENTIFIER),
            (data, condition) -> new WingsPowerType(
                    data.get("speed"),
                    data.get("boost"),
                    data.get("texture"),
                    condition
            ),
            (powerType, serializableData) -> serializableData.instance()
                    .set("speed", powerType.speed)
                    .set("boost", powerType.boost)
                    .set("texture", powerType.texture)
    );

    WingsPowerType(float speed, float boost, Identifier texture, Optional<EntityCondition> condition) {
        super(condition);
        this.speed = speed;
        this.boost = boost;
        this.texture = texture;
    }

    @Override
    public @NotNull PowerConfiguration<?> getConfig() {
        return LesboisPowerTypes.WINGS;
    }

    public static boolean hasWings(PlayerEntity player) {
        return PowerHolderComponent.hasPowerType (player, WingsPowerType.class);
    }

    public static float getSpeed(PlayerEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.speed).max().orElse(0);
    }

    public static float getBoost(PlayerEntity player) {
        return (float) PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .mapToDouble(powerType -> powerType.boost).max().orElse(0);
    }

    public static Identifier getTexture(PlayerEntity player) {
        return PowerHolderComponent.getPowerTypes(player, WingsPowerType.class).stream()
                .map(powerType -> powerType.texture).findFirst().orElse(null);
    }
}