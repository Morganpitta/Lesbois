package morgan.lesbois.interfaces;

public interface Winged {
    public boolean lesbois$isFlying();
    public void lesbois$setFlying(boolean value);

    public float lesbois$getWingAngle();
    public float lesbois$getPrevWingAngle();
    public float lesbois$getWingDistance();
    public float lesbois$getPrevWingDistance();
    public void lesbois$updateWings();
}