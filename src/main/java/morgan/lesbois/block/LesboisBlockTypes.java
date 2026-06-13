package morgan.lesbois.block;

import morgan.lesbois.Lesbois;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LesboisBlockTypes {
    public static void register() {
        Registry.register(Registries.BLOCK_TYPE, Lesbois.id("frost_block"), FrostBlock.CODEC);
    }
}
