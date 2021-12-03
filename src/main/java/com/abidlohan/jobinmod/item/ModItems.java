package com.abidlohan.jobinmod.item;

import com.abidlohan.jobinmod.JobinMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, JobinMod.MOD_ID);

    public static final RegistryObject<Item> JOBIN_INGOT = ITEMS.register("jobin_ingot",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));
    public static final RegistryObject<Item> JOBIN_NUGGET = ITEMS.register("jobin_nugget",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));
    public static final RegistryObject<Item> RAW_JOBIN = ITEMS.register("raw_jobin",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));

    public static final RegistryObject<Item> BANANA = ITEMS.register("banana",
            () -> new Item(new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)
                    .food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4f).build())
            ));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
