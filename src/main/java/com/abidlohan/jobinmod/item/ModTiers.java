package com.abidlohan.jobinmod.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeTier;
import net.minecraftforge.common.Tags;

public class ModTiers {
    public static final ForgeTier JOBIN = new ForgeTier(4, 3000, 12.0f, 4.0f, 15, Tags.Blocks.NEEDS_GOLD_TOOL, () -> {
        return Ingredient.of(ModItems.JOBIN_INGOT.get());
    });
}
