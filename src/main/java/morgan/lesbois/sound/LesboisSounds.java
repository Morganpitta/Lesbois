package morgan.lesbois.sound;

import morgan.lesbois.Lesbois;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

public class LesboisSounds {
    public static final SoundEvent SONIC_BOOM = register("sonic_boom");
    public static final SoundEvent PATRICK_SCREAM = register("patrick_scream");

    public static void register() {
    }

    private static SoundEvent register(String path) {
        return Registry.register(Registries.SOUND_EVENT, Lesbois.id(path), SoundEvent.of(Lesbois.id(path)));
    }
}