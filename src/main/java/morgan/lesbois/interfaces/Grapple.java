package morgan.lesbois.interfaces;

import morgan.lesbois.entity.GrappleHookEntity;
import org.jetbrains.annotations.Nullable;

public interface Grapple {
    public GrappleHookEntity lesbois$getGrappleHook();
    public void lesbois$setGrappleHook(@Nullable GrappleHookEntity hook);
    public GrappleHookEntity lesbois$grapple(float maxDistance, float minDistance, boolean disableFallDamage, float pullSpeed, float lookAssist, float damping);
    public boolean lesbois$unGrapple();
}
