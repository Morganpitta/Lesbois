package morgan.lesbos.client;

import morgan.lesbos.Lesbos;
import morgan.lesbos.client.render.entity.GrappleHookEntityRenderer;
import morgan.lesbos.entity.LesbosEntities;
import morgan.lesbos.network.packet.LesbosClientPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class LesbosClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // TODO: Make LesbosEntityRenderers.java?
        EntityRendererRegistry.register(LesbosEntities.GRAPPLE_HOOK, GrappleHookEntityRenderer::new);
        LesbosClientPackets.register();

        Lesbos.LOGGER.info("Lesbos Client initialised!!!!!");
    }
}