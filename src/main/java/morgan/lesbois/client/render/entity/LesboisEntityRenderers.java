package morgan.lesbois.client.render.entity;

import morgan.lesbois.entity.LesboisEntities;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

public class LesboisEntityRenderers {
    public static void register() {
        register(LesboisEntities.GRAPPLE_HOOK, GrappleHookEntityRenderer::new);
    }

    private static <T extends Entity> void register(EntityType<T> entityType, EntityRendererFactory<T> entityRendererFactory) {
        EntityRendererRegistry.register(entityType, entityRendererFactory );
    }
}