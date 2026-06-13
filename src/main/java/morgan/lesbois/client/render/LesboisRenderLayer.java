package morgan.lesbois.client.render;

import morgan.lesbois.block.LesboisBlocks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class LesboisRenderLayer {
    public static void register() {
        BlockRenderLayerMap.INSTANCE.putBlock(LesboisBlocks.FROST_BLOCK, RenderLayer.getTranslucent());
    }
}
