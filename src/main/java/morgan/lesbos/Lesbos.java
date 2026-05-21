package morgan.lesbos;

import morgan.lesbos.powers.LesbosPowers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lesbos implements ModInitializer {
    public static final String MOD_ID = "lesbos";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LesbosPowers.init();

        LOGGER.info("Lesbos initialised!!!!!");
    }
}
