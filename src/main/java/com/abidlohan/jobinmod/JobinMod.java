package com.abidlohan.jobinmod;

import com.abidlohan.jobinmod.block.ModBlocks;
import com.abidlohan.jobinmod.item.ModItems;
import com.abidlohan.jobinmod.paintings.ModPaintings;
import com.abidlohan.jobinmod.world.gen.ModOreGeneration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JobinMod.MOD_ID)
public class JobinMod {
    public static final String MOD_ID = "jobinmod";
    private static final Logger LOGGER = LogManager.getLogger();

    public JobinMod() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModPaintings.register(eventBus);

        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, ModOreGeneration::generateOres);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Starting Jobin Mod!");
    }
}