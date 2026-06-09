package morgan.lesbois.client.render.entity;

import morgan.lesbois.entity.GrappleHookEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GrappleHookEntityRenderer extends EntityRenderer<GrappleHookEntity> {

    public GrappleHookEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(GrappleHookEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        PlayerEntity owner = entity.getOwner();
        if (owner == null) return;

        matrices.push();

        float h = owner.getHandSwingProgress(tickDelta);
        float j = MathHelper.sin(MathHelper.sqrt(h) * (float) Math.PI);
        Vec3d ownerPos = getHandPos(owner, j, tickDelta);
        Vec3d hookPos = entity.getLerpedPos(tickDelta);

        float x = (float)(ownerPos.x - hookPos.x);
        float y = (float)(ownerPos.y - hookPos.y);
        float z = (float)(ownerPos.z - hookPos.z);

        VertexConsumer lineBuffer = vertexConsumers.getBuffer(RenderLayer.getLineStrip());
        MatrixStack.Entry entry = matrices.peek();

        int segments = 16;
        for (int i = 0; i <= segments; i++) {
            renderLine(x, y, z, lineBuffer, entry, percentage(i, segments), percentage(i + 1, segments));
        }

        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
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

    private static void renderLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        Vec3d start = new Vec3d(x * segmentStart, y * segmentStart, z * segmentStart);
        Vec3d end = new Vec3d(x * segmentEnd, y * segmentEnd, z * segmentEnd);

        Vec3d normal = new Vec3d(end.x - start.x, end.y - start.y, end.z - start.z);
        normal.normalize();

        buffer.vertex(matrices, (float) start.x, (float) start.y, (float) start.z)
                .color(Colors.BLACK)
                .normal(matrices, (float) normal.x, (float) normal.y, (float) normal.z);
    }

    private static float percentage(int value, int max) {
        return (float) value / max;
    }

    @Override
    public Identifier getTexture(GrappleHookEntity entity) {
        return Identifier.ofVanilla("textures/misc/white.png");
    }
}