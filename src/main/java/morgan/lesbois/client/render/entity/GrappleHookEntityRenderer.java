package morgan.lesbois.client.render.entity;

import morgan.lesbois.entity.GrappleHookEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GrappleHookEntityRenderer extends EntityRenderer<GrappleHookEntity> {
    private final ItemRenderer itemRenderer;
    private final ItemStack swordStack = new ItemStack(Items.IRON_SWORD);

    public GrappleHookEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(GrappleHookEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        renderSword(entity, tickDelta, matrices, vertexConsumers, light);

        renderLine(entity, tickDelta, matrices, vertexConsumers);

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderSword(GrappleHookEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        float lerpedYaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
        float lerpedPitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-lerpedYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(lerpedPitch - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(135.0F));

        this.itemRenderer.renderItem(
                this.swordStack,
                ModelTransformationMode.FIXED,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                vertexConsumers,
                entity.getWorld(),
                entity.getId()
        );

        matrices.pop();
    }

    private void renderLine(GrappleHookEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        PlayerEntity owner = entity.getOwner();
        if (owner == null) return;

        float h = owner.getHandSwingProgress(tickDelta);
        float j = MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);

        // Remember entity.getLerpedPos(tickDelta) is origin for this matrix

        Vec3d handPos = owner.getLeashPos(tickDelta).subtract(entity.getLerpedPos(tickDelta));
        Vec3d hiltOffset = entity.getHiltOffset();

        matrices.push();

        VertexConsumer lineBuffer = vertexConsumers.getBuffer(RenderLayer.getLineStrip());
        MatrixStack.Entry entry = matrices.peek();

        Vec3d normal = handPos.subtract(hiltOffset).normalize();

        int segments = 16;
        for (int index = 0; index <= segments; index++) {
            float percent = (float) index / segments;

            Vec3d vec3d = hiltOffset.lerp(handPos, percent);

            lineBuffer.vertex(entry.getPositionMatrix(), (float) vec3d.x, (float) vec3d.y, (float) vec3d.z)
                    .color(Colors.BLACK)
                    .normal(entry, (float) normal.x, (float) normal.y, (float) normal.z);
        }

        matrices.pop();
    }

    @Override
    public Identifier getTexture(GrappleHookEntity entity) {
        return Identifier.ofVanilla("textures/misc/white.png");
    }
}