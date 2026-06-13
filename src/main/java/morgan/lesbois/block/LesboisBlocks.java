package morgan.lesbois.block;

import morgan.lesbois.Lesbois;
import net.minecraft.block.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class LesboisBlocks {
    public static final Block FROST_BLOCK = register("frost_block", new FrostBlock(AbstractBlock.Settings.copy(Blocks.FROSTED_ICE)));

    public static void register() {
        LesboisBlockTypes.register();
    }

    public static Block register(String path, Block block) {
        return (Block) Registry.register(Registries.BLOCK, Lesbois.id(path), block);
    }
}