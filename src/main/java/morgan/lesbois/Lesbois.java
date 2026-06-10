package morgan.lesbois;

import morgan.lesbois.actions.LesboisActionTypes;
import morgan.lesbois.command.LesboisCommands;
import morgan.lesbois.component.LesboisComponentTypes;
import morgan.lesbois.conditions.LesboisConditionTypes;
import morgan.lesbois.entity.LesboisEntities;
import morgan.lesbois.entity.effect.LesboisStatusEffects;
import morgan.lesbois.network.packet.LesboisPackets;
import morgan.lesbois.powers.LesboisPowerTypes;
import morgan.lesbois.sound.LesboisSounds;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lesbois implements ModInitializer {
    public static final String MOD_ID = "lesbois";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
    public static String stringId(String path) {
        return id(path).toString();
    }

    @Override
    public void onInitialize() {
        // Minecraft registries
        LesboisPackets.register();
        LesboisEntities.register();
        LesboisCommands.register();
        LesboisSounds.register();
        LesboisComponentTypes.register();
        LesboisStatusEffects.register();

        // Apoli registries
        LesboisPowerTypes.register();
        LesboisConditionTypes.register();
        LesboisActionTypes.register();

        LOGGER.info("Lesbois initialised!!!!!");
    }
}
