package morgan.lesbois.entity;

import morgan.lesbois.item.LesboisItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class CoinEntity extends ThrownItemEntity {
    public CoinEntity(EntityType<? extends CoinEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected Item getDefaultItem() {
        return LesboisItems.COIN;
    }
}
