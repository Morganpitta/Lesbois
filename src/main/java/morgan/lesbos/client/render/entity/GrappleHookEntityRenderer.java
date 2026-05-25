package morgan.lesbos.client.render.entity;

import morgan.lesbos.Lesbos;
import morgan.lesbos.entity.GrappleHookEntity;
import morgan.lesbos.entity.LesbosEntities;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GrappleHookEntityRenderer extends EntityRenderer<GrappleHookEntity> {

    public GrappleHookEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(GrappleHookEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        Lesbos.LOGGER.info("rendering grapple hook");

        PlayerEntity owner = (PlayerEntity) entity.getOwner();
        if (owner == null) return;

        matrices.push();

        Vec3d ownerPos = owner.getLerpedPos(tickDelta).add(0, owner.getEyeHeight(owner.getPose()), 0);
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

    private static void renderLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        Vec3d start = new Vec3d(x * segmentStart, y * segmentStart, z * segmentStart);
        Vec3d end = new Vec3d(x * segmentEnd, y * segmentEnd, z * segmentEnd);

        Vec3d normal = new Vec3d(end.x - start.x, end.y - start.y, end.z - start.z);
        normal.normalize();

        buffer.vertex(matrices, (float) start.x, (float) start.y, (float) start.z)
                .color(Colors.WHITE)
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