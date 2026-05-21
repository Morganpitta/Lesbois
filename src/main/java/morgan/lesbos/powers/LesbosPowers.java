package morgan.lesbos.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import morgan.lesbos.Lesbos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LesbosPowers {
    public static final PowerFactory<Power> DOUBLE_JUMP =
            new PowerFactory<>(
                Lesbos.id("double_jump"),
                new SerializableData()
                        .add("double_jumps", SerializableDataTypes.INT, 1),
                data -> (type, player) -> new DoubleJumpPower(type, player, data.getInt("double_jumps"))
            ).allowCondition();


    public static void init() {
        Registry.register(ApoliRegistries.POWER_FACTORY, DOUBLE_JUMP.getSerializerId(), DOUBLE_JUMP);
    }
}
