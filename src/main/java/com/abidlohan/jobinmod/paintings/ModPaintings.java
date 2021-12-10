package com.abidlohan.jobinmod.paintings;

import com.abidlohan.jobinmod.JobinMod;
import net.minecraft.client.renderer.entity.PaintingRenderer;
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModPaintings{
    public static final DeferredRegister<Motive> PAINTING_TYPES =
            DeferredRegister.create(ForgeRegistries.PAINTING_TYPES, JobinMod.MOD_ID);

    public static final RegistryObject<Motive> JOBIN_MOTOQUERO = PAINTING_TYPES.register("jobin_motoquero",
            () -> new Motive(32, 32));

    public static void register(IEventBus eventBus){
        PAINTING_TYPES.register(eventBus);
    }
}
