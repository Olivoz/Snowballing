package io.github.olivoz.snowballing.extend;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface PointTracker {

    @Nullable LivingEntity getEnemy();

    long getLastRewardDrop();

    void setLastRewardDrop(long timestamp);

    int getPoints();

    void setPoints(int points);

}
