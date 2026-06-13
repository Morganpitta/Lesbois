package morgan.lesbois.mixin.common.entity.player;

import morgan.lesbois.block.FrostBlock;
import morgan.lesbois.block.LesboisBlocks;
import morgan.lesbois.entity.effect.LesboisStatusEffects;
import morgan.lesbois.interfaces.ParryInterface;
import morgan.lesbois.network.packet.FrostGlideC2SPacket;
import morgan.lesbois.powers.FrostGlidingPowerType;
import morgan.lesbois.powers.ParryPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static morgan.lesbois.powers.ActionOnParryPowerType.triggerParryActions;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ParryInterface {
    @Shadow
    public abstract ItemCooldownManager getItemCooldownManager();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public boolean lesbois$canParry() {
        return ParryPowerType.canParry((PlayerEntity) (Object) this) && !this.hasStatusEffect(LesboisStatusEffects.FALTERED);
    }

    public boolean lesbois$isParrying() {
        if (this.isUsingItem() && !this.activeItemStack.isEmpty()) {
            Item item = this.activeItemStack.getItem();
            if (item instanceof SwordItem || item instanceof AxeItem) {
                return item.getUseAction(this.activeItemStack) == UseAction.BLOCK;
            }
        }
        return false;
    }

    @Unique
    public boolean canParryDamage(DamageSource source) {
        Entity entity = source.getSource();
        if (entity instanceof PersistentProjectileEntity persistentProjectileEntity && persistentProjectileEntity.getPierceLevel() > 0) {
            return true;
        }

        Vec3d vec3d = source.getPosition();
        if (vec3d != null) {
            Vec3d vec3d2 = this.getRotationVector(0.0F, this.getHeadYaw());
            Vec3d vec3d3 = vec3d.relativize(this.getPos());
            vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z).normalize();
            return vec3d3.dotProduct(vec3d2) < 0.0;
        }

        return false;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void parryDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (this.lesbois$isParrying()) {
            if (!source.isIn(DamageTypeTags.BYPASSES_ARMOR) && canParryDamage(source)) {
                this.getItemCooldownManager().set(this.activeItemStack.getItem(), 10);
                this.clearActiveItem();

                if (!source.isIn(DamageTypeTags.IS_PROJECTILE) && source.getAttacker() instanceof LivingEntity entity) {
                    entity.takeKnockback(0.5F, this.getX() - entity.getX(), this.getZ() - entity.getZ());
                    entity.addStatusEffect(new StatusEffectInstance(LesboisStatusEffects.FALTERED, 40, 0));
                }

                this.getWorld().playSound(
                        null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BLOCK_ANVIL_PLACE,
                        SoundCategory.PLAYERS,
                        0.5F, 1.8F
                );

                triggerParryActions((PlayerEntity) (Object) this, source, amount);

                cir.setReturnValue(false);
            }
        }
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
