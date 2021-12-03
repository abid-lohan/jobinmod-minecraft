package com.abidlohan.jobinmod.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeModeTab {
    public static final CreativeModeTab JOBIN_TAB = new CreativeModeTab("jobinTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.JOBIN_INGOT.get());
        }
    };
}
