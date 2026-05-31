package morgan.lesbos.mixin.client.render;

import morgan.lesbos.interfaces.PossessionInterface;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/Camera;getFocusedEntity()Lnet/minecraft/entity/Entity;"
            )
    )
    private Entity renderPreventPossessedEntityRender(Camera camera) {
        Entity focusedEntity = camera.getFocusedEntity();

        if (focusedEntity instanceof PlayerEntity) {
            MobEntity entity = ((PossessionInterface) focusedEntity).lesbos$getPossessedEntity();
            if (entity != null) {
                return entity;
            }
        }

        return camera.getFocusedEntity();
    }
}