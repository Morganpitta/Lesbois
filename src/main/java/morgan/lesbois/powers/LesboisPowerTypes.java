package morgan.lesbois.powers;

import io.github.apace100.apoli.condition.EntityCondition;
import io.github.apace100.apoli.data.TypedDataObjectFactory;
import io.github.apace100.apoli.power.PowerConfiguration;
import io.github.apace100.apoli.power.type.PowerType;
import io.github.apace100.apoli.power.type.PowerTypes;
import morgan.lesbois.Lesbois;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class LesboisPowerTypes {
    public static final PowerConfiguration<DoubleJumpPowerType> DOUBLE_JUMP = register("double_jump", DoubleJumpPowerType.DATA_FACTORY);
    public static final PowerConfiguration<ActionOnKeyReleasePowerType> ACTION_ON_KEY_RELEASE = register("action_on_key_release", ActionOnKeyReleasePowerType.DATA_FACTORY);
    public static final PowerConfiguration<DragModifierPowerType> DRAG_MODIFIER = register("drag_modifier", DragModifierPowerType.DATA_FACTORY);
    public static final PowerConfiguration<DisableHungerPowerType> DISABLE_HUNGER = registerConditionedSimple("disable_hunger", DisableHungerPowerType::new);
    public static final PowerConfiguration<ActionOnParryPowerType> ACTION_ON_PARRY = register("action_on_parry", ActionOnParryPowerType.DATA_FACTORY);
    public static final PowerConfiguration<ParryPowerType> PARRY = register("parry", ParryPowerType.DATA_FACTORY);
    public static final PowerConfiguration<ActionOnCoinPowerType> ACTION_ON_COIN = register("action_on_coin", ActionOnCoinPowerType.DATA_FACTORY);
    public static final PowerConfiguration<FrostGlidingPowerType> FROST_GLIDE = register("frost_glide", FrostGlidingPowerType.DATA_FACTORY);
    public static final PowerConfiguration<SpeedometerPowerType> SPEEDOMETER = registerConditionedSimple("speedometer", SpeedometerPowerType::new);
    public static final PowerConfiguration<ShockwavePowerType> SHOCKWAVE = register("shockwave", ShockwavePowerType.DATA_FACTORY);

    public static void register() {
    }

    public static <T extends PowerType> PowerConfiguration<T> register(String path,  TypedDataObjectFactory<T> dataFactory) {
        PowerConfiguration<T> configuration = PowerConfiguration.of(Lesbois.id(path), dataFactory);

        PowerTypes.register(configuration);

        return configuration;
    }

    public static <T extends PowerType> PowerConfiguration<T> registerSimple(String path,  Supplier<T> constructor) {
        PowerConfiguration<T> configuration = PowerConfiguration.simple(Lesbois.id(path), constructor);

        PowerTypes.register(configuration);

        return configuration;
    }

    public static <T extends PowerType> PowerConfiguration<T> registerConditionedSimple(String path, Function<Optional<EntityCondition>,T> constructor) {
        PowerConfiguration<T> configuration = PowerConfiguration.conditionedSimple(Lesbois.id(path), constructor);

        PowerTypes.register(configuration);

        return configuration;
    }
}