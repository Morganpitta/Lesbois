package morgan.lesbois.mixin.common.entity.player;

import morgan.lesbois.block.FrostBlock;
import morgan.lesbois.block.LesboisBlocks;
import morgan.lesbois.interfaces.ParryInterface;
import morgan.lesbois.network.packet.FrostGlideC2SPacket;
import morgan.lesbois.powers.FrostGlidingPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SkinOverlayOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void travelFrostGliding(Vec3d movementInput, CallbackInfo ci) {
        if (FrostGlidingPowerType.shouldFrostGlide((PlayerEntity) (Object) this)) {
            if (this.isSpectator() || !this.getWorld().isClient()) return;

            BlockPos blockPos = this.getBlockPos();
            BlockState blockState = this.getWorld().getBlockState(blockPos);

            if (blockState.isAir()) {
                BlockState floorState = this.getWorld().getBlockState(blockPos.down());

                if (FrostBlock.canReplace(floorState)) {
                    BlockState frostBlockState = LesboisBlocks.FROST_BLOCK.getDefaultState();

                    if (floorState.isOf(Blocks.WATER)) {
                        frostBlockState = frostBlockState.with(Properties.WATERLOGGED, true);
                    }

                    this.getWorld().setBlockState(blockPos.down(), frostBlockState, 3);

                    ClientPlayNetworking.send(new FrostGlideC2SPacket(blockPos.down()));
                }
            }
        }
    }
}
