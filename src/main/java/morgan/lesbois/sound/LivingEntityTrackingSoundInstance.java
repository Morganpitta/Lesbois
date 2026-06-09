package morgan.lesbois.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;

public class LivingEntityTrackingSoundInstance extends MovingSoundInstance {
    private final LivingEntity entity;

    public LivingEntityTrackingSoundInstance(SoundEvent sound, SoundCategory category, float volume, float pitch, LivingEntity entity, long seed) {
        super(sound, category, Random.create(seed));
        this.volume = volume;
        this.pitch = pitch;
        this.entity = entity;
        this.x = (float)this.entity.getX();
        this.y = (float)this.entity.getY();
        this.z = (float)this.entity.getZ();
    }

    @Override
    public boolean canPlay() {
        return !this.entity.isSilent();
    }

    @Override
    public void tick() {
        if (this.entity.isDead() || this.entity.isRemoved()) {
            this.setDone();
        } else {
            this.x = (float)this.entity.getX();
            this.y = (float)this.entity.getY();
            this.z = (float)this.entity.getZ();
        }
    }
}
