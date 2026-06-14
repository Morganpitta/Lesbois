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
        renderSword(entity, matrices, vertexConsumers, light);

        renderLine(entity, tickDelta, matrices, vertexConsumers);

        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    private void renderSword(GrappleHookEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        matrices.push();

        matrices.multiply(entity.getSide().getRotationQuaternion());

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

    private Vec3d getHandPos(PlayerEntity player, float f, float tickDelta) {
        int i = player.getMainArm() == Arm.RIGHT ? 1 : -1;

        if (this.dispatcher.gameOptions.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double m = 960.0 / this.dispatcher.gameOptions.getFov().getValue().intValue();
            Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition(i * 0.525F, -0.1F).multiply(m).rotateY(f * 0.5F).rotateX(-f * 0.7F);
            return player.getCameraPosVec(tickDelta).add(vec3d);
        } else {
            float g = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
            double d = MathHelper.sin(g);
            double e = MathHelper.cos(g);
            float h = player.getScale();
            double j = i * 0.35 * h;
            double k = 0.8 * h;
            float l = player.isInSneakingPose() ? -0.1875F : 0.0F;
            return player.getCameraPosVec(tickDelta).add(-e * j - d * k, l - 0.45 * h, -d * j + e * k);
        }
    }

    private void renderLine(GrappleHookEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        PlayerEntity owner = entity.getOwner();
        if (owner == null) return;

        float h = owner.getHandSwingProgress(tickDelta);
        float j = MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);

        // Remember entity.getLerpedPos(tickDelta) is origin for this matrix

        Vec3d handPos = getHandPos(owner, j, tickDelta).subtract(entity.getLerpedPos(tickDelta));
        Vec3d hiltOffset = new Vec3d(entity.getSide().getUnitVector()).multiply(0.55);

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