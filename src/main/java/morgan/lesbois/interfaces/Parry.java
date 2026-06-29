package morgan.lesbois.interfaces;

import net.minecraft.entity.damage.DamageSource;

public interface Parry {
    public boolean lesbois$canParry();
    public boolean lesbois$isParrying();
    public void lesbois$parry(DamageSource source, float amount);
    public boolean lesbois$shouldRedirectProjectile();
    public void lesbois$setRedirectProjectile(boolean value);
}
