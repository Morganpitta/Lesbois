package morgan.lesbois.entity;

import morgan.lesbois.Lesbois;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LesboisEntities {
    public static final EntityType<GrappleHookEntity> GRAPPLE_HOOK = register(
            "grapple_hook",
            EntityType.Builder.<GrappleHookEntity>create(GrappleHookEntity::new, SpawnGroup.MISC)
                    .dimensions(0.25f, 0.25f)
                    .maxTrackingRange(4)
                    .trackingTickInterval(1)
    );

    public static final EntityType<CoinEntity> COIN = register(
            "coin",
            EntityType.Builder.<CoinEntity>create(CoinEntity::new, SpawnGroup.MISC)
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );

    public static void register() {}

    public static <T extends Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        return Registry.register(Registries.ENTITY_TYPE, Lesbois.id(path),builder.build());
    }
}