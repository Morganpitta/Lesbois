package morgan.lesbos;

import morgan.lesbos.actions.LesbosActionTypes;
import morgan.lesbos.command.LesbosCommands;
import morgan.lesbos.entity.LesbosEntities;
import morgan.lesbos.network.packet.LesbosPackets;
import morgan.lesbos.conditions.LesbosConditionTypes;
import morgan.lesbos.powers.LesbosPowerTypes;
import morgan.lesbos.sound.LesbosSounds;
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
        // Minecraft registries
        LesbosPackets.register();
        LesbosEntities.register();
        LesbosCommands.register();
        LesbosSounds.register();

        // Apoli registries
        LesbosPowerTypes.register();
        LesbosConditionTypes.register();
        LesbosActionTypes.register();

        LOGGER.info("Lesbos initialised!!!!!");
    }
}
