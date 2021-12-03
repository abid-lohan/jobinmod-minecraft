package com.abidlohan.jobinmod.block;

import com.abidlohan.jobinmod.JobinMod;
import com.abidlohan.jobinmod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, JobinMod.MOD_ID);

    public static final RegistryObject<Block> JOBIN_BLOCK = registerBlock("jobin_block",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(3f)));

    public static final RegistryObject<Block> JOBIN_ORE = registerBlock("jobin_ore",
            () -> new Block(BlockBehaviour.Properties.of(Material.METAL).strength(3f)));

    public static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    public static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name,
                () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
