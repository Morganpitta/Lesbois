package morgan.lesbois.component;

import morgan.lesbois.Lesbois;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.UnaryOperator;

public class LesboisComponentTypes {
    public static final ComponentType<Unit> FALTERED = register("faltered", builder -> builder.codec(Unit.CODEC).packetCodec(PacketCodec.unit(Unit.INSTANCE)));

    public static void register() {
    }


    private static <T> ComponentType<T> register(String path, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Lesbois.id(path), builderOperator.apply(ComponentType.builder()).build());
    }
}