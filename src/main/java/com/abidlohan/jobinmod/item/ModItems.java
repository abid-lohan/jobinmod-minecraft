package com.abidlohan.jobinmod.item;

import com.abidlohan.jobinmod.JobinMod;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
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
                    .food(new FoodProperties.Builder().nutrition(4).saturationMod(0.4f).build())));

    public static final RegistryObject<Item> JOBIN_SWORD = ITEMS.register("jobin_sword",
            () -> new SwordItem(ModTiers.JOBIN, 3, 6f,
                    new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));

    public static final RegistryObject<Item> JOBIN_PICKAXE = ITEMS.register("jobin_pickaxe",
            () -> new PickaxeItem(ModTiers.JOBIN, 1, 1f,
                    new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));

    public static final RegistryObject<Item> JOBIN_SHOVEL = ITEMS.register("jobin_shovel",
            () -> new ShovelItem(ModTiers.JOBIN, 1, 1f,
                    new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));

    public static final RegistryObject<Item> JOBIN_AXE = ITEMS.register("jobin_axe",
            () -> new AxeItem(ModTiers.JOBIN, 4, 2f,
                    new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));

    public static final RegistryObject<Item> JOBIN_HOE = ITEMS.register("jobin_hoe",
            () -> new HoeItem(ModTiers.JOBIN, 2, 4f,
                    new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));

//    public static final RegistryObject<Item> JOBIN_HELMET = ITEMS.register("jobin_helmet",
//            () -> new ArmorItem(ModItems.JOBIN_INGOT, 1,
//                    new Item.Properties().tab(ModCreativeModeTab.JOBIN_TAB)));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
