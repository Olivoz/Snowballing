package io.github.olivoz.snowballing.extend;

import net.minecraft.world.entity.projectile.Snowball;
import org.jetbrains.annotations.Nullable;

public interface EvilSnowballReferenceHack {

    @Nullable Snowball getLastHitBySnowball();

    void setLastHitBySnowball(@Nullable Snowball snowball);

}
