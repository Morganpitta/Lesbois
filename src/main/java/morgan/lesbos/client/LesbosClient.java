package morgan.lesbos.client;

import io.github.apace100.apoli.power.type.Active;
import morgan.lesbos.Lesbos;
import morgan.lesbos.client.render.entity.GrappleHookEntityRenderer;
import morgan.lesbos.entity.LesbosEntities;
import morgan.lesbos.powers.ActionOnKeyReleasePowerType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class LesbosClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // TODO: Make LesbosEntityRenderers.java?
        EntityRendererRegistry.register(LesbosEntities.GRAPPLE_HOOK, GrappleHookEntityRenderer::new);

        // TODO: Make LesbosPowerIntegrationClient.java?
        ClientTickEvents.START_CLIENT_TICK.register(ActionOnKeyReleasePowerType::integrateCallback);

        Lesbos.LOGGER.info("Lesbos Client initialised!!!!!");
    }
}