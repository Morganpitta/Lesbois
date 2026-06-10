package morgan.lesbois.component;

import morgan.lesbois.Lesbois;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class LesboisComponentTypes {
    public static void register() {
    }


    private static <T> ComponentType<T> register(String path, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Lesbois.id(path), builderOperator.apply(ComponentType.builder()).build());
    }
}