package morgan.lesbois.cardinalComponents;

import morgan.lesbois.Lesbois;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class LesboisEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<DoubleJumpComponent> DOUBLE_JUMP = getOrCreate("double_jumps", DoubleJumpComponent.class);
    public static final ComponentKey<PossessionComponent> POSSESSION = getOrCreate("possession", PossessionComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, DOUBLE_JUMP, DoubleJumpComponent::new);
        registry.registerFor(PlayerEntity.class, POSSESSION, PossessionComponent::new);
    }

    public static <T extends Component> ComponentKey<T> getOrCreate(String path, Class<T> component){
        return ComponentRegistryV3.INSTANCE.getOrCreate(Lesbois.id(path), component);
    }
}