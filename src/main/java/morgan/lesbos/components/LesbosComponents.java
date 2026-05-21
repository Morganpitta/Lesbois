package morgan.lesbos.components;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import morgan.lesbos.Lesbos;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class LesbosComponents implements EntityComponentInitializer {

    public static final ComponentKey<DoubleJumpComponent> DOUBLE_JUMP =
            ComponentRegistryV3.INSTANCE.getOrCreate(Lesbos.id("double_jumps"), DoubleJumpComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class ,DOUBLE_JUMP, entity -> new DoubleJumpComponent());
    }
}
