package morgan.lesbos.components;

import morgan.lesbos.Lesbos;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;

public class LesbosComponents implements EntityComponentInitializer {

    public static final ComponentKey<DoubleJumpComponent> DOUBLE_JUMP =
            ComponentRegistryV3.INSTANCE.getOrCreate(Lesbos.id("double_jumps"), DoubleJumpComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, DOUBLE_JUMP, entity -> new DoubleJumpComponent());
    }
}
