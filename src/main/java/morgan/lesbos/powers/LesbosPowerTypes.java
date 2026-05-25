package morgan.lesbos.powers;

import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.power.type.PowerTypes;
import morgan.lesbos.Lesbos;

public class LesbosPowerTypes {
    public static final PowerConfiguration<DoubleJumpPowerType> DOUBLE_JUMP = register("double_jump", DoubleJumpPowerType.DATA_FACTORY);
    public static final PowerConfiguration<ActionOnKeyReleasePowerType> ACTION_ON_KEY_RELEASE = register("action_on_key_release", ActionOnKeyReleasePowerType.DATA_FACTORY);

    public static void register() {
    }

    public static <T extends PowerType> PowerConfiguration<T> register(String path,  TypedDataObjectFactory<T> dataFactory) {
        PowerConfiguration<T> configuration = PowerConfiguration.of(Lesbos.id(path), dataFactory);

        PowerTypes.register(configuration);

        return configuration;
    }
}
