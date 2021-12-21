package com.abidlohan.jobinmod.world.gen;

import com.abidlohan.jobinmod.JobinMod;
import com.abidlohan.jobinmod.block.ModBlocks;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ModOreGeneration{

    public static final List<PlacedFeature> OVERWORLD_ORES = new ArrayList<>();

    public static void generateOres(final BiomeLoadingEvent event){
        final ConfiguredFeature<?,?> jobinOre = FeatureUtils.register("jobin_ore", Feature.ORE.configured(new OreConfiguration(
                List.of(
                        OreConfiguration.target(
                                OreFeatures.STONE_ORE_REPLACEABLES, ModBlocks.JOBIN_ORE.get().defaultBlockState()
                        ),
                        OreConfiguration.target(
                                OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_JOBIN_ORE.get().defaultBlockState()
                        )
                ), 3)));

        final PlacedFeature placedJobinOre = PlacementUtils.register("jobin_ore",
                jobinOre.placed(HeightRangePlacement.triangle(VerticalAnchor.bottom(),VerticalAnchor.aboveBottom(64)),
                        InSquarePlacement.spread(), CountPlacement.of(1)));

        OVERWORLD_ORES.add(placedJobinOre);
    }

    @Mod.EventBusSubscriber(modid = JobinMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusSubscriber{
        @SubscribeEvent
        public static void biomeLoading(BiomeLoadingEvent event){
            List<Supplier<PlacedFeature>> features = event.getGeneration().getFeatures(GenerationStep.Decoration.UNDERGROUND_ORES);

            if(event.getCategory() != Biome.BiomeCategory.THEEND && event.getCategory() != Biome.BiomeCategory.NETHER){
                ModOreGeneration.OVERWORLD_ORES.forEach(ore -> features.add(() -> ore));
            }
        }
    }

}
