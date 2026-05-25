package morgan.lesbos.entity;

import morgan.lesbos.Lesbos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LesbosEntities {
    public static final EntityType<GrappleHookEntity> GRAPPLE_HOOK = register(
            EntityType.Builder.<GrappleHookEntity>create(GrappleHookEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(1)
    );

    public static void register() {}

    public static <T extends Entity> EntityType<T> register(EntityType.Builder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE,Lesbos.id("grapple_hook"),builder.build());
    }
}