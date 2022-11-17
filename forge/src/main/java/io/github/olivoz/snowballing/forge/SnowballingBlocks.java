package io.github.olivoz.snowballing.forge;

import io.github.olivoz.snowballing.SnowballingMod;
import io.github.olivoz.snowballing.block.SnowPileBlock;
import lombok.experimental.UtilityClass;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@UtilityClass
public final class SnowballingBlocks {

    private static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, SnowballingMod.MOD_ID);
    public static final RegistryObject<Block> SNOWBALL_PILE = REGISTRY.register("snowball_pile", SnowPileBlock::new);

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }

}
