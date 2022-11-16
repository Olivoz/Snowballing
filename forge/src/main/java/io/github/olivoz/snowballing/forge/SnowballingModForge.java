package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import net.minecraftforge.fml.common.Mod;

@Mod(SnowballingMod.MOD_ID)
public class SnowballingModForge {
    public SnowballingModForge() {
        SnowballingMod.init();
    }
}
