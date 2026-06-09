package morgan.lesbois.client;

import morgan.lesbois.Lesbois;
import morgan.lesbois.client.render.entity.LesboisEntityRenderers;
import morgan.lesbois.network.packet.LesboisClientPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LesboisClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LesboisEntityRenderers.register();
        LesboisClientPackets.register();

        Lesbois.LOGGER.info("Lesbois Client initialised!!!!!");
    }
}