package morgan.lesbos;

import morgan.lesbos.network.packet.LesbosC2SPackets;
import morgan.lesbos.powers.LesbosConditions;
import morgan.lesbos.powers.LesbosPowerTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lesbos implements ModInitializer {
    public static final String MOD_ID = "lesbos";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LesbosPowerTypes.register();
        LesbosC2SPackets.register();

        LesbosConditions.register();

        LOGGER.info("Lesbos initialised!!!!!");
    }
}
